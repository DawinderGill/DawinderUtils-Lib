package com.dawinderutilslib.piechart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dawinderutilslib.R
import java.util.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MyPieView : View {

    interface OnPieClickListener {
        fun onPieClick(index: Int)
    }

    private var cirPaint: Paint = Paint()
    private var whiteLinePaint: Paint
    private var pieCenterPoint: Point
    private var textPaint: Paint
    private var cirRect: RectF
    private var cirSelectedRect: RectF

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var margin: Int = 0
    private var pieRadius: Int = 0

    private var onPieClickListener: OnPieClickListener? = null

    private var pieHelperList: ArrayList<MyPieHelper> = ArrayList()
    val noSelectedIndex = -999

    private var selectedIndex = noSelectedIndex
    private var showPercentLabel = true
    private val defaultColorList = intArrayOf(
        Color.parseColor("#33B5E5"),
        Color.parseColor("#AA66CC"),
        Color.parseColor("#99CC00"),
        Color.parseColor("#FFBB33"),
        Color.parseColor("#FF4444")
    )
    private val animator = object : Runnable {
        override fun run() {
            var needNewFrame = false
            for (pie in pieHelperList) {
                pie.update()
                if (!pie.isAtRest()) {
                    needNewFrame = true
                }
            }
            if (needNewFrame) {
                postDelayed(this, 10)
            }
            invalidate()
        }
    }

    constructor(context: Context) : this(context, null)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.MyPieView, 0, 0
        )
        try {
            cirPaint.isAntiAlias = true
            cirPaint.color = Color.GRAY
            whiteLinePaint = Paint(cirPaint)
            whiteLinePaint.color = Color.WHITE
            whiteLinePaint.strokeWidth = 2f
            textPaint = Paint()
            textPaint.isAntiAlias = true
            textPaint.color = a.getColor(R.styleable.MyPieView_chartTextColor, Color.BLACK)
            textPaint.textSize =
                a.getDimensionPixelSize(R.styleable.MyPieView_chartTextSize, 20).toFloat()
            textPaint.strokeWidth = 5f
            textPaint.textAlign = Paint.Align.CENTER
            pieCenterPoint = Point()
            cirRect = RectF()
            cirSelectedRect = RectF()
        } finally {
            a.recycle()
        }
    }

    fun showPercentLabel(show: Boolean) {
        showPercentLabel = show
        postInvalidate()
    }

    fun setOnPieClickListener(listener: OnPieClickListener) {
        onPieClickListener = listener
    }

    fun setData(helperList: List<MyPieHelper>?) {
        initPies(helperList!!)
        pieHelperList.clear()
        removeSelectedPie()

        if (helperList.isNotEmpty()) {
            for (pieHelper in helperList) {
                pieHelperList.add(
                    MyPieHelper(
                        pieHelper.getStartDegree(),
                        pieHelper.getStartDegree(),
                        pieHelper
                    )
                )
            }
        } else {
            pieHelperList.clear()
        }

        removeCallbacks(animator)
        post(animator)

        // pieHelperList = helperList;
        // postInvalidate();
    }

    /**
     * Set startDegree and endDegree for each MyPieHelper
     *
     * @param helperList
     */
    private fun initPies(helperList: List<MyPieHelper>) {
        var totalAngel = 270f
        for (pie in helperList) {
            pie.setDegree(totalAngel, totalAngel + pie.getSweep())
            totalAngel += pie.getSweep()
        }
    }

    fun selectedPie(index: Int) {
        selectedIndex = index
        if (onPieClickListener != null) onPieClickListener!!.onPieClick(index)
        postInvalidate()
    }

    fun removeSelectedPie() {
        selectedIndex = noSelectedIndex
        if (onPieClickListener != null) onPieClickListener!!.onPieClick(noSelectedIndex)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (pieHelperList.isEmpty()) {
            return
        }
        for ((index, pieHelper) in pieHelperList.withIndex()) {
            val selected = selectedIndex == index
            val rect = if (selected) cirSelectedRect else cirRect
            if (pieHelper.isColorSetted()) {
                cirPaint.color = pieHelper.getColor()
            } else {
                cirPaint.color = defaultColorList[index % 5]
            }
            canvas.drawArc(rect, pieHelper.getStartDegree(), pieHelper.getSweep(), true, cirPaint)
            if (pieHelper.getPercentage() > 1)
                drawPercentText(canvas, pieHelper)

            //drawLineBesideCir(canvas, pieHelper.getStartDegree(), selected);
            //drawLineBesideCir(canvas, pieHelper.getEndDegree(), selected);
        }

    }

    private fun drawLineBesideCir(canvas: Canvas, angel: Float, selectedCir: Boolean) {
        val sth2 =
            if (selectedCir) mViewHeight / 2 else pieRadius // Sorry I'm really don't know how to name the variable..
        var sth = 1 // And it's
        if (angel % 360 > 180 && angel % 360 < 360) {
            sth = -1
        }
        val lineToX =
            (mViewHeight / 2 + cos(Math.toRadians((-angel).toDouble())) * sth2).toFloat()
        val lineToY =
            (mViewHeight / 2 + sth.toDouble() * abs(sin(Math.toRadians((-angel).toDouble()))) * sth2.toDouble()).toFloat()
        canvas.drawLine(
            pieCenterPoint.x.toFloat(),
            pieCenterPoint.y.toFloat(),
            lineToX,
            lineToY,
            whiteLinePaint
        )
    }

    private fun drawPercentText(canvas: Canvas, pieHelper: MyPieHelper) {
        if (!showPercentLabel) return
        val angel = (pieHelper.getStartDegree() + pieHelper.getEndDegree()) / 2
        var sth = 1
        if (angel % 360 > 180 && angel % 360 < 360) {
            sth = -1
        }
        val x =
            (mViewHeight / 2 + cos(Math.toRadians((-angel).toDouble())) * pieRadius / 1.2).toFloat()
        val y =
            ((mViewHeight / 2).toDouble() + sth.toDouble() * abs(sin(Math.toRadians((-angel).toDouble()))) * pieRadius.toDouble() / 1.2 + 8.0).toFloat()
        canvas.drawText(pieHelper.getPercentStr(), x, y, textPaint)
    }

    private fun drawText(canvas: Canvas, pieHelper: MyPieHelper) {
        if (pieHelper.getTitle() == null) return
        val angel = (pieHelper.getStartDegree() + pieHelper.getEndDegree()) / 2
        var sth = 1
        if (angel % 360 > 180 && angel % 360 < 360) {
            sth = -1
        }
        val x =
            (mViewHeight / 2 + cos(Math.toRadians((-angel).toDouble())) * pieRadius / 2).toFloat()
        val y =
            (mViewHeight / 2 + sth.toDouble() * abs(sin(Math.toRadians((-angel).toDouble()))) * pieRadius.toDouble() / 2).toFloat()
        canvas.drawText(pieHelper.getTitle()!!, x, y, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        /*if (event.getAction() == MotionEvent.ACTION_DOWN ||event.getAction() == MotionEvent.ACTION_MOVE){
            selectedIndex = findPointAt((int) event.getX(), (int) event.getY());
            if(onPieClickListener != null){
                onPieClickListener.onPieClick(selectedIndex);
            }
            postInvalidate();
        }*/

        return true
    }

    /**
     * find pie index where point is
     *
     * @param x
     * @param y
     * @return
     */
    private fun findPointAt(x: Int, y: Int): Int {
        var degree = atan2(
            (x - pieCenterPoint.x).toDouble(),
            (y - pieCenterPoint.y).toDouble()
        ) * 180 / Math.PI
        degree = -(degree - 180) + 270
        for ((index, pieHelper) in pieHelperList.withIndex()) {
            if (degree >= pieHelper.getStartDegree() && degree <= pieHelper.getEndDegree()) {
                return index
            }
        }
        return noSelectedIndex
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mViewWidth = measureWidth(widthMeasureSpec)
        mViewHeight = measureHeight(heightMeasureSpec)
        margin = mViewWidth / 16
        pieRadius = mViewWidth / 2 - margin
        pieCenterPoint.set(pieRadius + margin, pieRadius + margin)
        cirRect.set(
            (pieCenterPoint.x - pieRadius).toFloat(),
            (pieCenterPoint.y - pieRadius).toFloat(),
            (pieCenterPoint.x + pieRadius).toFloat(),
            (pieCenterPoint.y + pieRadius).toFloat()
        )
        cirSelectedRect.set(
            2f, //minor margin for bigger circle
            2f,
            (mViewWidth - 2).toFloat(),
            (mViewHeight - 2).toFloat()
        )
        setMeasuredDimension(mViewWidth, mViewHeight)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val preferred = 3
        return getMeasurement(measureSpec, preferred)
    }

    private fun measureHeight(measureSpec: Int): Int {
        val preferred = mViewWidth
        return getMeasurement(measureSpec, preferred)
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> preferred.coerceAtMost(specSize)
            else -> preferred
        }
    }
}