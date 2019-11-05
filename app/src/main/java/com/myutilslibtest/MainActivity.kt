package com.myutilslibtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.piechart.MyPieHelper
import com.myutilslibtest.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setPieChart()

        binding.btShowToast.setOnClickListener {
            MyUtils.showToast(this, "Like this you can use MyUtils class methods.")
        }
    }

    fun setPieChart() {
        val pieList = ArrayList<MyPieHelper>()
        pieList.add(MyPieHelper(10F, MyUtils.getRandomColor()))
        pieList.add(MyPieHelper(25F, MyUtils.getRandomColor()))
        pieList.add(MyPieHelper(10F, MyUtils.getRandomColor()))
        pieList.add(MyPieHelper(50F, MyUtils.getRandomColor()))
        pieList.add(MyPieHelper(5F, MyUtils.getRandomColor()))
        binding.pieView.setData(pieList)
    }
}
