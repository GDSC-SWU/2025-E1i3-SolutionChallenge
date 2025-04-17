package com.example.solutionchallenge

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class MenuScanStep1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu_scan_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuContainer = view.findViewById<LinearLayout>(R.id.menuResultContainer)

        val menuList = listOf(
            FoodItem("Boiled Egg", 2),
            FoodItem("Grilled Chicken", 3),
            FoodItem("Salmon Salad", 4),
            FoodItem("Tofu Bowl", 5),
            FoodItem("Avocado Toast", 6),
            FoodItem("Kimchi", 1)
        )

        menuList.forEach {
            val itemView = layoutInflater.inflate(R.layout.item_food_result, menuContainer, false)
            itemView.findViewById<TextView>(R.id.foodName).text = it.name
            itemView.findViewById<TextView>(R.id.foodSugar).text = "${it.sugar}g"
            menuContainer.addView(itemView)
        }
    }
    // ⚠️ AI 연동 필요: 서버에서 결과 가져오도록 수정해야 함
}

