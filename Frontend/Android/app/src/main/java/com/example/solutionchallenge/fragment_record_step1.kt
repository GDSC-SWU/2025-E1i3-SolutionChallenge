package com.example.solutionchallenge

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class RecordStep1Fragment : Fragment() {

    private var selectedDateIndex = -1
    private var selectedMealIndex = -1
    private var isImageAdded = false

    private lateinit var nextButton: Button
    private lateinit var imageBoxContainer: LinearLayout
    private lateinit var dateContainer: LinearLayout
    private lateinit var mealButtons: List<Button>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_record_step1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dateContainer = view.findViewById(R.id.dateIconContainer)
        imageBoxContainer = view.findViewById(R.id.imageBoxContainer)
        nextButton = view.findViewById(R.id.nextButton)

        val btnBreakfast = view.findViewById<Button>(R.id.btnBreakfast)
        val btnLunch = view.findViewById<Button>(R.id.btnLunch)
        val btnDinner = view.findViewById<Button>(R.id.btnDinner)
        val btnSnack = view.findViewById<Button>(R.id.btnSnack)
        mealButtons = listOf(btnBreakfast, btnLunch, btnDinner, btnSnack)

        // ✅ 이미지 로드
        arguments?.getString("photoUri")?.let { uriString ->
            val imageView = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(this).load(Uri.parse(uriString)).into(imageView)
            (imageBoxContainer.getChildAt(0) as FrameLayout).apply {
                removeAllViews()
                addView(imageView)
            }
            isImageAdded = true
        }

        // ✅ 선택 상태 복원
        selectedDateIndex = arguments?.getInt("selectedDateIndex", -1) ?: -1
        selectedMealIndex = arguments?.getInt("selectedMealIndex", -1) ?: -1

        if (selectedDateIndex in 0 until dateContainer.childCount) {
            dateContainer.getChildAt(selectedDateIndex)
                .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brawn_600))
        }

        if (selectedMealIndex in mealButtons.indices) {
            mealButtons[selectedMealIndex]
                .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brawn_600))
        }

        // ✅ 날짜 선택
        for (i in 0 until dateContainer.childCount) {
            dateContainer.getChildAt(i).setOnClickListener {
                selectedDateIndex = i
                for (j in 0 until dateContainer.childCount) {
                    dateContainer.getChildAt(j).setBackgroundResource(R.drawable.bg_date_item)
                }
                it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brawn_600))
                checkIfAllSelected()
            }
        }

        // ✅ 식사 선택
        mealButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedMealIndex = index
                mealButtons.forEach { it.setBackgroundColor(Color.LTGRAY) }
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brawn_600))
                checkIfAllSelected()
            }
        }

        // ✅ 이미지 박스 클릭 시 메뉴 선택
        for (i in 0 until imageBoxContainer.childCount) {
            (imageBoxContainer.getChildAt(i) as FrameLayout).setOnClickListener {
                val dialog = MenuSelectDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt("selectedDateIndex", selectedDateIndex)
                        putInt("selectedMealIndex", selectedMealIndex)
                    }
                }
                dialog.show(parentFragmentManager, "MenuSelectDialog")
            }
        }

        // ✅ 다음 화면 이동
        nextButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecordStep2Fragment())
                .addToBackStack(null)
                .commit()
        }

        checkIfAllSelected()
    }

    private fun checkIfAllSelected() {
        Log.d("RecordStep1", "날짜=$selectedDateIndex, 식사=$selectedMealIndex, 이미지=$isImageAdded")
        nextButton.isEnabled = selectedDateIndex != -1 && selectedMealIndex != -1 && isImageAdded
    }
}
