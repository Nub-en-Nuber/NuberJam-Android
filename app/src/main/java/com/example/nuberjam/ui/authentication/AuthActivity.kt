package com.example.nuberjam.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ActivityAuthBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.ui.customview.CustomSnackbar

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val initViewModel: AuthViewModel by viewModels {
            factory
        }
        viewModel = initViewModel

        loadNavigationData()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        val navController = navHostFragment.navController

        showSnackbarObserve()
    }

    private fun loadNavigationData() {
        if (intent != null) {
            val username = intent.extras?.let { AuthActivityArgs.fromBundle(it).username }
            if (username != null) viewModel.setSnackbar(
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