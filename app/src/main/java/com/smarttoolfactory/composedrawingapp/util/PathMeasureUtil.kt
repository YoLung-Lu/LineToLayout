package com.smarttoolfactory.composedrawingapp.util

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
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
            if (initialPoint.isUnspecified) return path // Will throw exception if unspecified
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

    fun findTurningPoints(path: Path): List<PointF> {
        if (path.isEmpty) return emptyList()

        var directionX: Int
        var directionY: Int

        synchronized(mutex) {
            val measurer = measurePath(path)

            val output = mutableListOf<PointF>()
            val stepDistance = distanceForEachStep(measurer.length)
            var distanceToMeasure = 0f
            val initialTangent = measurer.getTangent(distanceToMeasure)
            if (initialTangent.isUnspecified) return emptyList() // Will throw exception if unspecified
            val initialDirection = directionFromTangent(initialTangent)
            directionX = initialDirection.first
            directionY = initialDirection.second

            val initialPoint = measurer.getPosition(distanceToMeasure).toPointF()
            output.add(initialPoint)

            while (distanceToMeasure < measurer.length) {
                distanceToMeasure += stepDistance
                val tangent = measurer.getTangent(distanceToMeasure)
                val direction = directionFromTangent(tangent)
                val isDirectionChanged = directionX != direction.first || directionY != direction.second
                if (isDirectionChanged) {
                    val turningPoint = measurer.getPosition(distanceToMeasure).toPointF()
                    output.add(turningPoint)
                }

                directionX = direction.first
                directionY = direction.second
            }

            val lastPoint = measurer.getPosition(measurer.length).toPointF()
            output.add(lastPoint)

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

    private fun directionFromTangent(tangent: Offset): Pair<Int, Int> {
        val directionX = if (tangent.x > 0) 1 else -1
        val directionY = if (tangent.y > 0) 1 else -1
        return Pair(directionX, directionY)
    }

    private fun Offset.toPointF() = PointF(x, y)
}