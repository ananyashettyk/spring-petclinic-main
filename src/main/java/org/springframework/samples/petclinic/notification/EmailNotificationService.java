/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.notification;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.samples.petclinic.model.NotificationPreference;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of NotificationService that sends email notifications using JavaMail.
 *
 * @author Claude
 */
@Service
public class EmailNotificationService implements NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

	@Value("${spring.mail.host:smtp.example.com}")
	private String smtpHost;

	@Value("${spring.mail.port:587}")
	private String smtpPort;

	@Value("${spring.mail.username:username}")
	private String username;

	@Value("${spring.mail.password:password}")
	private String password;

	@Value("${petclinic.notification.from-email:noreply@petclinic.org}")
	private String fromEmail;

	@Override
	public boolean sendNotification(NotificationSchedule notificationSchedule, Owner owner) {
		if (!canHandle(notificationSchedule, owner)) {
			logger.debug("Email notification service cannot handle this notification for owner: {}", owner.getId());
			return false;
		}

		String email = owner.getEmail();
		if (!StringUtils.hasText(email)) {
			logger.warn("Cannot send email notification: owner {} has no email address", owner.getId());
			notificationSchedule.setStatus(NotificationStatus.FAILED);
			return false;
		}

		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", smtpPort);

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

			// Create subject line based on pet name and visit type
			String petName = notificationSchedule.getPet().getName();
			String visitDesc = notificationSchedule.getVisit().getDescription();
			message.setSubject("Pet Clinic Reminder: " + petName + "'s " + visitDesc);

			// Use the message from notification schedule or create a default one
			String messageBody = notificationSchedule.getMessage();
			if (!StringUtils.hasText(messageBody)) {
				messageBody = createDefaultMessage(notificationSchedule, owner);
			}

			message.setText(messageBody);

			Transport.send(message);

			notificationSchedule.setStatus(NotificationStatus.SENT);
			logger.info("Email notification sent successfully to {}", email);
			return true;

		}
		catch (MessagingException e) {
			logger.error("Failed to send email notification to {}: {}", email, e.getMessage());
			notificationSchedule.setStatus(NotificationStatus.FAILED);
			return false;
		}
	}

	@Override
	public boolean canHandle(NotificationSchedule notificationSchedule, Owner owner) {
		NotificationPreference preference = notificationSchedule.getNotificationPreference();
		if (preference == null) {
			// Fall back to owner's preference if schedule doesn't specify
			preference = owner.getNotificationPreference();
		}

		return preference == NotificationPreference.EMAIL || preference == NotificationPreference.BOTH;
	}

	/**
	 * Creates a default notification message if one isn't provided
	 */
	private String createDefaultMessage(NotificationSchedule notificationSchedule, Owner owner) {
		String petName = notificationSchedule.getPet().getName();
		String ownerName = owner.getFirstName() + " " + owner.getLastName();
		String visitDate = notificationSchedule.getVisit().getDate().toString();
		String visitDesc = notificationSchedule.getVisit().getDescription();

		return "Dear " + ownerName + ",\n\n" + "This is a reminder that your pet " + petName + " has a " + visitDesc
				+ " scheduled on " + visitDate + ".\n\n" + "Please contact us if you need to reschedule.\n\n"
				+ "Regards,\nThe Pet Clinic Team";
	}

}
