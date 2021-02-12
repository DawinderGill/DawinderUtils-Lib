package com.dawinderutilslib.piechart

import kotlin.math.abs
import kotlin.math.roundToInt

class MyPieHelper {
    private var startDegree: Float = 0.toFloat()
    private var endDegree: Float = 0.toFloat()
    private var targetStartDegree: Float = 0.toFloat()
    private var targetEndDegree: Float = 0.toFloat()
    private var title: String? = null
    private var color: Int = 0
    private var sweepDegree: Float = 0.toFloat()
    private var percentage: Float = 0.toFloat()

    internal var velocity = 5

    /**
     * @param percent from 0 to 100
     */
    constructor(percent: Float) : this(percent, null, 0)

    constructor(percent: Float, color: Int) : this(percent, null, color)

    /**
     * @param percent from 0 to 100
     * @param title
     */
    constructor(percent: Float, title: String):this(percent, title,0)

    /**
     * @param percent from 0 to 100
     * @param title
     * @param color
     */
    constructor(percent: Float, title: String?, color: Int) {
        this.sweepDegree = percent * 360 / 100
        this.title = title
        this.color = color
        this.percentage = percent
    }

    constructor(startDegree: Float, endDegree: Float, targetPie: MyPieHelper) {
        this.startDegree = startDegree
        this.endDegree = endDegree
        targetStartDegree = targetPie.getStartDegree()
        targetEndDegree = targetPie.getEndDegree()
        this.sweepDegree = targetPie.getSweep()
        this.title = targetPie.getTitle()
        this.color = targetPie.getColor()
        this.percentage = targetPie.getPercentage()
    }

    internal fun setTarget(targetPie: MyPieHelper): MyPieHelper {
        this.targetStartDegree = targetPie.getStartDegree()
        this.targetEndDegree = targetPie.getEndDegree()
        this.title = targetPie.getTitle()
        this.color = targetPie.getColor()
        this.sweepDegree = targetPie.getSweep()
        this.percentage = targetPie.getPercentage()
        return this
    }

    internal fun setDegree(startDegree: Float, endDegree: Float) {
        this.startDegree = startDegree
        this.endDegree = endDegree
    }

    internal fun isColorSetted(): Boolean {
        return color != 0
    }

    internal fun isAtRest(): Boolean {
        return startDegree == targetStartDegree && endDegree == targetEndDegree
    }

    internal fun update() {
        this.startDegree = updateSelf(startDegree, targetStartDegree, velocity)
        this.endDegree = updateSelf(endDegree, targetEndDegree, velocity)
        this.sweepDegree = endDegree - startDegree
    }

    internal fun getPercentStr(): String {
        return percentage.roundToInt().toString() + "%"
    }

    fun getPercentage(): Float {
        return percentage
    }

    fun getColor(): Int {
        return color
    }

    fun getTitle(): String? {
        return title
    }

    fun getSweep(): Float {
        return sweepDegree
    }

    fun getStartDegree(): Float {
        return startDegree
    }

    fun getEndDegree(): Float {
        return endDegree
    }

    private fun updateSelf(origin: Float, target: Float, velocity: Int): Float {
        @Suppress("NAME_SHADOWING") var origin = origin
        if (origin < target) {
            origin += velocity.toFloat()
        } else if (origin > target) {
            origin -= velocity.toFloat()
        }
        if (abs(target - origin) < velocity) {
            origin = target
        }
        return origin
    }
}