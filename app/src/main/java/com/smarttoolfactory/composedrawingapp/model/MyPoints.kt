package com.smarttoolfactory.composedrawingapp.model

import android.graphics.PointF

data class MyPoints(
    val points: List<PointF>
) {
    companion object {
        val EMPTY = MyPoints(emptyList())
    }
}