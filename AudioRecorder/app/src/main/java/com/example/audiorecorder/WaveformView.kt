package com.example.audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class WaveformView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var width = 9f
    private var distanceBetweenSpikes = 6f

    private var screenWidth = 0f
    private var screenHeight = 400f

    private var maxSpikes = 0
    init {
        paint.color = Color.rgb(244, 81, 30)
        screenWidth = resources.displayMetrics.widthPixels.toFloat()

        maxSpikes = (screenWidth / (width + distanceBetweenSpikes)).toInt()
    }

    fun addAmplitude(ampl: Float){

        var norm = Math.min(ampl.toInt()/7, 400).toFloat()
        amplitudes.add(norm)

        var amps = amplitudes.takeLast(maxSpikes)
        for(i in amps.indices){
            var left = screenWidth - i*(width+distanceBetweenSpikes)
            var top = screenHeight/2 - amps[i]/2
            var right = left + width
            var bottom = top + amps[i]

            spikes.add(RectF(left, top, right, bottom))
        }
        invalidate()
    }
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        spikes.forEach{
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
        spikes.clear()
    }
}