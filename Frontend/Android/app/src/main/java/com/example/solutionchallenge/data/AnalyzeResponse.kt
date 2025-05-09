package com.example.solutionchallenge.data

import com.example.solutionchallenge.api.FoodItem

data class AnalyzeResponse(
    val morning: List<FoodItem>?,
    val lunch: List<FoodItem>?,
    val dinner: List<FoodItem>?,
    val snack: List<FoodItem>?
)
