package com.example.solutionchallenge

import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.*
import java.time.format.DateTimeFormatter

class ArchiveFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_archive, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        val currentMonth = YearMonth.now()
        val firstDayOfWeek = DayOfWeek.MONDAY

        calendarView.setup(currentMonth, currentMonth.plusMonths(12), firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.bind(day)
            }
        }

        view.findViewById<TextView>(R.id.galleryIcon).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, ArchiveGalleryFragment())
                addToBackStack(null)
            }
        }

        view.findViewById<CardView>(R.id.cardRecord).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, CameraMenuFragment())
                addToBackStack(null)
            }
        }

        // 예시: 당 섭취량 45g 시각화
        renderSugarCubes(view, sugarGrams = 45f)

        return view
    }

    private fun renderSugarCubes(view: View, sugarGrams: Float) {
        val sugarGrid = view.findViewById<androidx.gridlayout.widget.GridLayout>(R.id.sugarGrid)

        sugarGrid.removeAllViews()

        val totalCubes = 30
        val maxSugar = 100f
        val filledCubes = ((sugarGrams / maxSugar) * totalCubes).toInt()

        val size = (40 * resources.displayMetrics.density).toInt()
        val margin = (4 * resources.displayMetrics.density).toInt()

        repeat(totalCubes) { index ->
            val cube = View(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(margin, margin, margin, margin)
                }

                setBackgroundResource(
                    if (index < filledCubes) R.drawable.sugar_box_filled
                    else R.drawable.sugar_box_background
                )
            }
            sugarGrid.addView(cube)
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val textView = TextView(view.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            textSize = 16f
        }

        init {
            (view as ViewGroup).addView(textView)
        }

        fun bind(day: CalendarDay) {
            textView.text = day.date.dayOfMonth.toString()
            textView.setOnClickListener {
                val selectedDate = day.date.format(dateFormatter)
                val detailFragment = ArchiveDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("selectedDate", selectedDate)
                    }
                }
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainer, detailFragment)
                    addToBackStack(null)
                }
            }
        }
    }
}
