package com.example.solutionchallenge.api

import com.google.gson.annotations.SerializedName

data class AnalyzeResponse(
    @SerializedName("meals")
    val meals: Meals,
    @SerializedName("daily_total_sugar")
    val dailyTotalSugar: Double,
    @SerializedName("daily_risk_level")
    val dailyRiskLevel: String
)

data class Meals(
    @SerializedName("morning")
    val morning: MealData,
    @SerializedName("lunch")
    val lunch: MealData,
    @SerializedName("dinner")
    val dinner: MealData,
    @SerializedName("snack")
    val snack: MealData
)

data class MealData(
    @SerializedName("detected_classes")
    val detectedClasses: List<String>,
    @SerializedName("refined_names")
    val refinedNames: Map<String, String>,
    @SerializedName("food_sugar_data")
    val foodSugarData: Map<String, Any>,
    @SerializedName("total_sugar")
    val totalSugar: Double,
    @SerializedName("risk_level")
    val riskLevel: String
)
