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

		System.out.println("LLM Prompt: " + prompt);

		// Call Gemini LLM API (replace with your actual LLM API call)

		System.out.println(" Print the response: \n\n");
		String recommendations = callGeminiLLM(prompt);

		// Print or store the recommendations
		System.out.println("*****************Personalized Recommendations:\n\n" + recommendations);
		System.out.println("*******************");
		recommendations = removeAllHtmlCodeBlocks(recommendations);
		System.out.println("Removed HTML from Recommendations:\n\n" + recommendations);

		// generate html
		// recommendations = generateHtml(recommendations);
		// System.out.println("HTML Personalized Recommendations:\n" + recommendations);

		return recommendations;
	}

	public static String removeAllHtmlCodeBlocks(String input) {
		if (input == null || input.isEmpty()) {
			return input; // Handle null or empty input
		}

		return input.replace("```html", ""); // Simple replacement
	}

	public static String generateHtml(String inputString) {
		String[] sections = inputString.split("\\*\\*");
		StringBuilder html = new StringBuilder();

		html.append("    <h1>").append(sections[0]).append("</h1>\n\n");

		for (int i = 1; i < sections.length; i++) {
			String[] lines = sections[i].split("\\* ");
			html.append("    <h3>").append(lines[0].split(":")[0]).append("</h3>\n").append("    <ul>\n");
			for (int j = 1; j < lines.length; j++) {
				if (!lines[j].isEmpty()) {
					html.append("        <li>").append(lines[j]).append("</li>\n");
				}
			}
			html.append("    </ul>\n");
		}

		html.append(
				"    <p><strong>Disclaimer:** This information is for general guidance only and does not constitute medical advice. Consult with your physician for personalized recommendations and treatment plans.</p>\n");

		return html.toString();
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
		promptBuilder.append("- Lifestyle modifications (diet, exercise, stress management) (if applicable)\n");
		promptBuilder.append("- Preventive screenings and checkups (if applicable)\n");
		promptBuilder.append("- Medication recommendations in detail (if applicable)\n");
		promptBuilder.append("- Mindfulness recommendations (if applicable)\n");
		promptBuilder.append("The recommendations should be detailed, technical, add the details of tests,");
		promptBuilder.append(
				"and must be tailored to the patient's individual risk factors and attributes with explanations for why these tests are needed.");
		promptBuilder.append("Finally create it in an HTML Format within a Div.");

		return promptBuilder.toString();
	}

	private static String callGeminiLLM(String prompt) {

		ChatLanguageModel model = VertexAiGeminiChatModel.builder()
			.project("my-genome-project-p2")
			.location("us-central1")
			.modelName("gemini-1.5-flash-002")
			.temperature(0.8f)
			.maxRetries(3)
			.build();

		// .topP(0.95f)
		// .topK(40)
		// .maxOutputTokens(500)

		PromptTemplate promptTemplate = PromptTemplate.from(prompt);

		Map<String, Object> variables = new HashMap<>();
		variables.put("dish", "dessert");
		// variables.put("ingredients", "strawberries, chocolate, and whipped cream");

		Prompt p = promptTemplate.apply(variables);

		// Response<AiMessage> response = model.generate(p.toUserMessage());

		Response<AiMessage> response = model.generate(p.toUserMessage());

		System.out.println(response.content().toString());

		System.out.println(response.content().text());

		return response.content().text();
	}

}
