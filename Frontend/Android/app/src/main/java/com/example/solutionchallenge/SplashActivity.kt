import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.solutionchallenge.OnboardingActivity
import com.example.solutionchallenge.R

// SplashActivity.kt
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // 일정 시간 후, 온보딩 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish() // SplashActivity 종료
        }, 2000) // 2초 후 넘어감
    }
}
