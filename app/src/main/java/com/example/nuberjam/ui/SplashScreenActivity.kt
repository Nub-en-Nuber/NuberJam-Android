package com.example.nuberjam.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.nuberjam.R
import com.example.nuberjam.ui.authentication.AuthActivity
import com.example.nuberjam.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SplashViewModel by viewModels()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                return@setKeepOnScreenCondition viewModel.isLoading
            }
        }

        viewModel.getLoginState().observe(this) { hasLogin ->
            if (hasLogin != null) {
                viewModel.isLoading = false
                if (hasLogin){
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                }
                else {
                    startActivity(
                        Intent(
                            this,
                            AuthActivity::class.java
                        )
                    )
                }
                finish()
            }
        }
        
        setContentView(R.layout.activity_splash_screen)
    }
}
