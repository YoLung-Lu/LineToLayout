package com.smarttoolfactory.composedrawingapp.viewmodel

import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.composedrawingapp.DrawMode
import com.smarttoolfactory.composedrawingapp.gesture.MotionEvent
import com.smarttoolfactory.composedrawingapp.model.SampleLine
import com.smarttoolfactory.composedrawingapp.model.MyLine
import com.smarttoolfactory.composedrawingapp.model.MyPoints
import com.smarttoolfactory.composedrawingapp.model.MyRects
import com.smarttoolfactory.composedrawingapp.model.UsersLine
import com.smarttoolfactory.composedrawingapp.util.PathMeasureUtil
import com.smarttoolfactory.composedrawingapp.util.distanceTo
import com.smarttoolfactory.composedrawingapp.util.findLongEdgeBetweenPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val DEFAULT_MAX_SIZE = 200f

class CanvasViewModel: ViewModel() {
    // Lines.
    private val mutableLineList = mutableListOf<MyLine>()
    private val _lineList = MutableStateFlow(mutableLineList.toList())
    val lineList: StateFlow<List<MyLine>> = _lineList

    // Points from line.
    private val _points = MutableStateFlow(MyPoints.EMPTY)
    val points: StateFlow<MyPoints> = _points

    // Rects.
    private val _rects = MutableStateFlow(MyRects.EMPTY)
    val rects: StateFlow<MyRects> = _rects

    // Always draw.
    private val _drawMode = MutableStateFlow(DrawMode.Draw)
    val drawMode: StateFlow<DrawMode> = _drawMode

    private val _motionEvent = MutableStateFlow(MotionEvent.Idle)
    val motionEvent: StateFlow<MotionEvent> = _motionEvent

    fun updateLine(newLine: UsersLine) {
        // For my use case there should only be 1 line from user.
        mutableLineList.clear()
        mutableLineList.add(newLine)
        // Sampler line.
//        mutableLineList.add(SampleLine.fromUsersLine(newLine))

        // Update Points
        val points = pointsFromLine(newLine)
        _points.value = points
        _rects.value = rectsFromPoints(points)

        updateToFlow()
    }

    fun updateMotionEvent(newMotionEvent: MotionEvent) {
        _motionEvent.value = newMotionEvent
    }

    fun clear() {
        _points.value = MyPoints.EMPTY
        _rects.value = MyRects.EMPTY
        mutableLineList.clear()
        updateToFlow()
    }

    private fun rectsFromPoints(points: MyPoints): MyRects {
        val rects = mutableListOf<RectF>()
        points.points.forEachIndexed { index, point ->
            val prev = points.points.getOrNull(index - 1)
            val next = points.points.getOrNull(index + 1)
            val minSize = findLargeSizeBetweenPoints(prev, point, next)
            rects.add(rectByCenterAndSize(point, minSize))
        }

        return MyRects(rects)
    }

    private fun findLargeSizeBetweenPoints(
        prev: PointF?,
        point: PointF,
        next: PointF?
    ): Size {
        if (prev == null && next == null) return Size(DEFAULT_MAX_SIZE, DEFAULT_MAX_SIZE)

        val d1 = prev?.distanceTo(point) ?: Float.MAX_VALUE
        val d2 = next?.distanceTo(point) ?: Float.MAX_VALUE

        val size = if (prev != null && d1 < d2) {
            findLongEdgeBetweenPoints(prev, point)
        } else if (next != null && d2 < d1) {
            findLongEdgeBetweenPoints(point, next)
        } else {
            // Should not happen.
            DEFAULT_MAX_SIZE
        }

        return Size(size, size)
    }

    private fun rectByCenterAndSize(center: PointF, size: Size): RectF {
        return RectF(
            center.x - size.width / 2,
            center.y - size.height / 2,
            center.x + size.width / 2,
            center.y + size.height / 2
        )
    }

    private fun pointsFromLine(usersLine: UsersLine): MyPoints {
        if (usersLine.isEmpty) return MyPoints.EMPTY

        return MyPoints(PathMeasureUtil.findTurningPoints(usersLine.path))
    }

    private fun updateToFlow() {
        _lineList.value = mutableLineList.toList()
    }
}