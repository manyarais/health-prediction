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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
class PatientController {

	private static final String VIEWS_PATIENT_CREATE_OR_UPDATE_FORM = "patients/createOrUpdatePatientForm";

	private final PatientRepository patients;

	public PatientController(PatientRepository patients) {
		this.patients = patients;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("patient")
	public Patient findPatient(@PathVariable(name = "patientId", required = false) Integer patientId) {
		return patientId == null ? new Patient()
				: this.patients.findById(patientId)
					.orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId
							+ ". Please ensure the ID is correct " + "and the patient exists in the database."));
	}

	@GetMapping("/patients/new")
	public String initCreationForm() {
		return VIEWS_PATIENT_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/patients/new")
	public String processCreationForm(@Valid Patient patient, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the patient.");
			return VIEWS_PATIENT_CREATE_OR_UPDATE_FORM;
		}

		String recommendations = "";
		try {
			recommendations = PatientRiskAssessment.recommendation(patient);

			patient.setRecommendations(recommendations);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		this.patients.save(patient);

		redirectAttributes.addFlashAttribute("message", "New Patient Created. Recommendation: " + recommendations);
		return "redirect:/patients/" + patient.getId();
	}

	@GetMapping("/patients/find")
	public String initFindForm() {
		return "patients/findPatients";
	}

	@GetMapping("/patients")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Patient patient, BindingResult result,
			Model model) {
		// allow parameterless GET request for /patients to return all records
		if (patient.getUserId() == null) {
			patient.setUserId(""); // empty string signifies broadest possible search
		}

		// find patients by last name
		Page<Patient> patientsResults = findPaginatedForPatientsUserId(page, patient.getUserId());
		if (patientsResults.isEmpty()) {
			// no patients found
			result.rejectValue("userId", "notFound", "not found");
			return "patients/findPatients";
		}

		if (patientsResults.getTotalElements() == 1) {
			// 1 patient found
			patient = patientsResults.iterator().next();
			return "redirect:/patients/" + patient.getId();
		}

		// multiple patients found
		return addPaginationModel(page, model, patientsResults);
	}

	private String addPaginationModel(int page, Model model, Page<Patient> paginated) {
		List<Patient> listPatients = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listPatients", listPatients);
		return "patients/patientsList";
	}

	private Page<Patient> findPaginatedForPatientsUserId(int page, String userId) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return patients.findByUserIdStartingWith(userId, pageable);
	}

	@GetMapping("/patients/{patientId}/edit")
	public String initUpdatePatientForm() {
		return VIEWS_PATIENT_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/patients/{patientId}/edit")
	public String processUpdatePatientForm(@Valid Patient patient, BindingResult result,
			@PathVariable("patientId") int patientId, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the patient.");
			return VIEWS_PATIENT_CREATE_OR_UPDATE_FORM;
		}

		if (patient.getId() != patientId) {
			result.rejectValue("id", "mismatch", "The patient ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Patient ID mismatch. Please try again.");
			return "redirect:/patients/{patientId}/edit";
		}

		patient.setId(patientId);
		this.patients.save(patient);
		redirectAttributes.addFlashAttribute("message", "Patient Values Updated");
		return "redirect:/patients/{patientId}";
	}

	/**
	 * Custom handler for displaying an patient.
	 * @param patientId the ID of the patient to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/patients/{patientId}")
	public ModelAndView showPatient(@PathVariable("patientId") int patientId) {
		ModelAndView mav = new ModelAndView("patients/patientDetails");
		Optional<Patient> optionalPatient = this.patients.findById(patientId);
		Patient patient = optionalPatient.orElseThrow(() -> new IllegalArgumentException(
				"Patient not found with id: " + patientId + ". Please ensure the ID is correct "));
		mav.addObject(patient);
		return mav;
	}

}
