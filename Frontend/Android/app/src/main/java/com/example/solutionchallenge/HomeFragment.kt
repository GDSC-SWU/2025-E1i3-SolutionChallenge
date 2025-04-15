package com.example.solutionchallenge

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lineChart = view.findViewById<LineChart>(R.id.sugarLineChart)

        // 1️⃣ 당 섭취 데이터 (예시)
        val entries = listOf(
            Entry(0f, 15f),
            Entry(1f, 20f),
            Entry(2f, 10f),
            Entry(3f, 30f),
            Entry(4f, 25f),
            Entry(5f, 40f),
            Entry(6f, 18f)
        )

        // 2️⃣ 데이터셋 설정
        val dataSet = LineDataSet(entries, "Sugar Intake (g)").apply {
            color = Color.parseColor("#FFA726")
            lineWidth = 3f
            setCircleColor(Color.parseColor("#FFA726"))
            circleRadius = 5f
            valueTextSize = 12f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        lineChart.data = LineData(dataSet)

        // 3️⃣ X축 요일 설정
        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        lineChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            textSize = 12f
        }

        // 4️⃣ 평균선
        val avgLine = LimitLine(25f, "Average (25g)").apply {
            lineColor = Color.RED
            lineWidth = 2f
            textColor = Color.RED
            textSize = 12f
        }
        lineChart.axisLeft.addLimitLine(avgLine)

        // 5️⃣ 기타 설정
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.invalidate() // 새로고침
    }
}
