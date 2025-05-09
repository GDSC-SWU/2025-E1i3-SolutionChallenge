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
import kotlinx.coroutines.launch
import java.io.File

class MenuScanStep1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu_scan_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuContainer = view.findViewById<LinearLayout>(R.id.menuResultContainer)
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

                val menuList = response.lunch
                if (menuList.isNullOrEmpty()) {
                    Log.e("MenuScan", "'lunch' 분석 결과가 비어 있습니다.")
                    Toast.makeText(requireContext(), "AI 분석 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                menuList.forEach {
                    val itemView = layoutInflater.inflate(R.layout.item_food_result, menuContainer, false)
                    itemView.findViewById<TextView>(R.id.foodName).text = it.name
                    itemView.findViewById<TextView>(R.id.foodSugar).text = "${it.sugar}g"
                    menuContainer.addView(itemView)
                }

            } catch (e: Exception) {
                Log.e("MenuScan", "API 호출 실패", e)
                Toast.makeText(requireContext(), "AI 서버 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
