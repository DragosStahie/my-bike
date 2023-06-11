package com.mybike.bikes.scrollview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import com.mybike.common.enums.BikeType
import com.mybike.common.dpToPx
import kotlin.math.abs

class SnapHorizontalScrollView(context: Context, attributeSet: AttributeSet) :
    HorizontalScrollView(context, attributeSet) {

    private val minDistance = 300
    private var startX = 0f
    private var endX = 0f
    private var currentPosition = BikeType.MOUNTAIN_BIKE
    private var positionStepWidth = 0
    var onPositionChangedListener: OnPositionChangedListener? = null

    fun updateCurrentPosition(position: BikeType) {
        currentPosition = position
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        positionStepWidth = width - 75.dpToPx()
        scrollToCurrentPosition()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> startX = event.x
            MotionEvent.ACTION_UP -> {
                endX = event.x
                if (abs(startX - endX) > minDistance) {
                    if (startX < endX) {
                        scrollLeft()
                    } else {
                        scrollRight()
                    }
                } else {
                    scrollToCurrentPosition()
                }
            }
        }
        return true
    }

    private fun scrollLeft() {
        if (currentPosition.ordinal > 0) {
            currentPosition = BikeType.values()[currentPosition.ordinal - 1]
        }
        scrollToCurrentPosition()
        onPositionChangedListener?.onPositionChanged(currentPosition.ordinal)
    }

    private fun scrollRight() {
        if (currentPosition.ordinal < BikeType.values().size - 1) {
            currentPosition = BikeType.values()[currentPosition.ordinal + 1]
        }
        scrollToCurrentPosition()
        onPositionChangedListener?.onPositionChanged(currentPosition.ordinal)
    }

    private fun scrollToCurrentPosition() {
        smoothScrollTo(currentPosition.ordinal * positionStepWidth, 0)
    }
}