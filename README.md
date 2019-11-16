MyUtils-Lib
=====
[![](https://jitpack.io/v/DawinderGill/DawinderUtils-Lib.svg)](https://jitpack.io/#DawinderGill/DawinderUtils-Lib)

--------------------

Library Includes
--------------

**1. Range seek bar**

    Custom range seek bar in which you can customize seek bar thumbs, bar color, text color and many more without using any third party library.

**2. Pie graph**

    Create circular pie chat without any third party library in which you can divide circular chart with different percentage and colors.

**3. Common RecyclerView adapter**

    Forgot about creating different classes for different recyclerview adapters. Thanks to **Data Binding**.

**4. Add map in scroll view**

    You can add google map view in scrollview without any zooming issue.

**5. Custom page indicator**

    Add page indicator to the view pager without using any library. You can add your custom drawable in selected and unselected dots of page indicator.

**6. Some basic methods**

    This is one of the most useful class. In which added some basic methods which we need in most of our android projects. Like check internet connection, get current date, load image etc.

**7. Zoom image**

    A simple pinch-to-zoom ImageView for Android with an emphasis on a smooth and natural feel.

**8. Get any permission**

    You can get multile permissions at one time with the help of this class.

**9. Location picker**

    Easy way to get device location. This class will handle all things needed to get location of device like permission, check GPS etc.

**10. Image picker**

    Easy to way get image from device storage or capture image from camera. There is ability to disable gallery or camera option.

**Gif Demo**
-----------
![](https://media.giphy.com/media/iiQEEAQITxifM0XV5S/giphy.gif)

--------------------------------------------------

**Video Demo**
-----------

[![Watch the video](https://img.youtube.com/vi/tcQz_XEYbo0/0.jpg)](https://youtu.be/tcQz_XEYbo0)

--------------------
Download
--------

By using Gradle:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
    implementation 'com.github.DawinderGill:DawinderUtils-Lib:1.5'
}
```

Or Maven:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
<dependency>
    <groupId>com.github.DawinderGill</groupId>
    <artifactId>DawinderUtils-Lib</artifactId>
    <version>1.5</version>
</dependency>
```

--------------------

How do I use this library?
-----------------------
**1. Range seek bar**

    Custom range seek bar in which you can customize seek bar thumbs, bar color, text color and many more without using any third party library.

```java
<com.dawinderutilslib.rangeseekbar.MyRangeSeekBar
    android:id="@+id/rangeSeekBar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:absoluteMaxValue="100"
    app:absoluteMinValue="1"
    app:activateOnDefaultValues="true"
    app:activeColor="@color/colorPrimary"
    app:barHeight="5dp"
    app:defaultColor="@color/colorAccent"
    app:showLabels="true"
    app:singleThumb="false"
    app:step="1"
    app:textAboveThumbsColor="@color/colorAccent"
    app:thumbNormal="@drawable/custom_seek_thumb"
    app:thumbPressed="@drawable/custom_seek_thumb"
    app:thumbShadow="true"
    app:valuesAboveThumbs="true" />
```

**2. Pie graph**

    Create circular pie chat without any third party library in which you can divide circular chart with different percentage and colors.

```java
1. Add 'MyPieView' in xml

<com.dawinderutilslib.piechart.MyPieView
    android:id="@+id/pieView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="10sp"
    app:chartTextColor="@color/colorBlack"
    app:chartTextSize="10sp" />

2. Set data in 'MyPieView'

val pieList = ArrayList<MyPieHelper>()
pieList.add(MyPieHelper(10F, MyUtils.getRandomColor()))
pieList.add(MyPieHelper(25F, MyUtils.getRandomColor()))
pieList.add(MyPieHelper(10F, MyUtils.getRandomColor()))
pieList.add(MyPieHelper(50F, MyUtils.getRandomColor()))
pieList.add(MyPieHelper(5F, MyUtils.getRandomColor()))
binding.pieView.setData(pieList)
```

**3. Common RecyclerView adapter**

    Forgot about creating different classes for different recyclerview adapters.
    Use data binding and recyclerview. Create string type list and set adapter to recyclerview in below code.

```java
val list = arrayListOf<String>()
list.add("Item One")
list.add("Item Two")
list.add("Item Three")
list.add("Item Four")
list.add("Item Five")
val adapter =
MyBaseListAdapter<String>(R.layout.item_recyclerview,
    object : OnAdapterItemClick {
        override fun onItemClick(position: Int) {
            MyUtils.showToast(mContext, list[position])
        }
    })
binding.recyclerView.adapter = adapter
binding.recyclerView.addItemDecoration(
    DividerItemDecoration(
            mContext,
            LinearLayoutManager.VERTICAL
        )
    )
adapter.submitList(list)

\*'item_recyclerview' is a recyclerview layout. Make sure that you named databinding variable 'data' in each recyclerview layout like below :-*\

<data>

    <variable
        name="data"
        type="String" />
</data>
```

**4. Add map in scroll view**

    You can add google map view in scrollview without any zooming issue.

```java
1. Add 'fragment' class name 'com.dawinderutilslib.mapview.MyMapView'

<fragment
    android:id="@+id/map"
    android:name="com.dawinderutilslib.mapview.MyMapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

2. Here you need to find view and add listener, in which set 'requestDisallowInterceptTouchEvent' true of your outer 'scrollview'

private fun setUpMap() {
    val mSupportMapFragment =
        supportFragmentManager.findFragmentById(R.id.map) as MyMapView
    mSupportMapFragment.setListener(object : MyMapView.OnTouchListener {
        override fun onTouch() {
            binding.scrollView.requestDisallowInterceptTouchEvent(true)
        }
    })
    mSupportMapFragment.getMapAsync(this)
}
```

**5. Custom page indicator**

    Add page indicator to the view pager without using any library. You can add your custom drawable in selected and unselected dots of page indicator.

```java
1. Add 'LinearLayout' where you want to show page indicator

<LinearLayout
    android:id="@+id/llIndicator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="10sp"
    android:gravity="center"
    android:orientation="horizontal" />

2. Initiate page indicator

MyPageIndicator(mContext)
    .ini(binding.viewPager, binding.llIndicator)
    .setDrawable(R.drawable.custom_dot_selected,R.drawable.custom_dot_unselecte)
    .build()

\*Note : 'ini' method need 'viewpager' and 'linear layout'. 'setDrawable' method is to add custom drawable to the indicator dots. Do not call this method if you want default dots. If you want to change only one then another parameter set '0'*\
```

**6. Some basic methods**

    This is one of the most useful class. In which added some basic methods which we need in most of our android projects. Like check internet connection, get current date, load image etc.

```java
MyUtils.showToast(mContext, "Like this you can use MyUtils class methods.")
```

**7. Zoom image**

    A simple pinch-to-zoom ImageView for Android with an emphasis on a smooth and natural feel.

```java
<com.dawinderutilslib.zoomimageview.MyZoomImageView
    android:id="@+id/ivZoom"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

**8. Get any permission**

    You can get multile permissions at one time with the help of this class.

```java
1. Add this in 'onRequestPermissionsResult' method of activity/fragment

MyPermissionChecker.onRequestPermissionsResult(
    mContext,
    requestCode,
    permissions,
    grantResults
)

2. Mention permissions while making call and you will get result in listener if all permissions granted. MAke sure you add these permissions in 'AndroidManifest.xml' file too.

MyPermissionChecker.getPermission(
    mContext,
    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA),
    object : OnPermissionGranted {
                override fun onPermissionGranted() {
                    MyUtils.showToast(mContext, "All permissions granted.")
                }
            })
```

**9. Location picker**

    Easy way to get device location. This class will handle all things needed to get location of device like permission, check GPS etc.

```java
1. Add this in 'onRequestPermissionsResult' method of activity/fragment

MyLocationPicker.onRequestPermissionsResult(
    mContext,
    requestCode,
    permissions,
    grantResults
)

2. Add this in 'onActivityResult' method of activity/fragment

MyLocationPicker.onActivityResult(mContext, requestCode, resultCode, data)

3. Now its time to get location

MyLocationPicker.getCurrentLocation(mContext,
    object : OnLocationPick {
        override fun onLocationPick(latitude: Double, longitude: Double) {
            MyUtils.showToast(
                mContext,
                "Latitude : $latitude, Longitude : $longitude"
            )
        }
    })
```

**10. Image picker**

    Easy to way get image from device storage or capture image from camera. There is ability to disable gallery or camera option.

```java
1. Add this in 'onRequestPermissionsResult' method of activity/fragment

MyImagePicker.onRequestPermissionsResult(
    mContext,
    requestCode,
    permissions,
    grantResults
)

2. Add this in 'onActivityResult' method of activity/fragment

MyImagePicker.onActivityResult(mContext, requestCode, resultCode, data)

3. Now its time to get image. You can disable gallery or camera option by calling 'disableGallery()' and 'disableCamera()' methods.

MyImagePicker.selectImage(mContext,
    object : OnImagePick {
        override fun onImagePick(path: String) {
            MyUtils.showToast(mContext, "Image path : $path")
        }
    })
```

--------------------

Developer
---------
**Dawinder Singh**
--------------
