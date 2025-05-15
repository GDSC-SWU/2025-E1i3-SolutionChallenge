package com.example.solutionchallenge

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.solutionchallenge.data.FoodItem

class RecordStep3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_record_step3, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mealType = arguments?.getString("mealType") ?: ""
        val foodList = arguments?.getSerializable("foodList") as? ArrayList<FoodItem> ?: arrayListOf()

        val titleText = view.findViewById<TextView>(R.id.finalSummaryTitle)
        val mealText = view.findViewById<TextView>(R.id.mealType)
        val summaryContainer = view.findViewById<LinearLayout>(R.id.summaryContainer)
        val totalSugarText = view.findViewById<TextView>(R.id.totalSugarText)

        titleText.text = "AI 분석 결과 요약"
        mealText.text = mealType

        var total = 0
        foodList.forEach {
            val itemView = layoutInflater.inflate(R.layout.item_food_summary, summaryContainer, false)
            itemView.findViewById<TextView>(R.id.foodName).text = it.name
            itemView.findViewById<TextView>(R.id.foodSugar).text = "${it.sugar}g"
            summaryContainer.addView(itemView)
            total += it.sugar
        }

        totalSugarText.text = "총 당류: ${total}g"

        view.findViewById<Button>(R.id.btnOk).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }
    }
}
