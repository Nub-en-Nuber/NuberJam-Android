package com.example.nuberjam.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ActivityMainBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadNavigationData()

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        showSnackbarObserve()
    }

    private fun loadNavigationData() {
        if (intent != null) {
            val username = intent.extras?.let { MainActivityArgs.fromBundle(it).username }
            if (username != null) viewModel.setSnackbar(
                getString(R.string.login_success_message, username), CustomSnackbar.STATE_SUCCESS
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