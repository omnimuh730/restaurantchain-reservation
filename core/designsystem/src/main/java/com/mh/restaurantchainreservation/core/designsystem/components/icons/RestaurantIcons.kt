package com.mh.restaurantchainreservation.core.designsystem.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object RestaurantIcons {
    val Star: ImageVector
        get() {
            if (_star != null) return _star!!
            _star = ImageVector.Builder(
                name = "Star",
                defaultWidth = 32.dp,
                defaultHeight = 32.dp,
                viewportWidth = 32f,
                viewportHeight = 32f
            ).path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(15.1f, 1.58f)
                lineToRelative(-4.13f, 8.88f)
                lineToRelative(-9.86f, 1.27f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.54f, 1.74f)
                lineToRelative(7.3f, 6.57f)
                lineToRelative(-1.97f, 9.85f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.48f, 1.06f)
                lineToRelative(8.62f, -5f)
                lineToRelative(8.63f, 5f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.48f, -1.06f)
                lineToRelative(-1.97f, -9.85f)
                lineToRelative(7.3f, -6.57f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.55f, -1.73f)
                lineToRelative(-9.86f, -1.28f)
                lineToRelative(-4.12f, -8.88f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.82f, 0f)
                close()
            }.build()
            return _star!!
        }

    private var _star: ImageVector? = null
}
