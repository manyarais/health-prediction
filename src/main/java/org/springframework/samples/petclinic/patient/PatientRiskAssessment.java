package org.springframework.samples.petclinic.patient;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;

import java.io.IOException;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class PatientRiskAssessment {

	public static String recommendation(Patient patient) throws IOException {

		// Extract predictions
		// - Diabetes Risk
		String diabetesRisk = HealthRiskCalculator.getDiabetesRisk(patient);
		// - Heart Disease Risk
		String heartDiseaseRisk = HealthRiskCalculator.getHeartDiseaseRisk(patient);

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
	private static String generateRecommendationPrompt(Patient patient, String heartDiseaseRisk, String diabetesRisk) {
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

	private static String callGeminiLLM(String prompt) {

		ChatLanguageModel model = VertexAiGeminiChatModel.builder()
			.project("my-genome-project-p2")
			.location("us-central1")
			.modelName("gemini-1.5-flash-002")
			.maxOutputTokens(500)
			.temperature(1.0f)
			.topK(40)
			.topP(0.95f)
			.maxRetries(3)
			.build();

		PromptTemplate promptTemplate = PromptTemplate.from(prompt);

		Map<String, Object> variables = new HashMap<>();
		variables.put("dish", "dessert");
		// variables.put("ingredients", "strawberries, chocolate, and whipped cream");

		Prompt p = promptTemplate.apply(variables);

		// Response<AiMessage> response = model.generate(p.toUserMessage());

		Response<AiMessage> response = model.generate(p.toUserMessage());

		System.out.println(response.content().text());

		return response.content().text();
	}

}
