package com.smarttoolfactory.composedrawingapp.util

import android.graphics.PointF
import kotlin.math.absoluteValue
import kotlin.math.sqrt


fun PointF.distanceTo(other: PointF): Float {
    val dx = x - other.x
    val dy = y - other.y
    return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
}

/**
 *                      * p2
 *                      |
 *                      | edge2
 *                      |
 *    *-----------------*
 *    p1       edge1
 */
fun findLongEdgeBetweenPoints(p1: PointF, p2: PointF): Float {
    val dx = (p1.x - p2.x).absoluteValue
    val dy = (p1.y - p2.y).absoluteValue
    return if (dx > dy) dx else dy
}