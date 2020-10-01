package com.example.cross_zero_rubic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder

class DrawThread() : Thread() {

    private var running = false
    private lateinit var surfaceHolder: SurfaceHolder
    private var p: Paint = Paint()

    constructor(surfaceHolder: SurfaceHolder) : this() {

        this.surfaceHolder = surfaceHolder
    }

    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {

        var canvas: Canvas? = null

        while (running) {

            try {
                canvas = surfaceHolder.lockCanvas(null)
                if (canvas == null) continue
                canvas.drawColor(Color.GREEN)
                onDraw(canvas)
            }
            finally {

                if(canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun onDraw(canvas: Canvas) {

        p.color = Color.WHITE
        canvas.drawPaint(p)
        p.color = Color.BLACK
        if (MainActivity.currentValue == Value.CROSS) {

            canvas.drawLine(((200F * MainActivity.SIZE) + 20), 10F, ((200F * MainActivity.SIZE) + 20) + 100, 210F, p)
            canvas.drawLine(((200F * MainActivity.SIZE) + 20) + 100, 10F, ((200F * MainActivity.SIZE) + 20), 210F, p)
        }
        else canvas.drawCircle(((200F * MainActivity.SIZE) + 100), 200F, 75F, p)

        canvas.drawLine(10F, 10F, ((200F * MainActivity.SIZE) + 10), 10F, p)
        canvas.drawLine(((200F * MainActivity.SIZE) + 10), 10F, ((200F * MainActivity.SIZE) + 10), ((200F * MainActivity.SIZE) + 10), p)
        canvas.drawLine(((200F * MainActivity.SIZE) + 10), ((200F * MainActivity.SIZE) + 10), 10F, ((200F * MainActivity.SIZE) + 10), p)
        canvas.drawLine(10F, ((200F * MainActivity.SIZE) + 10), 10F, 10F, p)

        for (i in 0 until MainActivity.SIZE) canvas.drawLine(10F + 200F * i, 10F, 10F + 200F * i, ((200F * MainActivity.SIZE) + 10), p)
        for (i in 0 until MainActivity.SIZE) canvas.drawLine(10F, 10F + 200F * i, ((200F * MainActivity.SIZE) + 10), 10F + 200F * i, p)

        for (i in MainActivity.front.cages.indices) {

            val value: Value = MainActivity.front.cages[i]
            val x: Int = i % MainActivity.SIZE * 200 + 10
            val y: Int = i / MainActivity.SIZE * 200 + 10
            if (value == Value.CROSS) {
                canvas.drawLine(x.toFloat(), y.toFloat(), x + 200F, y + 200F, p)
                canvas.drawLine(x + 200F, y.toFloat(), x.toFloat(), y + 200F, p)
            }
            else if (value == Value.ZERO) canvas.drawCircle((x.toFloat() + 75), (y.toFloat() + 75), 75F, p)
        }
    }
}