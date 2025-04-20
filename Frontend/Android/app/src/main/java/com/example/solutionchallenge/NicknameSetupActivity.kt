import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.solutionchallenge.R

// NicknameSetupActivity.kt
class NicknameSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nickname_setup)

        val nextButton = findViewById<Button>(R.id.nextButton)
        val nicknameInput = findViewById<EditText>(R.id.nicknameInput)

        nicknameInput.addTextChangedListener {
            // 이름을 입력하면 Next 버튼 활성화
            nextButton.isEnabled = it.toString().isNotEmpty()
            nextButton.setBackgroundColor(
                if (nextButton.isEnabled) Color.parseColor("#FFA726") else Color.GRAY
            )
        }

        nextButton.setOnClickListener {
            // 닉네임을 저장하고, 신체 정보 입력 화면으로 이동
            startActivity(Intent(this, UserInfoInputActivity::class.java))
        }
    }
}
