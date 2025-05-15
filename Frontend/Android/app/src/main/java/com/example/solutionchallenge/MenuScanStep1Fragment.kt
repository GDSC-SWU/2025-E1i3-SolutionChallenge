package com.example.solutionchallenge

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.solutionchallenge.api.MealData
import com.example.solutionchallenge.api.RetrofitClient
import com.example.solutionchallenge.api.toMultipartBodyPart
import kotlinx.coroutines.launch
import java.io.File

class MenuScanStep1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_menu_scan_step1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val foodResultContainer = view.findViewById<LinearLayout>(R.id.foodResultContainer)
        val photoUri = arguments?.getString("photoUri")

        if (photoUri.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = File(Uri.parse(photoUri).path ?: return)

        val morning = imageFile.toMultipartBodyPart("morning")
        val lunch = imageFile.toMultipartBodyPart("lunch")
        val dinner = imageFile.toMultipartBodyPart("dinner")
        val snack = imageFile.toMultipartBodyPart("snack")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.analyzeDay(
                    morning = morning,
                    lunch = lunch,
                    dinner = dinner,
                    snack = snack
                )

                Log.d("MenuScan", "서버 응답 코드: ${response.code()}")
                Log.d("MenuScan", "서버 응답 바디: ${response.body()}")

                val resultBody = response.body()

                if (resultBody == null) {
                    Toast.makeText(requireContext(), "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val meals = resultBody.meals

                fun addMealData(mealName: String, mealData: MealData) {
                    mealData.detectedClasses.forEach { foodName ->
                        val sugarInfo = mealData.foodSugarData[foodName]
                        val sugarValue = when (sugarInfo) {
                            is Number -> sugarInfo.toInt()
                            is String -> Regex("""\d+""").find(sugarInfo)?.value?.toIntOrNull() ?: 0
                            else -> 0
                        }

                        val itemView = layoutInflater.inflate(R.layout.item_food_result, foodResultContainer, false)
                        itemView.findViewById<TextView>(R.id.foodName).text = "$mealName - $foodName"
                        itemView.findViewById<TextView>(R.id.foodSugar).text = "${sugarValue}g"

                        foodResultContainer.addView(itemView)
                    }
                }

                addMealData("Breakfast", meals.morning)
                addMealData("Lunch", meals.lunch)
                addMealData("Dinner", meals.dinner)
                addMealData("Snack", meals.snack)

            } catch (e: Exception) {
                Log.e("MenuScan", "API 호출 실패", e)
                Toast.makeText(requireContext(), "AI 서버 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
