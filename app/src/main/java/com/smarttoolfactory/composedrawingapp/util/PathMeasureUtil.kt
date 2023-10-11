package com.smarttoolfactory.composedrawingapp.util

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure

object PathMeasureUtil {
    private val mutex = Any()

    /**
     * TODO: Find a good value for distance.
     */
    fun sampler(path: Path): Path {
        if (path.isEmpty) return path

        synchronized(mutex) {
            val measurer = measurePath(path)
            val output = Path()
            val stepDistance = distanceForEachStep(measurer.length).coerceAtLeast(150f)
            var distanceToMeasure = 0f
            val initialPoint = measurer.getPosition(distanceToMeasure)
            output.moveTo(initialPoint.x, initialPoint.y)

            while (distanceToMeasure < measurer.length) {
                val point = measurer.getPosition(distanceToMeasure)
                distanceToMeasure += stepDistance
                output.lineTo(point.x, point.y)
            }

            val lastPoint = measurer.getPosition(measurer.length)
            output.lineTo(lastPoint.x, lastPoint.y)

            return output
        }
    }

    private fun measurePath(path: Path): PathMeasure {
        return PathMeasure().apply { setPath(path, false) }
    }

    /**
     * Returns the distance of each step.
     */
    private fun distanceForEachStep(length: Float): Float {
        val step = (length * 1000).coerceIn(10f, 1000f)
        return length / step
    }
}