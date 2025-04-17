package com.example.solutionchallenge

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
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

        // 1️⃣ 그래프 처리
        val lineChart = view.findViewById<LineChart>(R.id.sugarLineChart)

        val entries = listOf(
            Entry(0f, 15f),
            Entry(1f, 20f),
            Entry(2f, 10f),
            Entry(3f, 30f),
            Entry(4f, 25f),
            Entry(5f, 40f),
            Entry(6f, 18f)
        )

        val dataSet = LineDataSet(entries, "Sugar Intake (g)").apply {
            color = Color.parseColor("#FFA726")
            lineWidth = 3f
            setCircleColor(Color.parseColor("#FFA726"))
            circleRadius = 5f
            valueTextSize = 12f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        lineChart.data = LineData(dataSet)

        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        lineChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            textSize = 12f
            setDrawGridLines(false)
        }

        val avgLine = LimitLine(25f, "Average (25g)").apply {
            lineColor = Color.RED
            lineWidth = 2f
            textColor = Color.RED
            textSize = 12f
        }
        lineChart.axisLeft.addLimitLine(avgLine)

        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.textSize = 12f
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.legend.isEnabled = false
        lineChart.invalidate()

        // 2️⃣ 카드 클릭 이벤트 (프래그먼트 전환)
        val photoCard = view.findViewById<CardView>(R.id.photoCard)
        val menuCard = view.findViewById<CardView>(R.id.menuCard)

        photoCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecordStep1Fragment())
                .addToBackStack(null)
                .commit()
        }

        //menuCard.setOnClickListener {
            //requireActivity().supportFragmentManager.beginTransaction()
                //.replace(R.id.fragmentContainer, MenuScanStep1Fragment())
               // .addToBackStack(null)
                //.commit()
        }
    }
//}
