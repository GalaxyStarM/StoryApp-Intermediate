package id.ac.unri.storyapp.utils

import android.animation.ObjectAnimator
import android.view.View

fun View.animateVisibility(isVisible: Boolean, duration: Long = 300) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration).start()
}