package com.example.solutionchallenge

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class UserInfoInputActivity : AppCompatActivity() {

    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_input)

        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        ageInput = findViewById(R.id.ageInput)
        nextButton = findViewById(R.id.nextButton)

        val inputWatcher = {
            val allFilled = heightInput.text.isNotBlank() &&
                    weightInput.text.isNotBlank() &&
                    ageInput.text.isNotBlank()

            nextButton.isEnabled = allFilled
            nextButton.setBackgroundColor(
                if (allFilled) Color.parseColor("#FFA726") else Color.GRAY
            )
        }

        // 모든 EditText에 addTextChangedListener 추가
        heightInput.addTextChangedListener { inputWatcher() }
        weightInput.addTextChangedListener { inputWatcher() }
        ageInput.addTextChangedListener { inputWatcher() }

        nextButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
