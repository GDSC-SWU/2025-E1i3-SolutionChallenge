package com.example.solutionchallenge

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class RecordStep3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_step3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mealType = arguments?.getString("mealType") ?: ""
        val foodList = arguments?.getSerializable("foodList") as? ArrayList<FoodItem> ?: arrayListOf()

        val titleText = view.findViewById<TextView>(R.id.finalSummaryTitle)
        val mealText = view.findViewById<TextView>(R.id.mealType)
        val summaryContainer = view.findViewById<LinearLayout>(R.id.summaryContainer)
        val totalSugarText = view.findViewById<TextView>(R.id.totalSugarText)

        titleText.text = "I'll write it down like this"
        mealText.text = mealType

        var total = 0
        foodList.forEach {
            val itemView = layoutInflater.inflate(R.layout.item_food_summary, summaryContainer, false)
            itemView.findViewById<TextView>(R.id.foodName).text = it.name
            itemView.findViewById<TextView>(R.id.foodSugar).text = "${it.sugar}g"
            summaryContainer.addView(itemView)
            total += it.sugar
        }

        totalSugarText.text = "Less: ${total}g"

        val btnOk = view.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            // TODO: 서버에 기록 저장 (식사 타입, foodList 등)
            // ex) POST /save-record
            // MealType: 🍝 Dinner
            // FoodList: [Clam Pasta, Pickle, Baguette]

            // 홈화면으로 이동
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

    }
}
