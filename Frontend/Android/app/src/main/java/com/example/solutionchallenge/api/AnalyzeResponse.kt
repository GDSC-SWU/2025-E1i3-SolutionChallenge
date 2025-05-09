package com.example.solutionchallenge.api

data class AnalyzeResponse(
    val morning: List<FoodItem>,
    val lunch: List<FoodItem>,
    val dinner: List<FoodItem>,
    val snack: List<FoodItem>
)

data class FoodItem(
    val name: String,
    val sugar: Int
)
