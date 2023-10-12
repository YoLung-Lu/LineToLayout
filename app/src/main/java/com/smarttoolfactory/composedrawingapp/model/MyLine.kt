package com.smarttoolfactory.composedrawingapp.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.smarttoolfactory.composedrawingapp.util.PathMeasureUtil

sealed class MyLine(
    open val path: Path,
    open val pathProperties: PathProperties
)

data class UsersLine(
    override val path: Path,
    override val pathProperties: PathProperties = PathProperties.DEFAULT
) : MyLine(path, pathProperties) {

    val isEmpty get() = path.isEmpty

    companion object {
        val EMPTY = UsersLine(Path())
    }
}

data class SampleLine(
    override val path: Path,
    override val pathProperties: PathProperties = DIRECT_LINE_PROPERTIES
): MyLine(path, pathProperties) {

    companion object {
        private val DIRECT_LINE_PROPERTIES = PathProperties(
            strokeWidth = 5f,
            color = Color.Cyan
        )

        fun fromUsersLine(usersLine: UsersLine): SampleLine {
            val sampler = PathMeasureUtil.sampler(usersLine.path)
            return SampleLine(sampler)
        }
    }
}