package ru.giksengik.customviewsample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveView: View {

    var itemWidth :Int = 0
    var originalData = WaveRepository.waveData
    lateinit var linePaint  : Paint
    lateinit var measuredData : IntArray
    lateinit var wavePath : Path

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        itemWidth = resources.getDimension(R.dimen.wave_view_item_width).toInt()

        linePaint = Paint()
        linePaint.style = Paint.Style.STROKE
        linePaint.color = Color.BLACK
        linePaint.strokeWidth = itemWidth.toFloat()

        this.wavePath = Path()

    }

    constructor(context: Context) : super(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        var width = originalData.size * itemWidth * 2 - itemWidth

        when(widthMode){
            MeasureSpec.EXACTLY -> {
                measuredData = if (width > measuredWidth) {
                    val itemCount = (measuredWidth + itemWidth) / (itemWidth * 2)
                    originalData.copyOfRange(0, itemCount)
                } else {
                    originalData
                }
                width = measuredWidth
            }
            MeasureSpec.AT_MOST -> {
                if (width > measuredWidth) {
                    val itemCount = (measuredWidth + itemWidth) / (itemWidth * 2)
                    measuredData = originalData.copyOfRange(0, itemCount)
                    width = measuredWidth
                } else {
                    measuredData = originalData
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                width = measuredWidth;
                measuredData = originalData;
            }
        }
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (!this::measuredData.isInitialized) {
            return
        }
        wavePath.reset()
        val measuredHeight = measuredHeight
        var currentX = itemWidth
        for (data in measuredData) {
            val height = data.toFloat() / WaveRepository.MAX_VOLUME * measuredHeight
            val startY = measuredHeight.toFloat() / 2f - height / 2f
            val endY = startY + height
            wavePath.moveTo(currentX.toFloat(), startY)
            wavePath.lineTo(currentX.toFloat(), endY)
            currentX += itemWidth * 2
        }
        canvas.drawPath(wavePath, linePaint)
    }

}