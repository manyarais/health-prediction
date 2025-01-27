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
package org.springframework.samples.petclinic.patient;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Manya Raisinghani
 */
@Entity
@Table(name = "patient")
public class Patient extends BaseEntity {

	@Column(name = "user_id")
	@NotBlank
	private String userId;

	@Column(name = "date_of_birth")
	private java.sql.Date dateOfBirth;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "have_hypertension")
	private Boolean haveHypertension;

	@Column(name = "ever_married")
	private Boolean everMarried;

	@Column(name = "have_diabetes")
	private Boolean haveDiabetes;

	@Column(name = "had_stroke")
	private Boolean hadStroke;

	@Column(name = "have_heart_disease")
	private Boolean haveHeartDisease;

	@Column(name = "bmi")
	private BigDecimal bmi;

	@Column(name = "address")
	private String address;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "smoking_status")
	private SmokingStatus smokingStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "residence_type")
	private ResidenceType residenceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "work_type")
	private WorkType workType;

	@Column(name = "recommendations")
	private String recommendations;

	@Column(name = "registration_date")
	private Timestamp registrationDate;

	// Getters and setters for all fields

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public java.sql.Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(java.sql.Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Boolean getHaveHypertension() {
		return haveHypertension;
	}

	public void setHaveHypertension(Boolean haveHypertension) {
		this.haveHypertension = haveHypertension;
	}

	public Boolean getEverMarried() {
		return everMarried;
	}

	public void setEverMarried(Boolean everMarried) {
		this.everMarried = everMarried;
	}

	public Boolean getHaveDiabetes() {
		return haveDiabetes;
	}

	public void setHaveDiabetes(Boolean haveDiabetes) {
		this.haveDiabetes = haveDiabetes;
	}

	public Boolean getHadStroke() {
		return hadStroke;
	}

	public void setHadStroke(Boolean hadStroke) {
		this.hadStroke = hadStroke;
	}

	public Boolean getHaveHeartDisease() {
		return haveHeartDisease;
	}

	public void setHaveHeartDisease(Boolean haveHeartDisease) {
		this.haveHeartDisease = haveHeartDisease;
	}

	public BigDecimal getBmi() {
		return bmi;
	}

	public void setBmi(BigDecimal bmi) {
		this.bmi = bmi;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SmokingStatus getSmokingStatus() {
		return smokingStatus;
	}

	public void setSmokingStatus(SmokingStatus smokingStatus) {
		this.smokingStatus = smokingStatus;
	}

	public ResidenceType getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(ResidenceType residenceType) {
		this.residenceType = residenceType;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	// Calculates age based on the date of birth
	public int getAge() {
		if (this.getDateOfBirth() == null) {
			return 0;
		}
		LocalDate birthDate = this.getDateOfBirth().toLocalDate();
		LocalDate today = LocalDate.now();
		return Period.between(birthDate, today).getYears();
	}

	// Calculates isSmoker
	public Boolean isSmoker() {
		return this.getSmokingStatus() != null && (this.getSmokingStatus() == SmokingStatus.CurrentSmoker
				|| this.getSmokingStatus() == SmokingStatus.FormerSmoker);
	}

	// Enums for Gender, SmokingStatus, ResidenceType, and WorkType
	public enum Gender {

		Male, Female, Other

	}

	public enum SmokingStatus {

		CurrentSmoker, FormerSmoker, NeverSmoked, Unknown

	}

	public enum ResidenceType {

		OwnHouse, OwnApartment, Rental, Other

	}

	public enum WorkType {

		SalariedEmployee, DailyWorker, BusinessOwner, SelfEmployed, Unemployed, Other, Student, Retired

	}

}
