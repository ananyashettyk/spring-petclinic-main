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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NotificationPreference;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Simple JavaBean domain object representing a notification schedule for a visit.
 *
 * @author Claude
 */
@Entity
@Table(name = "notification_schedules")
public class NotificationSchedule extends BaseEntity {

	@Column(name = "notification_preference")
	@Enumerated(EnumType.STRING)
	@NotNull
	private NotificationPreference notificationPreference;

	@Column(name = "scheduled_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private LocalDateTime scheduledTime;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	@NotNull
	private NotificationStatus status;

	@ManyToOne
	@JoinColumn(name = "visit_id")
	@NotNull
	private Visit visit;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	@NotNull
	private Pet pet;

	@Column(name = "message")
	private String message;

	/**
	 * Creates a new instance of NotificationSchedule with default status as PENDING
	 */
	public NotificationSchedule() {
		this.status = NotificationStatus.PENDING;
	}

	public NotificationPreference getNotificationPreference() {
		return this.notificationPreference;
	}

	public void setNotificationPreference(NotificationPreference notificationPreference) {
		this.notificationPreference = notificationPreference;
	}

	public LocalDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public NotificationStatus getStatus() {
		return this.status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public Visit getVisit() {
		return this.visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public Pet getPet() {
		return this.pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
