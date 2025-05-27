package com.example.vkr.presentation.components

import androidx.annotation.DrawableRes
import com.example.vkr.R

@DrawableRes
fun getAchievementDrawable(resId: Int): Int {
    return when (resId) {
        1 -> R.drawable.test91
        2 -> R.drawable.test41
        3 -> R.drawable.test42
        4 -> R.drawable.test92
        5 -> R.drawable.test43
        6 -> R.drawable.test44
        7 -> R.drawable.test45
        else -> R.drawable.test41
    }
}