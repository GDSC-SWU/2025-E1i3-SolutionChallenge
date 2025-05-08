package com.example.solutionchallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ArchiveDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archive_detail, container, false)

        val selectedDate = arguments?.getString("selectedDate") ?: "날짜 없음"
        val dateLabel = view.findViewById<TextView>(R.id.selectedDateLabel)
        dateLabel.text = selectedDate

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
}
