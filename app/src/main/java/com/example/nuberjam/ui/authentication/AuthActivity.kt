package com.example.nuberjam.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ActivityAuthBinding
import com.example.nuberjam.ui.SplashViewModel
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.MainActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelFactory.getInstance(this)
        val viewModel: SplashViewModel by viewModels { factory }

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
                    finish()
                }
            }
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadNavigationData()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        val navController = navHostFragment.navController
    }

    private fun loadNavigationData() {
        if (intent != null) {
            val username = intent.extras?.let { AuthActivityArgs.fromBundle(it).username }
            if (username != null)
                showSnackbar(
                    getString(R.string.logout_success_message, username),
                    CustomSnackbar.STATE_SUCCESS
                )
        }
    }

    private fun showSnackbar(
        message: String,
        state: Int,
        length: Int = CustomSnackbar.LENGTH_LONG
    ) {
        val customSnackbar =
            CustomSnackbar.build(layoutInflater, binding.root, length)
        customSnackbar.setMessage(message)
        customSnackbar.setState(state)
        customSnackbar.show()
    }
}