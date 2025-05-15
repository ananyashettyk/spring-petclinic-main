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

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository class for <code>NotificationSchedule</code> domain objects.
 *
 * @author Claude
 */
public interface NotificationScheduleRepository extends Repository<NotificationSchedule, Integer> {

	/**
	 * Retrieve a {@link NotificationSchedule} by its id.
	 * @param id the id to search for
	 * @return the notification schedule if found
	 */
	NotificationSchedule findById(Integer id);

	/**
	 * Save a {@link NotificationSchedule} to the data store.
	 * @param notificationSchedule the notification schedule to save
	 */
	void save(NotificationSchedule notificationSchedule);

	/**
	 * Find all pending notification schedules.
	 * @return a Collection of pending notification schedules
	 */
	@Query("SELECT ns FROM NotificationSchedule ns WHERE ns.status = org.springframework.samples.petclinic.notification.NotificationStatus.PENDING")
	Collection<NotificationSchedule> findAllPendingNotifications();

	/**
	 * Find notification schedules for a specific visit.
	 * @param visitId the visit id to search for
	 * @return a Collection of notification schedules for the visit
	 */
	@Query("SELECT ns FROM NotificationSchedule ns WHERE ns.visit.id = :visitId")
	Collection<NotificationSchedule> findByVisitId(@Param("visitId") Integer visitId);

	/**
	 * Find notification schedules for a specific pet.
	 * @param petId the pet id to search for
	 * @return a Collection of notification schedules for the pet
	 */
	@Query("SELECT ns FROM NotificationSchedule ns WHERE ns.pet.id = :petId")
	Collection<NotificationSchedule> findByPetId(@Param("petId") Integer petId);

}
