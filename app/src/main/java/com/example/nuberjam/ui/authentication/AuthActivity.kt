package com.example.nuberjam.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ActivityAuthBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private var doSnackbar = true

    companion object {
        private const val STATE_SNACKBAR = "state_snackbar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            doSnackbar = savedInstanceState.getBoolean(STATE_SNACKBAR, true);
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadNavigationData()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment

        showSnackbarObserve()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SNACKBAR, false);
    }

    private fun loadNavigationData() {
        if (intent != null) {
            val username = intent.extras?.let { AuthActivityArgs.fromBundle(it).username }
            if (username != null && username != "null" && doSnackbar) viewModel.setSnackbar(
                getString(R.string.logout_success_message, username), CustomSnackbar.STATE_SUCCESS
            )
        }
    }

    private fun showSnackbarObserve() {
        viewModel.snackbarState.observe(this) { event ->
            event.getContentIfNotHandled()?.let { snackbarState ->
                val customSnackbar =
                    CustomSnackbar.build(layoutInflater, binding.root, snackbarState.length)
                customSnackbar.setMessage(snackbarState.message)
                customSnackbar.setState(snackbarState.state)
                customSnackbar.show()
            }
        }
    }
}