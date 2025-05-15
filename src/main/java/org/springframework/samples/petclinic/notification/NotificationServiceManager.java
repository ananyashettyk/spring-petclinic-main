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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.NotificationPreference;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Service;

/**
 * Manager service that coordinates sending notifications through appropriate
 * NotificationService implementations based on owner preferences.
 *
 * @author Claude
 */
@Service
public class NotificationServiceManager {

	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceManager.class);

	private final List<NotificationService> notificationServices;

	@Autowired
	public NotificationServiceManager(List<NotificationService> notificationServices) {
		this.notificationServices = notificationServices;
	}

	/**
	 * Sends a notification using the appropriate service(s) based on owner preferences.
	 * @param notificationSchedule the notification to send
	 * @param owner the owner to notify
	 * @return true if at least one notification was sent successfully
	 */
	public boolean sendNotification(NotificationSchedule notificationSchedule, Owner owner) {
		if (owner == null) {
			logger.error("Cannot send notification: owner is null");
			return false;
		}

		if (notificationSchedule == null) {
			logger.error("Cannot send notification: notificationSchedule is null");
			return false;
		}

		// Skip if owner has opted out of notifications
		if (owner.getNotificationPreference() == NotificationPreference.NONE) {
			logger.info("Owner {} has opted out of notifications", owner.getId());
			notificationSchedule.setStatus(NotificationStatus.SKIPPED);
			return false;
		}

		boolean anySuccess = false;

		// Try each notification service that can handle this notification
		for (NotificationService service : notificationServices) {
			if (service.canHandle(notificationSchedule, owner)) {
				boolean success = service.sendNotification(notificationSchedule, owner);
				anySuccess = anySuccess || success;
			}
		}

		if (!anySuccess) {
			logger.warn("No notification services were able to successfully send the notification");
			notificationSchedule.setStatus(NotificationStatus.FAILED);
		}

		return anySuccess;
	}

	/**
	 * Process all pending notifications in the schedule.
	 * @param notificationSchedules list of pending notification schedules
	 * @return number of successfully sent notifications
	 */
	public int processNotifications(List<NotificationSchedule> notificationSchedules, List<Owner> owners) {
		int sentCount = 0;

		for (NotificationSchedule schedule : notificationSchedules) {
			// Find the owner for this pet
			Owner owner = findOwnerForPet(schedule.getPet().getId(), owners);

			if (owner != null) {
				boolean sent = sendNotification(schedule, owner);
				if (sent) {
					sentCount++;
				}
			}
			else {
				logger.warn("Could not find owner for pet ID: {}", schedule.getPet().getId());
				schedule.setStatus(NotificationStatus.FAILED);
			}
		}

		return sentCount;
	}

	/**
	 * Helper method to find the owner for a given pet ID
	 */
	private Owner findOwnerForPet(Integer petId, List<Owner> owners) {
		for (Owner owner : owners) {
			if (owner.getPet(petId) != null) {
				return owner;
			}
		}
		return null;
	}

}
