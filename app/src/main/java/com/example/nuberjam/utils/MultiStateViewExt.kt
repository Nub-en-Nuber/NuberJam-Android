package com.example.nuberjam.utils

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.kennyc.view.MultiStateView

fun MultiStateView.showNuberJamErrorState(
    lottieJson: String? = null,
    emptyMessage: String? = null,
    onButtonClicked: (() -> Unit)? = null
) {
    val shimmerLoading = findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.stopShimmerAnimation()
    this.viewState = MultiStateView.ViewState.EMPTY

    emptyMessage?.let {
        val tvError = findViewById<TextView>(R.id.tv_error)
        tvError?.text = emptyMessage
    }

    lottieJson?.let {
        val imgError = findViewById<LottieAnimationView>(R.id.lt_data_not_available)
        imgError?.setAnimation(lottieJson)
        imgError.visible()
    }

    val button = findViewById<ImageButton>(R.id.btn_refresh)
    button?.setOnClickListener {
        onButtonClicked?.invoke()
    }
}

fun MultiStateView.showNuberJamLoadingState() {
    this.viewState = MultiStateView.ViewState.LOADING
    val shimmerLoading = this.getView(MultiStateView.ViewState.LOADING)
        ?.findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.startShimmerAnimation()
}