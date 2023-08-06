package com.example.nuberjam.ui.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ActivityAuthBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.profile.ProfileFragment

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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