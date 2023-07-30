package com.example.nuberjam.ui.customview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.nuberjam.R
import com.example.nuberjam.databinding.SnackbarBinding
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar private constructor(
    layoutInflater: LayoutInflater,
    private val context: Context,
    private val snackbar: Snackbar
) {
    private val snackbarBinding: SnackbarBinding = SnackbarBinding.inflate(layoutInflater)
    private val snackbarLayout: Snackbar.SnackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    private var state: Int? = null
    private var message: String? = null

    init {
        clearSnackbarLayout()
    }

    companion object {
        const val LENGTH_SHORT = Snackbar.LENGTH_SHORT
        const val LENGTH_LONG = Snackbar.LENGTH_LONG
        const val LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE

        const val STATE_ERROR = 0
        const val STATE_SUCCESS = 1

        const val COLOR_SUCCESS = R.color.success_main
        const val COLOR_ERROR = R.color.error_main

        const val DRAWABLE_SUCCESS = R.drawable.ic_check_circle_white
        const val DRAWABLE_ERROR = R.drawable.ic_error_circle_white

        fun build(
            layoutInflater: LayoutInflater,
            view: View,
            length: Int
        ): CustomSnackbar {
            val snackbar = Snackbar.make(view, "", length)
            return CustomSnackbar(layoutInflater, view.context, snackbar)
        }
    }

    private fun clearSnackbarLayout() {
        snackbarLayout.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        )
        snackbarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .visibility = View.GONE
        snackbarLayout.setPadding(0, 0, 0, 0)
    }

    fun show() {
        if (message != null && state != null) {
            val tvTextSnackbar = snackbarBinding.tvTextSnackbar
            tvTextSnackbar.text = message
            if (state == STATE_SUCCESS) {
                setColor(COLOR_SUCCESS)
                setDrawable(DRAWABLE_SUCCESS)
            } else {
                setColor(COLOR_ERROR)
                setDrawable(DRAWABLE_ERROR)
            }
            snackbarLayout.addView(snackbarBinding.root, 0)
            snackbar.show()
        }
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setState(state: Int) {
        this.state = state
    }

    private fun setColor(colorId: Int) {
        val cardSnackbar = snackbarBinding.cardSnackbar
        cardSnackbar.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                context,
                colorId
            )
        )
    }

    private fun setDrawable(drawableId: Int) {
        val ivIconSnackbar = snackbarBinding.ivIconSnackbar
        ivIconSnackbar.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                drawableId
            )
        )
    }
}