package com.myutilslibtest.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawinderutilslib.MyPermissionChecker
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.adapter.MyBaseListAdapter
import com.dawinderutilslib.listeners.OnAdapterItemClick
import com.dawinderutilslib.listeners.OnPermissionGranted
import com.dawinderutilslib.mapview.MyMapView
import com.dawinderutilslib.pageindicator.MyPageIndicator
import com.dawinderutilslib.piechart.MyPieHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.myutilslibtest.R
import com.myutilslibtest.adapters.ViewPagerAdapter
import com.myutilslibtest.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setPieChart()
        setCommonAdapter()
        setMap()
        setViewPager()

        binding.btShowToast.setOnClickListener {
            MyUtils.showToast(this, "Like this you can use MyUtils class methods.")
        }
        binding.btZoomImage.setOnClickListener {
            startActivity(Intent(this, ZoomImageActivity::class.java))
        }
        binding.btPermission.setOnClickListener {
            MyPermissionChecker.getPermission(this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), object : OnPermissionGranted {
                    override fun onPermissionGranted() {
                        MyUtils.showToast(this@MainActivity, "All permissions granted.")
                    }
                })
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

    fun setCommonAdapter() {
        val list = arrayListOf<String>()
        list.add("Item One")
        list.add("Item Two")
        list.add("Item Three")
        list.add("Item Four")
        list.add("Item Five")
        val adapter =
            MyBaseListAdapter<String>(R.layout.item_recyclerview, object : OnAdapterItemClick {
                override fun onItemClick(position: Int) {
                    MyUtils.showToast(this@MainActivity, list[position])
                }
            })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter.submitList(list)
    }

    fun setMap() {
        val mSupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as MyMapView
        mSupportMapFragment.setListener(object : MyMapView.OnTouchListener {
            override fun onTouch() {
                binding.scrollView.requestDisallowInterceptTouchEvent(true)
            }
        })
        mSupportMapFragment.getMapAsync(this)
    }

    fun setViewPager() {
        val list = arrayListOf<String>()
        list.add("https://cdn.shopify.com/s/files/1/1381/1501/products/9334059_rfr_summer_banners_ci_1024x1024.jpg?v=1468450863")
        list.add("https://i.pinimg.com/originals/c6/da/03/c6da0308991deed3af22d92065242a08.jpg")
        list.add("https://image.freepik.com/free-photo/two-umbrella-tropical-beach-summer-holiday-banner_34755-248.jpg")
        list.add("https://previews.123rf.com/images/maridav/maridav1604/maridav160400665/55657495-panorama-summer-vacation-couple-walking-on-beach-young-adults-having-fun-together-enjoying-their-hol.jpg")
        list.add("https://keylimeexcursions.com/wp-content/uploads/2019/07/best-beach-umbrellas.jpg")
        list.add("https://previews.123rf.com/images/mrcmos/mrcmos1409/mrcmos140900010/31616208-pair-of-colorful-beach-chair-with-sun-umbrella-on-beautiful-beach-concept-for-rest-relaxation-and-ho.jpg")
        binding.viewPager.adapter = ViewPagerAdapter(this, list)
        MyPageIndicator(this)
            .ini(binding.viewPager, binding.llIndicator, list.size)
            .setDrawable(R.drawable.custom_dot_selected, R.drawable.custom_dot_unselected)
            .build()
    }

    override fun onMapReady(mMap: GoogleMap?) {
        val loc = LatLng(30.946549, 74.843051)
        val position = CameraPosition.Builder().target(loc).zoom(12f).bearing(18f).tilt(30f).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        mMap?.addMarker(MarkerOptions().position(loc).title("Home"))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MyPermissionChecker.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}
