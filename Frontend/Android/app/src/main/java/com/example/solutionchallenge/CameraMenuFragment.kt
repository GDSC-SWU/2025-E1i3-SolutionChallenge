package com.example.solutionchallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class CameraMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardRecord = view.findViewById<CardView>(R.id.cardRecord)
        val cardMenu = view.findViewById<CardView>(R.id.cardMenu)

        cardRecord.setOnClickListener {
            // 👉 RecordStep1Fragment로 프래그먼트 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecordStep1Fragment())
                .addToBackStack(null) // 뒤로가기 지원
                .commit()
        }

        cardMenu.setOnClickListener {
            val cameraFragment = CameraFragment().apply {
                arguments = Bundle().apply {
                    putString("cameraPurpose", "menu")
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, cameraFragment)
                .addToBackStack(null)
                .commit()
        }



    }


}
