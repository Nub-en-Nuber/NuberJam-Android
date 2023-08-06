package com.example.nuberjam.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.ui.authentication.AuthActivity
import com.example.nuberjam.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val factory = ViewModelFactory.getInstance(this)
        val viewModel: SplashViewModel by viewModels { factory }

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getLoginState().observe(this) { hasLogin ->
                if (hasLogin != null) {
                    if (hasLogin) startActivity(
                        Intent(
                            this@SplashScreenActivity,
                            MainActivity::class.java
                        )
                    )
                    else startActivity(
                        Intent(
                            this@SplashScreenActivity,
                            AuthActivity::class.java
                        )
                    )
                    finish()
                }
            }
        }, 3000)
    }
}