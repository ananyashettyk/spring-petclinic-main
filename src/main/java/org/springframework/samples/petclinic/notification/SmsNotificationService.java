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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.samples.petclinic.model.NotificationPreference;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.exception.TwilioException;

/**
 * Implementation of NotificationService that sends SMS notifications using Twilio.
 *
 * @author Claude
 */
@Service
public class SmsNotificationService implements NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

	@Value("${twilio.account.sid:accountSid}")
	private String accountSid;

	@Value("${twilio.auth.token:authToken}")
	private String authToken;

	@Value("${twilio.phone.number:+15551234567}")
	private String twilioPhoneNumber;

	@Override
	public boolean sendNotification(NotificationSchedule notificationSchedule, Owner owner) {
		if (!canHandle(notificationSchedule, owner)) {
			logger.debug("SMS notification service cannot handle this notification for owner: {}", owner.getId());
			return false;
		}

		String phoneNumber = owner.getTelephone();
		if (!StringUtils.hasText(phoneNumber)) {
			logger.warn("Cannot send SMS notification: owner {} has no phone number", owner.getId());
			notificationSchedule.setStatus(NotificationStatus.FAILED);
			return false;
		}

		try {
			// Initialize Twilio client
			Twilio.init(accountSid, authToken);

			// Format phone number (assuming US for simplicity - in production, handle
			// international formats)
			String formattedPhoneNumber = "+1" + phoneNumber;

			// Use the message from notification schedule or create a default one
			String messageBody = notificationSchedule.getMessage();
			if (!StringUtils.hasText(messageBody)) {
				messageBody = createDefaultMessage(notificationSchedule, owner);
			}

			// Send the SMS
			Message twilioMessage = Message
				.creator(new PhoneNumber(formattedPhoneNumber), new PhoneNumber(twilioPhoneNumber), messageBody)
				.create();

			// Check the message status
			String status = twilioMessage.getStatus().toString();
			logger.info("SMS sent with status: {} to {}", status, phoneNumber);

			// Update notification status
			notificationSchedule.setStatus(NotificationStatus.SENT);
			return true;

		}
		catch (TwilioException e) {
			logger.error("Failed to send SMS notification to {}: {}", phoneNumber, e.getMessage());
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

		return preference == NotificationPreference.SMS || preference == NotificationPreference.BOTH;
	}

	/**
	 * Creates a default notification message if one isn't provided
	 */
	private String createDefaultMessage(NotificationSchedule notificationSchedule, Owner owner) {
		String petName = notificationSchedule.getPet().getName();
		String visitDate = notificationSchedule.getVisit().getDate().toString();
		String visitDesc = notificationSchedule.getVisit().getDescription();

		// SMS should be short and concise
		return "Pet Clinic Reminder: " + petName + " has a " + visitDesc + " on " + visitDate
				+ ". Reply HELP for assistance or STOP to unsubscribe.";
	}

}
