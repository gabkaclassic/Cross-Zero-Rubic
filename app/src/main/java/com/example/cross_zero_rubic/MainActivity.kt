package com.example.cross_zero_rubic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity: AppCompatActivity(), View.OnTouchListener {

    companion object {

        const val SIZE = 3

        internal lateinit var currentValue: Value
        private lateinit var currentOrientation: Orientation
        internal lateinit var front: Edge
        internal lateinit var left: Edge
        internal lateinit var right: Edge
        internal lateinit var top: Edge
        internal lateinit var bottom: Edge
        internal lateinit var back: Edge
    }

    private var x: Float = 0F
    private var y: Float = 0F
    private var countZero = 0
    private var countCross = 0
    private var cages: Int = 0

    private var currentLine: IntArray
    private lateinit var draw: DrawView

    init {

        cages = SIZE * SIZE * 6

        front = Edge()
        back = Edge()
        left = Edge()
        right = Edge()
        top = Edge()
        bottom = Edge()

        currentOrientation = Orientation.UP
        currentLine = intArrayOf(0, 1, 2)

        for (i in 0 until SIZE) currentLine[i] = i
        for (i in 0 until SIZE) currentLine[i] = i

        currentValue = Value.ZERO
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        draw = DrawView(this)
        draw.setOnTouchListener(this)
        setContentView(draw)

    }

    private fun motion(index: Int) {

        if (front.cages[index] == Value.NULL) {

            front.cages[index] = currentValue
            changeMotion()
            cages--

            if (cages == 0) {

                checkWinner()
                Toast.makeText(this, ("Cross: $countCross, zero: $countZero"), Toast.LENGTH_LONG)
                    .show()
            }
        }

        draw.invalidate()
    }

    private fun rotation() {

        val cashCages: Array<Value> = Arrays.copyOf(front.cages, front.cages.size)
        val indexes = IntArray(SIZE * SIZE)

        for (i in indexes.indices) indexes[i] = i

        when (currentOrientation) {

            Orientation.UP -> {

                front.setCages(indexes, bottom.getCages(indexes))
                bottom.setCages(indexes, back.getCages(indexes))
                back.setCages(indexes, top.getCages(indexes))
                top.setCages(indexes, cashCages)
            }
            Orientation.DOWN -> {

                front.setCages(indexes, top.getCages(indexes))
                top.setCages(indexes, back.getCages(indexes))
                back.setCages(indexes, bottom.getCages(indexes))
                bottom.setCages(indexes, cashCages)
            }
            Orientation.LEFT -> {

                front.setCages(indexes, right.getCages(indexes))
                right.setCages(indexes, back.getCages(indexes))
                back.setCages(indexes, left.getCages(indexes))
                left.setCages(indexes, cashCages)
            }
            Orientation.RIGHT -> {

                front.setCages(indexes, left.getCages(indexes))
                left.setCages(indexes, back.getCages(indexes))
                back.setCages(indexes, right.getCages(indexes))
                right.setCages(indexes, cashCages)
            }
        }

        draw.invalidate()
    }

    private fun shift() {

        val cashValues: Array<Value> = Array(SIZE) { Value.NULL }

        for (i in 0 until SIZE) cashValues[i] = front.cages[currentLine[i]]

        when (currentOrientation) {

            Orientation.UP -> {

                front.setCages(currentLine, bottom.getCages(currentLine))
                bottom.setCages(currentLine, back.getCages(currentLine))
                back.setCages(currentLine, top.getCages(currentLine))
                top.setCages(currentLine, cashValues)
            }
            Orientation.DOWN -> {

                front.setCages(currentLine, top.getCages(currentLine))
                top.setCages(currentLine, back.getCages(currentLine))
                back.setCages(currentLine, bottom.getCages(currentLine))
                bottom.setCages(currentLine, cashValues)
            }
            Orientation.LEFT -> {

                front.setCages(currentLine, right.getCages(currentLine))
                right.setCages(currentLine, back.getCages(currentLine))
                back.setCages(currentLine, left.getCages(currentLine))
                left.setCages(currentLine, cashValues)
            }
            Orientation.RIGHT -> {

                front.setCages(currentLine, left.getCages(currentLine))
                left.setCages(currentLine, back.getCages(currentLine))
                back.setCages(currentLine, right.getCages(currentLine))
                right.setCages(currentLine, cashValues)
            }
        }

        changeMotion()
        draw.invalidate()
    }

    private fun checkWinner() {

        checkEdge(front)
        checkEdge(back)
        checkEdge(top)
        checkEdge(bottom)
        checkEdge(left)
        checkEdge(right)
    }

    private fun checkEdge(edge: Edge?) {  //Костыли

        val cages: Array<Value> = edge!!.cages
        var flag: Boolean

        flag = (cages[0] == cages[1]) && (cages[1] == cages[2])
        checkCage(flag, cages[0])
        flag = (cages[3] == cages[4]) && (cages[4] == cages[5])
        checkCage(flag, cages[3])
        flag = (cages[6] == cages[7]) && (cages[7] == cages[8])
        checkCage(flag, cages[6])
        flag = (cages[0] == cages[3]) && (cages[3] == cages[6])
        checkCage(flag, cages[0])
        flag = (cages[1] == cages[4]) && (cages[4] == cages[7])
        checkCage(flag, cages[1])
        flag = (cages[2] == cages[5]) && (cages[5] == cages[8])
        checkCage(flag, cages[2])
        flag = (cages[0] == cages[4]) && (cages[4] == cages[8])
        checkCage(flag, cages[0])
        flag = (cages[2] == cages[4]) && (cages[4] == cages[6])
        checkCage(flag, cages[2])
    }

    private fun checkCage(b: Boolean, v: Value) {

        if (b) {
            if (v == Value.CROSS) countCross++
            else countZero++
        }
    }

    private fun changeMotion() {

        currentValue = if (currentValue == Value.CROSS) Value.ZERO
        else Value.CROSS
    }

    class Edge {

        var cages: Array<Value> = Array(9) { Value.NULL }

        init {
            Arrays.fill(cages, Value.NULL)
        }


        fun setCages(indexes: IntArray, values: Array<Value>) {

            for (i in indexes.indices) cages[indexes[i]] = values[i]
        }

        fun getCages(indexes: IntArray): Array<Value> {

            val values: Array<Value> = Array(indexes.size) { Value.NULL }

            for (i in indexes.indices) values[i] = cages[indexes[i]]

            return values
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {

        val x: Float = event.x
        val y: Float = event.y

        when (event.action) {

            ACTION_DOWN -> {

                this.x = x
                this.y = y
            }

            ACTION_UP -> {

                if(getDistance(x, y) > 50) {

                    setOrientation(x, y)

                    if((x > ((SIZE * 200) + 50)) || (y > ((SIZE * 200) + 50))) rotation()
                    else {

                        setCurrentLine(x, y)
                        shift()
                    }
                }
                else motion(getIndex(x, y))
            }
        }

        return true
    }

    private fun setOrientation(x: Float, y: Float) {

        val dX: Float = this.x - x
        val dY: Float = this.y - y

            currentOrientation = if(dX.absoluteValue > dY.absoluteValue) {

                if(dX > 0) Orientation.LEFT
                else Orientation.RIGHT
            } else {

                if(dY > 0) Orientation.UP
                else Orientation.DOWN
            }
    }

    private fun getIndex(x: Float, y: Float): Int {

        val indX = ((x - 10) / 200).toInt()
        val indY = ((y - 10) / 200).toInt()

        var index = 0

        if (indX == 0) {

            if (indY == 1) index = 3
            else if (indY == 2) index = 6
        }
        else if (indX == 1) {

            when (indY) {
                0 -> index = 1
                1 -> index = 4
                2 -> index = 7
            }
        }
        else if (indX == 2) {

            when (indY) {
                0 -> index = 2
                1 -> index = 5
                2 -> index = 8
            }
        }

        return index
    }

    private fun getDistance(x: Float, y: Float): Float {

        val dX: Float = this.x - x
        val dY: Float = this.y - y

        return sqrt(dX.pow(2) + dY.pow(2))
    }

    private fun setCurrentLine(x: Float, y: Float) {

            currentLine =
                if ((currentOrientation == Orientation.LEFT) || (currentOrientation == Orientation.RIGHT)) {

                    when {
                        y < 210 -> intArrayOf(0, 1, 2)
                        y < 410 -> intArrayOf(3, 4, 5)
                        else -> intArrayOf(6, 7, 8)
                    }
                }
                else {

                    when {
                        x < 210 -> intArrayOf(0, 3, 6)
                        x < 410 -> intArrayOf(1, 4, 7)
                        else -> intArrayOf(2, 5, 8)
                    }
                }
        }
}

enum class Value {
    CROSS, ZERO, NULL
}

internal enum class Orientation {
    UP, DOWN, LEFT, RIGHT
}
