package com.example.solutionchallenge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// UserInfoInputActivity.kt
class UserInfoInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_input)

        val nextButton = findViewById<Button>(R.id.nextButton)

        // 사용자가 값을 입력하면 Next 버튼 활성화
        nextButton.setOnClickListener {
            // 입력된 정보를 저장하고, 홈 화면으로 이동
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
