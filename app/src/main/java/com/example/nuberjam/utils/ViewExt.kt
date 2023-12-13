package com.example.nuberjam.utils

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleIf(condition: () -> Boolean) {
    if (condition.invoke()) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun View.onlyVisibleIf(condition: () -> Boolean) {
    if (condition.invoke()) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}