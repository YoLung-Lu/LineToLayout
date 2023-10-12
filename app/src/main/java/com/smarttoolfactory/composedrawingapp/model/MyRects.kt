package com.smarttoolfactory.composedrawingapp.model

import android.graphics.RectF

class MyRects(
    val rects: List<RectF>
) {
    companion object {
        val EMPTY = MyRects(emptyList())
        val TEST = MyRects(listOf(
            RectF(0f, 0f, 50f, 50f),
            RectF(100f, 100f, 200f, 200f)
        ))
    }
}