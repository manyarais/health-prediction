package org.springframework.samples.petclinic.patient;

public class HealthRiskCalculator {

	private static final int YOUNG_AGE_THRESHOLD = 35;

	private static final double NORMAL_BMI_THRESHOLD = 25;

	private static final double OVERWEIGHT_BMI_THRESHOLD = 30;

	public static String getHeartDiseaseRisk(Patient patient) {
		int age = patient.getAge();
		int riskScore = 0;

		if (age < YOUNG_AGE_THRESHOLD) {
			// Lower risk for young age
			riskScore--;
		}

		if (patient.getHaveHypertension() != null && patient.getHaveHypertension()) {
			riskScore += 2;
		}

		if (patient.getHaveDiabetes() != null && patient.getHaveDiabetes()) {
			riskScore += 3;
		}

		if (patient.getHadStroke() != null && patient.getHadStroke()) {
			riskScore += 3;
		}

		if (patient.getHaveHeartDisease() != null && patient.getHaveHeartDisease()) {
			riskScore += 4;
		}

		if (patient.isSmoker()) {
			riskScore += 2;
		}

		if (patient.getBmi() != null) {
			if (patient.getBmi().doubleValue() > OVERWEIGHT_BMI_THRESHOLD) {
				riskScore += 2;
			}
			else if (patient.getBmi().doubleValue() > NORMAL_BMI_THRESHOLD) {
				riskScore++;
			}
		}

		return categorizeRisk(riskScore, "Heart Disease");
	}

	public static String getDiabetesRisk(Patient patient) {
		int riskScore = 0;

		if (patient.getHaveHypertension() != null && patient.getHaveHypertension()) {
			riskScore += 2;
		}

		if (patient.getHaveDiabetes() != null && patient.getHaveDiabetes()) {
			riskScore += 5;
		}

		if (patient.getBmi() != null) {
			if (patient.getBmi().doubleValue() > OVERWEIGHT_BMI_THRESHOLD) {
				riskScore += 2;
			}
		}

		if (patient.isSmoker()) {
			riskScore++;
		}

		// Add more factors for diabetes risk as needed

		return categorizeRisk(riskScore, "Diabetes");
	}

	private static String categorizeRisk(int score, String riskType) {
		if (score <= 1) {
			return "LOW";
		}
		else if (score <= 3) {
			return "MEDIUM";
		}
		else if (score <= 5) {
			return "HIGH";
		}
		else {
			System.out.println("WARNING: Very high risk factors identified for " + riskType
					+ ". Please consult a healthcare professional.");
			return "VERY HIGH";
		}
	}

}
