package com.example.solutionchallenge

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.solutionchallenge.api.RetrofitClient
import com.example.solutionchallenge.api.toMultipartBodyPart
import com.example.solutionchallenge.data.FoodItem
import kotlinx.coroutines.launch
import java.io.File

class RecordStep2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_record_step2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            sendImagesToServer()
        }
    }

    private fun sendImagesToServer() {
        val morningUri = arguments?.getString("imageUriMorning")?.let { Uri.parse(it) }
        val lunchUri = arguments?.getString("imageUriLunch")?.let { Uri.parse(it) }
        val dinnerUri = arguments?.getString("imageUriDinner")?.let { Uri.parse(it) }
        val snackUri = arguments?.getString("imageUriSnack")?.let { Uri.parse(it) }

        if (morningUri == null || lunchUri == null || dinnerUri == null || snackUri == null) {
            Toast.makeText(requireContext(), "모든 사진을 선택해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val morningFile = uriToFile(morningUri) ?: return
        val lunchFile = uriToFile(lunchUri) ?: return
        val dinnerFile = uriToFile(dinnerUri) ?: return
        val snackFile = uriToFile(snackUri) ?: return

        val morningPart = morningFile.toMultipartBodyPart("morning")
        val lunchPart = lunchFile.toMultipartBodyPart("lunch")
        val dinnerPart = dinnerFile.toMultipartBodyPart("dinner")
        val snackPart = snackFile.toMultipartBodyPart("snack")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.analyzeDay(
                    morning = morningPart,
                    lunch = lunchPart,
                    dinner = dinnerPart,
                    snack = snackPart
                )

                if (response.isSuccessful) {
                    val result = response.body()

                    result?.let {
                        Log.d("AI_RESULT", "하루 총 당류: ${it.dailyTotalSugar}g (${it.dailyRiskLevel})")

                        val foodList = arrayListOf<FoodItem>()

                        val meals = listOf(
                            "Breakfast" to it.meals.morning,
                            "Lunch" to it.meals.lunch,
                            "Dinner" to it.meals.dinner,
                            "Snack" to it.meals.snack
                        )

                        meals.forEach { (mealName, mealData) ->
                            Log.d("AI_RESULT", "$mealName 총당류: ${mealData.totalSugar}g (${mealData.riskLevel})")
                            mealData.detectedClasses.forEach { foodName ->
                                val sugarInfo = mealData.foodSugarData[foodName]
                                val sugarValue = when (sugarInfo) {
                                    is Number -> sugarInfo.toInt()
                                    is String -> Regex("""\d+""").find(sugarInfo)?.value?.toIntOrNull() ?: 0
                                    else -> 0
                                }
                                foodList.add(FoodItem(name = "$mealName - $foodName", sugar = sugarValue))
                            }
                        }

                        // 결과를 Step3로 넘기기
                        val bundle = Bundle().apply {
                            putString("mealType", "Daily Total: ${it.dailyTotalSugar}g (${it.dailyRiskLevel})")
                            putSerializable("foodList", foodList)
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, RecordStep3Fragment().apply { arguments = bundle })
                            .addToBackStack(null)
                            .commit()
                    }
                } else {
                    Toast.makeText(requireContext(), "서버 오류 발생: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "통신 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val contentResolver = requireContext().contentResolver
        val fileName = "temp_${System.currentTimeMillis()}.jpg"
        val tempFile = File(requireContext().cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
