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

import org.springframework.samples.petclinic.owner.Owner;

/**
 * Interface for notification services in the pet clinic application. Defines methods for
 * sending notifications to pet owners.
 *
 * @author Claude
 */
public interface NotificationService {

	/**
	 * Sends a notification based on the provided NotificationSchedule. The implementation
	 * should determine how to send the notification based on Owner preferences and the
	 * provided NotificationSchedule details.
	 * @param notificationSchedule the notification schedule containing message and timing
	 * information
	 * @param owner the pet owner who should receive the notification
	 * @return true if the notification was sent successfully, false otherwise
	 */
	boolean sendNotification(NotificationSchedule notificationSchedule, Owner owner);

	/**
	 * Checks if this notification service can handle the given notification preference.
	 * @param notificationSchedule the notification schedule to check
	 * @param owner the pet owner with notification preferences
	 * @return true if this service can handle the notification, false otherwise
	 */
	boolean canHandle(NotificationSchedule notificationSchedule, Owner owner);

}
