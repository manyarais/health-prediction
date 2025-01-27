package org.springframework.samples.petclinic.patient;

import com.google.protobuf.Value;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class PatientRiskAssessment {

	// public static void main(String[] args) throws IOException {

	// // Replace with your actual endpoint ID, project ID, and location
	// String endpointId = "YOUR_ENDPOINT_ID";
	// String projectId = "YOUR_PROJECT_ID";
	// String location = "us-central1";

	// // Get patient data from Patient object
	// Patient patient = new Patient(); // Assuming you have a Patient object populated
	// Map<String, Value> patientData = preparePatientDataMap(patient);

	// // Create PredictionInstance
	// PredictionInstance instance = PredictionInstance.newBuilder()
	// .putAllInputs(patientData)
	// .build();

	// // Create PredictRequest
	// PredictRequest request = PredictRequest.newBuilder()
	// .setEndpoint(Endpoint.newBuilder()
	// .setName(String.format("projects/%s/locations/%s/endpoints/%s", projectId,
	// location, endpointId))
	// .build())
	// .setInstances(instance)
	// .build();

	// // Create PredictionServiceClient
	// try (PredictionServiceClient predictionServiceClient =
	// PredictionServiceClient.create()) {

	// // Make the prediction request
	// PredictResponse response = predictionServiceClient.predict(request);

	// // Extract predictions
	// // - Heart Disease Risk
	// double heartDiseaseRisk = response.getPredictionsList().get(0).getNumberValue();

	// // - Diabetes Risk
	// double diabetesRisk = response.getPredictionsList().get(1).getNumberValue();

	// // Print or store the predictions
	// System.out.println("Heart Disease Risk: " + heartDiseaseRisk);
	// System.out.println("Diabetes Risk: " + diabetesRisk);

	// // **Call Gemini LLM for Personalized Recommendations**

	// // Prepare prompt for Gemini
	// String prompt = generateRecommendationPrompt(patient, heartDiseaseRisk,
	// diabetesRisk);

	// // Call Gemini LLM API (replace with your actual LLM API call)
	// String recommendations = callGeminiLLM(prompt);

	// // Print or store the recommendations
	// System.out.println("Personalized Recommendations:\n" + recommendations);

	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public static String recommendation(Patient patient) throws IOException {

		// Extract predictions
		// - Heart Disease Risk
		// double heartDiseaseRisk =
		// response.getPredictionsList().get(0).getNumberValue();

		double heartDiseaseRisk = 88.00;

		// - Diabetes Risk
		// double diabetesRisk = response.getPredictionsList().get(1).getNumberValue();
		double diabetesRisk = 88.00;

		// Print or store the predictions
		System.out.println("Heart Disease Risk: " + heartDiseaseRisk);
		System.out.println("Diabetes Risk: " + diabetesRisk);

		// **Call Gemini LLM for Personalized Recommendations**

		// Prepare prompt for Gemini
		String prompt = generateRecommendationPrompt(patient, heartDiseaseRisk, diabetesRisk);

		// Call Gemini LLM API (replace with your actual LLM API call)
		String recommendations = callGeminiLLM(prompt);

		// Print or store the recommendations
		System.out.println("Personalized Recommendations:\n" + recommendations);

		return recommendations;

	}

	// Prepares a Map containing patient data suitable for model input
	private static Map<String, Value> preparePatientDataMap(Patient patient) {
		Map<String, Value> patientData = new HashMap<>();
		patientData.put("age", Value.newBuilder().setNumberValue(calculateAge(patient.getDateOfBirth())).build());
		patientData.put("gender", Value.newBuilder().setStringValue(patient.getGender().toString()).build());
		patientData.put("smoker",
				Value.newBuilder()
					.setBoolValue(patient.getSmokingStatus() == Patient.SmokingStatus.CurrentSmoker)
					.build());
		patientData.put("diabetes",
				Value.newBuilder()
					.setBoolValue(patient.getHaveDiabetes() != null ? patient.getHaveDiabetes() : false)
					.build());
		patientData.put("hypertension",
				Value.newBuilder()
					.setBoolValue(patient.getHaveHypertension() != null ? patient.getHaveHypertension() : false)
					.build());
		patientData.put("bmi",
				Value.newBuilder()
					.setNumberValue(patient.getBmi() != null ? patient.getBmi().doubleValue() : 0.0)
					.build());
		// Add more relevant patient data fields here (e.g., family history)

		return patientData;
	}

	// Calculates age based on the date of birth
	private static int calculateAge(java.sql.Date dateOfBirth) {
		if (dateOfBirth == null) {
			return 0;
		}
		LocalDate birthDate = dateOfBirth.toLocalDate();
		LocalDate today = LocalDate.now();
		return Period.between(birthDate, today).getYears();
	}

	// Generates prompt for Gemini LLM
	private static String generateRecommendationPrompt(Patient patient, double heartDiseaseRisk, double diabetesRisk) {
		StringBuilder promptBuilder = new StringBuilder();
		promptBuilder.append("Based on the following patient data:\n");
		promptBuilder.append("Age: ").append(calculateAge(patient.getDateOfBirth())).append("\n");
		promptBuilder.append("Gender: ").append(patient.getGender().toString()).append("\n");
		promptBuilder.append("Smoker: ")
			.append(patient.getSmokingStatus() == Patient.SmokingStatus.CurrentSmoker)
			.append("\n");
		promptBuilder.append("Diabetes: ")
			.append(patient.getHaveDiabetes() != null ? patient.getHaveDiabetes() : false)
			.append("\n");
		promptBuilder.append("Hypertension: ")
			.append(patient.getHaveHypertension() != null ? patient.getHaveHypertension() : false)
			.append("\n");
		promptBuilder.append("BMI: ")
			.append(patient.getBmi() != null ? patient.getBmi().doubleValue() : 0.0)
			.append("\n");
		// Add more patient data fields to the prompt
		promptBuilder.append("and their calculated risks:\n");
		promptBuilder.append("Heart Disease Risk: ").append(heartDiseaseRisk).append("\n");
		promptBuilder.append("Diabetes Risk: ").append(diabetesRisk).append("\n");
		promptBuilder.append("Generate a personalized health recommendation plan for this patient. ");
		promptBuilder.append("The plan should include:\n");
		promptBuilder.append("- Lifestyle modifications (diet, exercise, stress management)\n");
		promptBuilder.append("- Preventive screenings and checkups\n");
		promptBuilder.append("- Medication recommendations (if applicable)\n");
		promptBuilder.append(
				"The recommendations should be concise, easy to understand, and tailored to the patient's individual risk factors.");
		return promptBuilder.toString();
	}

	// Placeholder function to call Gemini LLM API
	private static String callGeminiLLM(String prompt) {
		// Implement logic to call Gemini LLM API
		// and get the generated recommendations
		// This is a simplified example and needs to be replaced
		// with your actual LLM API integration.
		return "**Sample Recommendations:**\n" + "- Maintain a healthy diet with plenty of fruits and vegetables.\n"
				+ "- Engage in regular physical activity, such as brisk walking or jogging.\n"
				+ "- Manage stress through relaxation techniques like meditation or yoga.\n"
				+ "- Schedule regular checkups with your doctor.";
	}

}
