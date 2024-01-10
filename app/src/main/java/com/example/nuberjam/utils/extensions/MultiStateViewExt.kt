package com.example.nuberjam.utils.extensions

import android.widget.ImageButton
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.nuberjam.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.kennyc.view.MultiStateView

fun MultiStateView.showNuberJamErrorState(
    lottieJson: Int? = R.raw.no_data_animation,
    errorMessage: String,
    onButtonClicked: (() -> Unit)? = null
) {
    val shimmerLoading = findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.stopShimmerAnimation()
    this.viewState = MultiStateView.ViewState.ERROR

    errorMessage.let {
        val tvError = findViewById<TextView>(R.id.tv_error)
        tvError?.text = errorMessage
    }

    lottieJson?.let {
        val imgError = findViewById<LottieAnimationView>(R.id.lt_error)
        imgError?.setAnimation(lottieJson)
        imgError.playAnimation()
        imgError.visible()
    }

    val button = findViewById<ImageButton>(R.id.btn_refresh)
    button?.setOnClickListener {
        onButtonClicked?.invoke()
    }
}

fun MultiStateView.showNuberJamEmptyState(
    lottieJson: Int? = R.raw.no_data_animation,
    emptyMessage: String? = null
) {
    val shimmerLoading = findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.stopShimmerAnimation()
    this.viewState = MultiStateView.ViewState.EMPTY

    emptyMessage?.let {
        val tvError = findViewById<TextView>(R.id.tv_error)
        tvError?.text = emptyMessage
    }

    lottieJson?.let {
        val imgError = findViewById<LottieAnimationView>(R.id.lt_empty)
        imgError?.setAnimation(lottieJson)
        imgError.playAnimation()
        imgError.visible()
    }
}

fun MultiStateView.showNuberJamLoadingState() {
    this.viewState = MultiStateView.ViewState.LOADING
    val shimmerLoading = this.getView(MultiStateView.ViewState.LOADING)
        ?.findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.startShimmerAnimation()
}

fun MultiStateView.showNuberJamDefaultState() {
    val shimmerLoading = this.getView(MultiStateView.ViewState.LOADING)
        ?.findViewById<ShimmerFrameLayout>(R.id.shimmer_loading)
    shimmerLoading?.stopShimmerAnimation()
    this.viewState = MultiStateView.ViewState.CONTENT
}