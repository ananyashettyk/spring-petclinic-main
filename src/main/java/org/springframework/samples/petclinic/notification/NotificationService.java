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

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.NotificationPreference;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing notification schedules.
 *
 * @author Claude
 */
@Service
public class NotificationService {

	private final NotificationScheduleRepository notificationScheduleRepository;

	@Autowired
	public NotificationService(NotificationScheduleRepository notificationScheduleRepository) {
		this.notificationScheduleRepository = notificationScheduleRepository;
	}

	/**
	 * Create a new notification schedule for a visit.
	 * @param visit the visit
	 * @param pet the pet
	 * @param notificationPreference the notification preference
	 * @param scheduledTime the scheduled time
	 * @param message the message
	 * @return the created notification schedule
	 */
	@Transactional
	public NotificationSchedule scheduleNotification(Visit visit, Pet pet,
			NotificationPreference notificationPreference, LocalDateTime scheduledTime, String message) {
		NotificationSchedule notificationSchedule = new NotificationSchedule();
		notificationSchedule.setVisit(visit);
		notificationSchedule.setPet(pet);
		notificationSchedule.setNotificationPreference(notificationPreference);
		notificationSchedule.setScheduledTime(scheduledTime);
		notificationSchedule.setMessage(message);

		this.notificationScheduleRepository.save(notificationSchedule);
		return notificationSchedule;
	}

	/**
	 * Find a notification schedule by its id.
	 * @param notificationId the notification id
	 * @return the notification schedule
	 */
	@Transactional(readOnly = true)
	public NotificationSchedule findNotificationById(int notificationId) {
		return this.notificationScheduleRepository.findById(notificationId);
	}

	/**
	 * Find all pending notifications.
	 * @return a collection of pending notifications
	 */
	@Transactional(readOnly = true)
	public Collection<NotificationSchedule> findPendingNotifications() {
		return this.notificationScheduleRepository.findAllPendingNotifications();
	}

	/**
	 * Find notifications for a specific visit.
	 * @param visitId the visit id
	 * @return a collection of notifications for the visit
	 */
	@Transactional(readOnly = true)
	public Collection<NotificationSchedule> findNotificationsByVisitId(int visitId) {
		return this.notificationScheduleRepository.findByVisitId(visitId);
	}

	/**
	 * Find notifications for a specific pet.
	 * @param petId the pet id
	 * @return a collection of notifications for the pet
	 */
	@Transactional(readOnly = true)
	public Collection<NotificationSchedule> findNotificationsByPetId(int petId) {
		return this.notificationScheduleRepository.findByPetId(petId);
	}

	/**
	 * Update the status of a notification.
	 * @param notificationId the notification id
	 * @param status the new status
	 * @return the updated notification schedule
	 */
	@Transactional
	public NotificationSchedule updateNotificationStatus(int notificationId, NotificationStatus status) {
		NotificationSchedule notificationSchedule = findNotificationById(notificationId);
		if (notificationSchedule != null) {
			notificationSchedule.setStatus(status);
			this.notificationScheduleRepository.save(notificationSchedule);
		}
		return notificationSchedule;
	}

}
