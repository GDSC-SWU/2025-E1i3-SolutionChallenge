package com.example.solutionchallenge

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordStep1Fragment : Fragment() {
    private var selectedDateIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dateContainer = view.findViewById<LinearLayout>(R.id.dateIconContainer)
        createDateButtons(dateContainer)
    }

    private fun createDateButtons(container: LinearLayout) {
        val today = Calendar.getInstance()
        val inflater = LayoutInflater.from(requireContext())

        for (i in 0 until 7) {
            val dateLayout = inflater.inflate(R.layout.item_date_circle, container, false) as LinearLayout

            val dayNum = today.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(today.time)

            val dayText = dateLayout.findViewById<TextView>(R.id.textDayNum)
            val weekText = dateLayout.findViewById<TextView>(R.id.textDayWeek)
            dayText.text = dayNum.toString()
            weekText.text = dayOfWeek

            // 기본 배경
            dateLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_date_item)

            // 인덱스 저장을 위한 변수 캡처
            val index = i
            dateLayout.setOnClickListener {
                updateDateSelection(container, index)
            }

            container.addView(dateLayout)
            today.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun updateDateSelection(container: LinearLayout, selectedIndex: Int) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i) as LinearLayout
            val dayText = child.findViewById<TextView>(R.id.textDayNum)
            val weekText = child.findViewById<TextView>(R.id.textDayWeek)

            if (i == selectedIndex) {
                child.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_date_item_selected)
                dayText.setTextColor(Color.WHITE)
                weekText.setTextColor(Color.WHITE)
            } else {
                child.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_date_item)
                dayText.setTextColor(Color.BLACK)
                weekText.setTextColor(Color.BLACK)
            }
        }

        selectedDateIndex = selectedIndex
    }
}
