package com.example.solutionchallenge

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class RecordStep2Fragment : Fragment() {

    private lateinit var nextButton: Button
    private lateinit var foodContainer: LinearLayout
    private val foodList = arrayListOf<FoodItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextButton = view.findViewById(R.id.btnNext)
        foodContainer = view.findViewById(R.id.foodResultContainer)

        // ⭐️ 예시 데이터 (AI가 추론했다고 가정)
        foodList.add(FoodItem("Clam Pasta", 8))
        foodList.add(FoodItem("Pickle", 9))
        foodList.add(FoodItem("Baguette", 7))

        foodList.forEach {
            val itemView = layoutInflater.inflate(R.layout.item_food_result, foodContainer, false)
            itemView.findViewById<TextView>(R.id.foodName).text = it.name
            itemView.findViewById<TextView>(R.id.foodSugar).text = "${it.sugar}g"
            foodContainer.addView(itemView)
        }

        nextButton.setOnClickListener {
            val mealType = "🍝 Dinner" // 선택된 식사 타입
            val bundle = Bundle().apply {
                putString("mealType", mealType)
                putSerializable("foodList", foodList)
            }

            val fragment = RecordStep3Fragment()
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
