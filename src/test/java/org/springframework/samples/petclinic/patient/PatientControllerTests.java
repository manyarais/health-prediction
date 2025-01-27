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

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link PatientController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(PatientController.class)
@DisabledInNativeImage
@DisabledInAotMode
class PatientControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PatientRepository patients;

	private Patient saurabh() {
		Patient saurabh = new Patient();
		saurabh.setId(TEST_OWNER_ID);
		saurabh.setUserID("saurabhrais");
		/*
		 * saurabh.setAge(55); saurabh.setAvg_glucose_level(43); saurabh.setBmi(45.5);
		 * saurabh.setEver_married("Yes"); saurabh.setGender("Female");
		 * saurabh.setHeart_disease(0); saurabh.setHypertension(0);
		 * saurabh.setResidence_type("House"); saurabh.setUserID("saurabhtest1");
		 * saurabh.setWork_type("Office");
		 */
		return saurabh;
	}

	@BeforeEach
	void setup() {

		Patient saurabh = saurabh();
		given(this.patients.findByUserIDStartingWith(eq("saurabhrais"), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(saurabh)));

		given(this.patients.findAll(any(Pageable.class))).willReturn(new PageImpl<>(Lists.newArrayList(saurabh)));

		given(this.patients.findById(TEST_OWNER_ID)).willReturn(Optional.of(saurabh));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/patients/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("patient"))
			.andExpect(view().name("patients/createOrUpdatePatientForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/patients/new").param("userID", "saurabhrais")
		/*
		 * .param("age", "45") .param("bmi", "43.1") .param("ever_married", "Yes")
		 * .param("heart_disease", "0") .param("hypertension", "0") .param("gender",
		 * "Male") .param("residence_type", "House") .param("work_type", "Office")
		 */
		).andExpect(status().is3xxRedirection());
	}

	/**
	 * @Test void testProcessCreationFormHasErrors() throws Exception { mockMvc
	 * .perform(post("/patients/new").param("firstName", "Joe").param("lastName",
	 * "Bloggs").param("city", "London")) .andExpect(status().isOk())
	 * .andExpect(model().attributeHasErrors("patient"))
	 * .andExpect(model().attributeHasFieldErrors("patient", "address"))
	 * .andExpect(model().attributeHasFieldErrors("patient", "telephone"))
	 * .andExpect(view().name("patients/createOrUpdatePatientForm")); }
	 *
	 **/

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/patients/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("patient"))
			.andExpect(view().name("patients/findPatients"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Patient> tasks = new PageImpl<>(Lists.newArrayList(saurabh(), new Patient()));
		when(this.patients.findByUserIDStartingWith(anyString(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/patients?page=1"))
			.andExpect(status().isOk())
			.andExpect(view().name("patients/patientsList"));
	}

	@Test
	void testProcessFindFormByUserID() throws Exception {
		Page<Patient> tasks = new PageImpl<>(Lists.newArrayList(saurabh()));
		when(this.patients.findByUserIDStartingWith(eq("saurabhrais"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/patients?page=1").param("userID", "saurabhrais"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/patients/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoPatientsFound() throws Exception {
		Page<Patient> tasks = new PageImpl<>(Lists.newArrayList());
		when(this.patients.findByUserIDStartingWith(eq("Unknown UserID"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/patients?page=1").param("userID", "Unknown UserID"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("patient", "userID"))
			.andExpect(model().attributeHasFieldErrorCode("patient", "userID", "notFound"))
			.andExpect(view().name("patients/findPatients"));

	}

	@Test
	void testInitUpdatePatientForm() throws Exception {
		mockMvc.perform(get("/patients/{patientId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("patient"))
			.andExpect(model().attribute("patient", hasProperty("userID", is("saurabhrais"))))
			.andExpect(view().name("patients/createOrUpdatePatientForm"));
	}

	@Test
	void testProcessUpdatePatientFormSuccess() throws Exception {
		mockMvc.perform(post("/patients/{patientId}/edit", TEST_OWNER_ID).param("userID", "saurabhrais"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/patients/{patientId}"));
	}

	@Test
	void testProcessUpdatePatientFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/patients/{patientId}/edit", TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/patients/{patientId}"));
	}

	@Test
	void testProcessUpdatePatientFormHasErrors() throws Exception {
		mockMvc.perform(post("/patients/{patientId}/edit", TEST_OWNER_ID).param("userID", "saurabhrais"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("patient"))
			.andExpect(model().attributeHasFieldErrors("patient", "userID"))
			.andExpect(view().name("patients/createOrUpdatePatientForm"));
	}

	@Test
	void testShowPatient() throws Exception {
		mockMvc.perform(get("/patients/{patientId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("patient", hasProperty("userID", is("saurabhrais"))))
			.andExpect(view().name("patients/patientDetails"));
	}

	@Test
	public void testProcessUpdatePatientFormWithIdMismatch() throws Exception {
		int pathPatientId = 1;

		Patient patient = new Patient();
		patient.setId(2);
		patient.setUserID("saurabhrais");
		/*
		 * patient.setAge(55); patient.setAvg_glucose_level(43); patient.setBmi(45.5);
		 * patient.setEver_married("Yes"); patient.setGender("Female");
		 * patient.setHeart_disease(0); patient.setHypertension(0);
		 * patient.setResidence_type("House"); patient.setWork_type("Office");
		 */

		when(patients.findById(pathPatientId)).thenReturn(Optional.of(patient));

		mockMvc
			.perform(MockMvcRequestBuilders.post("/patients/{patientId}/edit", pathPatientId)
				.flashAttr("patient", patient))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/patients/" + pathPatientId + "/edit"))
			.andExpect(flash().attributeExists("error"));
	}

}
