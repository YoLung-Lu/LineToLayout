package com.smarttoolfactory.composedrawingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.smarttoolfactory.composedrawingapp.DrawMode
import com.smarttoolfactory.composedrawingapp.gesture.MotionEvent
import com.smarttoolfactory.composedrawingapp.model.SampleLine
import com.smarttoolfactory.composedrawingapp.model.MyLine
import com.smarttoolfactory.composedrawingapp.model.MyPoints
import com.smarttoolfactory.composedrawingapp.model.UsersLine
import com.smarttoolfactory.composedrawingapp.util.PathMeasureUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CanvasViewModel: ViewModel() {
    // Lines.
    private val mutableLineList = mutableListOf<MyLine>()
    private val _lineList = MutableStateFlow(mutableLineList.toList())
    val lineList: StateFlow<List<MyLine>> = _lineList

    // Points from line.
    private val _points = MutableStateFlow(MyPoints.EMPTY)
    val points: StateFlow<MyPoints> = _points

    // Always draw.
    private val _drawMode = MutableStateFlow(DrawMode.Draw)
    val drawMode: StateFlow<DrawMode> = _drawMode

    private val _motionEvent = MutableStateFlow(MotionEvent.Idle)
    val motionEvent: StateFlow<MotionEvent> = _motionEvent

    fun updateLine(newLine: UsersLine) {
        // For my use case there should only be 1 line from user.
        mutableLineList.clear()
        mutableLineList.add(newLine)
        mutableLineList.add(SampleLine.fromUsersLine(newLine))

        // Update Points
        _points.value = pointsFromLine(newLine)

        updateToFlow()
    }

    fun updateMotionEvent(newMotionEvent: MotionEvent) {
        _motionEvent.value = newMotionEvent
    }

    fun clear() {
        _points.value = MyPoints.EMPTY
        mutableLineList.clear()
        updateToFlow()
    }

    private fun pointsFromLine(usersLine: UsersLine): MyPoints {
        if (usersLine.isEmpty) return MyPoints.EMPTY

        return MyPoints(PathMeasureUtil.findTurningPoints(usersLine.path))
    }

    private fun updateToFlow() {
        _lineList.value = mutableLineList.toList()
    }
}