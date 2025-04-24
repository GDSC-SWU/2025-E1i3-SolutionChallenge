package com.example.solutionchallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}
