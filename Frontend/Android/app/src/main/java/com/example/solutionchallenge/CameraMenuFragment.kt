package com.example.solutionchallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CameraMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardRecord = view.findViewById<CardView>(R.id.cardRecord)
        val cardMenu = view.findViewById<CardView>(R.id.cardMenu)

        cardRecord.setOnClickListener {
            // Record Flow 진입 (날짜/식사/이미지 선택)
            findNavController().navigate(R.id.action_cameraMenu_to_recordStep1)
        }

        cardMenu.setOnClickListener {
            // 메뉴 인식 Flow 진입
            findNavController().navigate(R.id.action_cameraMenu_to_menuScanStep1)
        }
    }
}
