@file:Suppress(
    "UNUSED_ANONYMOUS_PARAMETER", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "CAST_NEVER_SUCCEEDS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "MemberVisibilityCanBePrivate", "FunctionName"
)

package com.dawinderutilslib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.*
import android.text.format.DateUtils
import android.text.style.UnderlineSpan
import android.transition.TransitionManager
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs

@Suppress("unused")
object MyUtils {

    /**
     * Check internet availability
     *
     * @param mContext Context of activity or fragment
     * @return Returns true is internet connected and false if no internet connected
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun isInternetAvailable(mContext: Context): Boolean {
        var result = false
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    /**
     * Show toast message
     *
     * @param mContext Context of activity or fragment
     * @param message  Message that show into the Toast
     */
    fun showToast(mContext: Context, message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Show snackbar
     *
     * @param view in which you want to show it
     * @param message  Message that show into the snackbar
     * @param backgroundColor  background color of snackbar (Default : Black)
     * @param textColor  text color of snackbar (Default : White)
     * @param textSize  text size of snackbar (Default : 10)
     */
    fun showSnackBar(
        view: View,
        message: String,
        backgroundColor: Int = android.R.color.black,
        textColor: Int = android.R.color.white,
        textSize: Float = 10F
    ) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColor))
        val textView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(view.context, textColor))
        textView.textSize = textSize
        snackBar.show()
    }

    /**
     * Show alert dialog
     *
     * @param mContext         Context of activity o fragment
     * @param message          Message that shows on Dialog
     * @param positiveText     Set positive text
     * @param positiveListener Set functionality on positive button click
     * @param negativeListener Set functionality on negative button click
     * @param negativeText     Negative button text
     * @param neutralText      Neturat button text
     * @param neutralListener  Set Netural button listener
     * @param isCancelable     true -> Cancelable True ,false -> Cancelable False
     * @return dialog
     */
    fun showDialog(
        mContext: Context, title: String,
        message: String, positiveText: String,
        negativeText: String, neutralText: String,
        positiveListener: DialogInterface.OnClickListener?,
        negativeListener: DialogInterface.OnClickListener?,
        neutralListener: DialogInterface.OnClickListener?,
        isCancelable: Boolean
    ): AlertDialog.Builder {
        val alert = AlertDialog.Builder(mContext)
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setNegativeButton(negativeText, negativeListener)
        alert.setPositiveButton(positiveText, positiveListener)
        alert.setNeutralButton(neutralText, neutralListener)
        alert.setCancelable(isCancelable)
        alert.show()
        return alert
    }

    /**
     * Show message dialog
     *
     * @param mContext Context of activity o fragment
     * @param message  Message that shows on Dialog
     * @param listener Set action that you want to perform OK click
     */
    fun showMessageDialog(
        mContext: Context,
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener?
    ) {
        val dialog = AlertDialog.Builder(mContext)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setNegativeButton(mContext.getString(R.string.ok), listener)
        try {
            dialog.show()
        } catch (ignored: Exception) {

        }
    }

    /**
     * Check weather device is GPS is Enabled or not.
     *
     * @param mContext Context of the Activity or fragment.
     * @return Returns true if GPS Enabled and false when its not.
     */
    @SuppressLint("NewApi")
    fun isGpsEnabled(mContext: Context): Boolean {
        return (Objects.requireNonNull(mContext.getSystemService(Context.LOCATION_SERVICE)) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    }

    /**
     * Simple Share Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param text     Text that you want to share with intent
     */
    fun shareContent(mContext: Context, text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        mContext.startActivity(Intent.createChooser(sendIntent, "Go To : "))
    }

    /**
     * Simple Browser Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param url      Url that you want to open in Browser
     */
    fun intentToBrowser(mContext: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            mContext.startActivity(Intent.createChooser(intent, "Go To : "))
        } catch (e: Exception) {
            showLog("Exp : " + e.message)
        }

    }

    /**
     * Intent to Mail App
     *
     * @param mContext Context of the Activity or Fragment.
     * @param mail     Email Id that you want to intent into Mail App
     * @param subject  Subject from email
     * @param message  Message for email
     */
    fun intentToMail(mContext: Context, mail: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
        intent.putExtra(Intent.EXTRA_SUBJECT, "" + subject)
        intent.putExtra(Intent.EXTRA_TEXT, "" + message)
        mContext.startActivity(Intent.createChooser(intent, "Go To : "))
    }

    /**
     * Intent to Phone
     *
     * @param mContext Context of the Activity or Fragment.
     * @param number   Number on which want to make a call
     */
    fun intentToPhone(mContext: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        mContext.startActivity(Intent.createChooser(intent, "Go To : "))
    }

    /**
     * Intent to Sms
     *
     * @param mContext Context of the Activity or Fragment.
     * @param number   Number on which want to make a sms
     */
    fun intentToSms(mContext: Context, number: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
        mContext.startActivity(Intent.createChooser(intent, "Go To : "))
    }

    /**
     * Intent to Google Map
     *
     * @param mContext Context of the Activity or Fragment.
     * @param mLat     Latitude
     * @param mLong    Longitude
     */
    fun intentToMap(mContext: Context, title: String, mLat: String, mLong: String) {
        val label = title.ifEmpty { mContext.getString(R.string.app_name) }
        val uriBegin = "geo:$mLat,$mLong"
        val query = "$mLat,$mLong($label)"
        val encodedQuery = Uri.encode(query)
        val uriString = "$uriBegin?q=$encodedQuery&z=10"
        showLog(uriString)
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        mContext.startActivity(Intent.createChooser(intent, "Go To : "))
    }

    /**
     * Get playstore link of app
     *
     * @param mContext Context of the Activity or Fragment.
     */
    fun getPlayStoreLink(mContext: Context): String {
        return "https://play.google.com/store/apps/details?id=" + mContext.packageName
    }

    /**
     * Check weather device is Tablet or not.
     *
     * @param mContext Context of the Activity.
     * @return Returns true if device is Tablet and false when its not.
     */
    fun isTablet(mContext: Context): Boolean {
        return mContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * Show Log
     *
     * @param message Message that want to show into Log
     */
    fun showLog(message: String) {
        Log.e("Log Message", "" + message)
    }

    /**
     * Goto any Fragment
     *
     * @param mContext    Context of the Activity of Fragment.
     * @param newFragment Fragment that want to open
     * @param container   Container in which want to inflate Fragment
     */
    fun goToFragment(
        mContext: Context,
        newFragment: Fragment,
        container: Int,
        addtoBackstack: Boolean
    ) {
        hideKeyboard(mContext)
        val transaction =
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addtoBackstack) {
            val oldFragment = mContext.supportFragmentManager.findFragmentById(container)
            transaction.add(container, newFragment)
            transaction.addToBackStack(null)
            if (oldFragment != null) {
                transaction.hide(oldFragment)
            }
        } else {
            transaction.replace(container, newFragment)
        }
        transaction.commit()
    }

    /**
     * Change status Bar color
     *
     * @param mContext Context of the Activity.
     * @param act      Activity,in which want to change status bar color
     * @param color    Color that you want
     */
    @SuppressLint("ObsoleteSdkInt")
    fun changeStatusBarColor(mContext: Context, act: AppCompatActivity, color: Int) {
        if (Build.VERSION.SDK_INT > 20) {
            val window = act.window
            /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(mContext, color)*/

            window.statusBarColor = ContextCompat.getColor(mContext, color)
        }
    }

    /**
     * Hide Soft Keyboard
     *
     * @param mContext Context of the Activity or Fragment.
     */
    fun hideKeyboard(mContext: Context) {
        val view = (mContext as AppCompatActivity).currentFocus
        if (view != null) {
            val imm =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Get current date
     *
     * @return Returns current date
     */
    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        return formatter.format(c.time)
    }

    /**
     * Get current date
     *@param pattern Data format in which you want date
     * @return Returns current date
     */
    fun getCurrentDate(pattern: String): String {
        val c = Calendar.getInstance()
        val formatter = SimpleDateFormat(pattern, Locale.US)
        return formatter.format(c.time)
    }

    /**
     * Get current time
     *
     * @return Returns current Time
     */
    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val formatter = SimpleDateFormat("hh:mm a", Locale.US)
        return formatter.format(c.time)
    }

    /**
     * Get current time
     * @param pattern time format in which you want time
     *
     * @return Returns current Time
     */
    fun getCurrentTime(pattern: String): String {
        val c = Calendar.getInstance()
        val formatter = SimpleDateFormat(pattern, Locale.US)
        return formatter.format(c.time)
    }

    /**
     * Get month from date
     *
     * @return Returns month of date
     */
    fun getMonthFromDate(date: Date): String {
        return android.text.format.DateFormat.format("MMM", date) as String
    }

    /**
     * Get day from date
     *
     * @return Returns day of date
     */
    fun getDayFromDate(date: Date): String {
        return android.text.format.DateFormat.format("dd", date) as String
    }

    /**
     * Get week day from date
     *
     * @return Returns week day of date
     */
    fun getWeekDayFromDate(date: Date): String {
        return android.text.format.DateFormat.format("EE", date) as String
    }

    /**
     * Get Date from string
     *
     * @return Returns date
     */
    fun convertDateStringIntoDate(stringDate: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            format.parse(stringDate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get Date from string
     * @param pattern date format in which you are sending date
     *
     * @return Returns date
     */
    fun convertDateStringIntoDate(stringDate: String, pattern: String): Date? {
        val format = SimpleDateFormat(pattern, Locale.US)
        return try {
            format.parse(stringDate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get String Date from Date object
     *
     * @param date Timestamp
     * @return Returns Date according to give Timestamp
     */
    fun convertDateIntoStringDate(date: Date): String {
        val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return dayFormat.format(calendar.time)
    }

    /**
     * Get String Date from Date object
     *
     * @param date Timestamp
     * @param pattern date format in which you want date
     * @return Returns Date according to give Timestamp
     */
    fun convertDateIntoStringDate(date: Date, pattern: String): String {
        val dayFormat = SimpleDateFormat(pattern, Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return dayFormat.format(calendar.time)
    }

    /**
     * Get day from Timestamp
     *
     * @param TimeInMillis TimeStamp
     * @return Returns day according to give Timestamp
     */
    fun getDayFromTimeStamp(TimeInMillis: String): String {
        val weekDay: String
        val dayFormat = SimpleDateFormat("EEEE", Locale.US)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = java.lang.Long.parseLong(TimeInMillis) * 1000
        weekDay = dayFormat.format(calendar.time)
        return weekDay
    }

    /**
     * Get day from Date [Date Format : 2017-02-24]
     *
     * @param date TimeStamp
     * @return Returns day according to given date
     */
    fun getDayFromDate(date: String): String {
        return try {
            val format1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dt1 = format1.parse(date)
            val format2 = SimpleDateFormat("EEE", Locale.US)
            format2.format(dt1)
        } catch (e: Exception) {
            showLog("Exp : " + e.message)
            ""
        }
    }

    /**
     * Get Date from Timestamp
     *
     * @param TimeInMillis Timestamp
     * @return Returns Date according to give Timestamp
     */
    fun getDateFromTimeStamp(TimeInMillis: String): String {
        val weekDay: String
        val dayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = java.lang.Long.parseLong(TimeInMillis)
        weekDay = dayFormat.format(calendar.time)
        return weekDay
    }

    /**
     * Get Date from Timestamp give pattern and get outout accordingly
     *
     * @param TimeInMillis Timestamp
     * @return Returns Date according to give Timestamp
     */
    fun getDateFromTimeStamp(TimeInMillis: String, pattern: String): String {
        val weekDay: String
        val dayFormat = SimpleDateFormat(pattern, Locale.US)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = java.lang.Long.parseLong(TimeInMillis)
        weekDay = dayFormat.format(calendar.time)
        return weekDay
    }

    /**
     * Get Date and Time from Timestamp
     *
     * @param TimeInMillis Timestamp
     * @return Returns Date and Time according to give Timestamp
     */
    fun getDateTimeFromTimeStamp(TimeInMillis: String): String {
        val weekDay: String
        val dayFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a", Locale.US)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = java.lang.Long.parseLong(TimeInMillis)
        weekDay = dayFormat.format(calendar.time)
        return weekDay
    }

    /**
     * Get Time from Timestamp
     *
     * @param TimeInMillis Timestamp
     * @return Returns Time according to given Timestamp
     */
    fun getTimeFromTimeStamp(mContext: Context, TimeInMillis: String): String {
        return DateUtils.formatDateTime(
            mContext,
            java.lang.Long.parseLong(TimeInMillis),
            DateUtils.FORMAT_SHOW_TIME
        )
    }

    /**
     * Compare Time with Current Time
     *
     * @param datetime Date-Time that you want to compare with current datetime
     * (Format : yyyy-mm-dd hh:mm:ss, Example : 2016-06-07 21:00:00 Time will be in 24 Hour Format)
     * @return True if Time is smaller than current time otherwise return false
     */
    fun compareTimewithCurrentTime(datetime: String): Boolean {
        val c = Calendar.getInstance()
        val currentTime: Date
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return try {
            val timeCompare = sdf.parse(datetime)
            currentTime = c.time
            timeCompare >= currentTime
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            false
        }

    }

    fun compareTwoDates(startDate: String, endDate: String): Boolean {
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val date1 = sdf.parse(startDate)
            val date2 = sdf.parse(endDate)
            val cal = Calendar.getInstance()
            cal.time = date2
            cal.add(Calendar.DATE, +1)
            val date2Final = cal.time
            date2Final > date1
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            false
        }

    }

    /**
     * Compare Two times
     *
     * @param startTime Time that you want to compare with another time
     * @param endTime   Time that you want to compare with first time
     * (Format : hh:mm a, Example : 02:12 PM Time will be in 12 Hour Format)
     * @return True if StartTime is greater than EndTime return true otherwise false
     */
    fun compareTwoTime(startTime: String, endTime: String): Boolean {
        @SuppressLint("SimpleDateFormat")
        val sdfStart = SimpleDateFormat("hh:mm:ss a", Locale.US)
        val sdfEnd = SimpleDateFormat("hh:mm a", Locale.US)
        return try {
            val time1 = sdfStart.parse(startTime)
            val calendar = Calendar.getInstance()
            calendar.time = time1
            calendar.add(Calendar.HOUR, 2)
            val newTime = calendar.time
            val time2 = sdfEnd.parse(endTime)
            time2 > newTime
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            false
        }
    }

    /**
     * Check is Current time is in between Start and End time or not
     *
     * @param starttime Start Date-Time
     * @param endtime   End Date-Time
     * (Format : yyyy-mm-dd hh:mm:ss, Example : 2016-06-07 21:00:00 Time will be in 24 Hour Format)
     * @return True if Current Time is in between start and end time than return true otherwise return false
     */
    fun compareTimeBetweenTwoTimes(starttime: String, endtime: String): Boolean {
        val c = Calendar.getInstance()
        val currentTime: Date
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return try {
            currentTime = c.time
            val startCompare = sdf.parse(starttime)
            val endCompare = sdf.parse(endtime)
            startCompare <= currentTime && endCompare >= currentTime
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            false
        }

    }

    /**
     * Check is Date is in between Start and End date or not
     *
     * @param startDate    Start Date-Time
     * @param endDate      End Date-Time
     * @param selectedDate Selected Date-Time
     * (Format : yyyy-mm-dd hh:mm:ss, Example : 2016-06-07 21:00:00 Time will be in 24 Hour Format)
     * @return True if Selected Date is in between start and end date than return true otherwise return false
     */
    fun compareDateBetweenTwoDates(
        startDate: String,
        endDate: String,
        selectedDate: String
    ): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return try {
            val startCompare = sdf.parse(startDate)
            val endCompare = sdf.parse(endDate)
            val selectedCompare = sdf.parse(selectedDate)
            selectedCompare in startCompare..endCompare
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            false
        }
    }

    /**
     * Get days count between two dates
     *
     * @param startDate Start Date
     * @param endDate   End Date
     * (Format : yyyy-mm-dd, Example : 2016-06-07)
     * @return count between two dates
     */
    fun getDaysCountBetweenTwoDates(startDate: String, endDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val startCompare = sdf.parse(startDate)
            val endCompare = sdf.parse(endDate)
            val diff = endCompare.time - startCompare.time
            var days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
            days += 1
            days
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            1
        }
    }

    /**
     * Change date format
     *
     * @param dateTime String date time
     * @param inputFormat Sample input date time format
     * @param outputFormat Sample output date time format
     * @return date in give output date format if format it correct
     */
    fun changeDateFormat(dateTime: String, inputFormat: String, outputFormat: String): String {
        val inputPattern = SimpleDateFormat(inputFormat, Locale.US)
        val outputPattern = SimpleDateFormat(outputFormat, Locale.US)
        return try {
            val prevDate = inputPattern.parse(dateTime)!!
            outputPattern.format(prevDate)
        } catch (e: Exception) {
            Log.e("exp", "in catch")
            e.printStackTrace()
            dateTime
        }
    }

    /**
     * To get time from long time duration
     *
     * @param duration int duration
     * @return return time in hh:mm:ss
     */
    fun getTimeInMinutes(duration: Int): String {
        val sec: String
        if (TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    duration.toLong()
                )
            ) < 10
        ) {
            sec =
                "0" + (TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
                ))
        } else {
            sec =
                "" + (TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
                ))
        }
        val min: String = if (TimeUnit.MILLISECONDS.toMinutes(duration.toLong()) < 10) {
            "0" + TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        } else {
            "" + TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        }
        val hour: String = if (TimeUnit.MILLISECONDS.toHours(duration.toLong()) < 10) {
            "0" + TimeUnit.MILLISECONDS.toHours(duration.toLong())
        } else {
            "" + TimeUnit.MILLISECONDS.toHours(duration.toLong())
        }
        return if (hour.equals("00", ignoreCase = true)) {
            "$min:$sec"
        } else {
            "$hour:$min:$sec"
        }
    }

    /**
     * Get passed time from time stamp
     *
     * @param timeStamp Timestamp
     * @return Returns passed time from current time
     */
    fun getPassedTimeString(mContext: Context, timeStamp: String): String {
        val epochInMillis = java.lang.Long.parseLong(timeStamp)
        val now = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        val timeToCheck = Calendar.getInstance()
        timeToCheck.timeInMillis = epochInMillis

        return if (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(
                Calendar.DAY_OF_YEAR
            )
        ) {
            "Today, " + getTimeFromTimeStamp(mContext, timeStamp)
        } else if (yesterday.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR) && now.get(
                Calendar.YEAR
            ) == timeToCheck.get(
                Calendar.YEAR
            )
        ) {
            "Yesterday, " + getTimeFromTimeStamp(mContext, timeStamp)
        } else {
            if (daysBetween(timeToCheck, now) == 1)
                daysBetween(timeToCheck, now).toString() + " day ago"
            else
                daysBetween(timeToCheck, now).toString() + " days ago"
        }
    }

    /**
     * Get Days between two calendar dates
     *
     * @param day1 Calendar One
     * @param day2 Calendar Two
     * @return Returns days count
     */
    private fun daysBetween(day1: Calendar, day2: Calendar): Int {
        var dayOne = day1.clone() as Calendar
        var dayTwo = day2.clone() as Calendar
        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR))
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                val temp = dayOne
                dayOne = dayTwo
                dayTwo = temp
            }
            var extraDays = 0
            val dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR)
            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1)
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR)
            }
            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays
        }
    }

    /**
     * To show Date Picker and set selected Date in TextView
     *
     * @param mContext context of activity or fragment
     * @param textView TextView in which you want to selected date
     */
    fun showDatePickerDialog(mContext: Context, textView: TextView) {
        val mcurrentDate = Calendar.getInstance()
        val mYear = mcurrentDate.get(Calendar.YEAR)
        val mMonth = mcurrentDate.get(Calendar.MONTH)
        val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)

        @SuppressLint("SetTextI18n") val mDatePicker =
            DatePickerDialog(mContext, { datepicker, selectedyear, selectedmonth, selectedday ->
                val month = selectedmonth + 1
                val m = if (month > 9) "" + month else "0$month"
                val d = if (selectedday > 9) "" + selectedday else "0$selectedday"
                textView.text = "$selectedyear-$m-$d"
            }, mYear, mMonth, mDay)
        mDatePicker.datePicker.minDate = mcurrentDate.time.time

        mDatePicker.show()
    }

    /**
     * To show DOB Picker and set selected Date in TextView
     *
     * @param mContext context of activity or fragment
     * @param textView TextView in which you want to selected date
     */
    fun showDOBPickerDialog(mContext: Context, textView: TextView) {
        val mcurrentDate = Calendar.getInstance()
        val mYear = mcurrentDate.get(Calendar.YEAR)
        val mMonth = mcurrentDate.get(Calendar.MONTH)
        val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)

        @SuppressLint("SetTextI18n") val mDatePicker =
            DatePickerDialog(mContext, { datepicker, selectedyear, selectedmonth, selectedday ->
                val month = selectedmonth + 1
                val m = if (month > 9) "" + month else "0$month"
                val d = if (selectedday > 9) "" + selectedday else "0$selectedday"
                textView.text = "$selectedyear-$m-$d"
            }, mYear, mMonth, mDay)

        //Deduct 18 years from current date
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -18)
        val date = cal.time
        mDatePicker.datePicker.maxDate = date.time

        mDatePicker.show()
    }

    /**
     * To show Time Picker and set selected Time in TextView
     *
     * @param mContext context of activity or fragment
     * @param textView TextView in which you want to selected time
     */
    @SuppressLint("SetTextI18n")
    fun showTimePickerDialog(mContext: Context, textView: TextView) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker =
            TimePickerDialog(mContext, { timePicker, selectedHour, selectedMinute ->
                val amPM = if (selectedHour >= 12) "PM" else "AM"
                val min = if (selectedMinute > 9) "" + selectedMinute else "0$selectedMinute"
                val hour1: String = if (selectedHour < 10) {
                    "0$selectedHour"
                } else if (selectedHour < 13) {
                    "" + selectedHour
                } else {
                    val h = selectedHour - 12
                    if (h > 9) "" + h else "0$h"
                }
                textView.text = "$hour1:$min $amPM"
            }, hour, minute, false)
        mTimePicker.show()
    }

    /**
     * To get unique number
     *
     * @return Unique Integer
     */
    fun getUniqueNumber(): Int {
        val random = Random()
        return random.nextInt(999999999 - 100000000) + 100000000
    }

    private fun RGB565toARGB888(img: Bitmap): Bitmap {
        val numPixels = img.width * img.height
        val pixels = IntArray(numPixels)
        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.width, 0, 0, img.width, img.height)
        //Create a Bitmap of the appropriate format.
        val result = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
        //Set RGB pixels.
        result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
        return result
    }

    /**
     * Get Real path while getting image from SD card or internal memory
     *
     * @param mContext Avtivity or Fragment Context
     * @param uri      Uri of file
     * @return String path of file
     */
    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    fun getRealPathFromURI(mContext: Context, uri: Uri): String? {
        @SuppressLint("ObsoleteSdkInt") val isKitKat =
            Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(mContext, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(mContext, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(mContext, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                mContext,
                uri,
                null,
                null
            )

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
        //  return cursor.getString(column_index);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Load image into imageView
     *
     * @param mContext    Context of Activity or Fragment
     * @param url         Url that want to load into Imageview
     * @param imageView   ImageView in which url loads
     * @param placeholder Drawable image while loading image from Url
     */
    fun loadImage(mContext: Context, url: Any, imageView: ImageView, placeholder: Int) {
        Glide.with(mContext)
            .load(url)
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(imageView)
    }

    /**
     * Load blur image into imageView
     *
     * @param mContext    Context of Activity or Fragment
     * @param url         Url that want to load into Imageview
     * @param imageView   ImageView in which url loads
     * @param placeholder Drawable image while loading image from Url
     */
    fun loadBlurImage(mContext: Context, url: Any, imageView: ImageView, placeholder: Int) {
        Glide.with(mContext).load(url)
            .apply(bitmapTransform(BlurTransformation(15)))
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(imageView)
    }

    /*/**
     * To share any text on Twitter
     *
     * @param mContext  Context of Activity or Fragment
     * @param shareText Text that you want to share on twitter
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun twitterShare(mContext: Context, shareText: String) {
        //String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s", urlEncode("Testing"),urlEncode("Image Url"));
        val tweetUrl =
            String.format("https://twitter.com/intent/tweet?text=%s", urlEncode(shareText))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
        // Narrow down to official Twitter app, if available:
        val matches = mContext.packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.lowercase(Locale.getDefault())
                    .startsWith("com.twitter")
            ) {
                intent.setPackage(info.activityInfo.packageName)
            }
        }
        mContext.startActivity(Intent.createChooser(intent, "Go To : "))
    }*/

    /**
     * To share any text with image on Instagram
     *
     * @param mContext      Context of Activity or Fragment
     * @param shareText     Text that you want to share on twitter
     * @param drawableImage Drawable image
     */
    @Suppress("DEPRECATION")
    fun instagramShare(mContext: Context, shareText: String, drawableImage: Int) {
        var intent = mContext.packageManager.getLaunchIntentForPackage("com.instagram.android")
        if (intent != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.instagram.android")
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(
                    MediaStore.Images.Media.insertImage(
                        mContext.contentResolver,
                        getBitmapFromDrawable(mContext, drawableImage),
                        mContext.getString(R.string.app_name),
                        shareText
                    )
                )
            )
            shareIntent.type = "image/jpeg"
            mContext.startActivity(shareIntent)
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=" + "com.instagram.android")
            mContext.startActivity(Intent.createChooser(intent, "Go To : "))
        }
    }

    private fun urlEncode(s: String): String {
        try {
            return URLEncoder.encode(s, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e("Twitter!", "UTF-8 should always be supported", e)
            throw RuntimeException("URLEncoder.encode() failed for $s")
        }

    }

    /**
     * To Bitmap of any drawable image
     *
     * @param mContext      Context of Activity or Fragment
     * @param drawableImage Drawable image
     * @return Return Bitmap of any image
     */
    private fun getBitmapFromDrawable(mContext: Context, drawableImage: Int): Bitmap {
        return BitmapFactory.decodeResource(mContext.resources, drawableImage)
    }

    /**
     * To make text view text with underline
     *
     * @param text     Text that want to set into TextView
     * @param textView TextView that want to underline
     */
    fun underlineText(text: String, textView: TextView) {
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content
    }

    /**
     * To rating bar colors
     *
     * @param mContext        Context of activity/fragment
     * @param ratingBar       Rating Bar for which you want to change color
     * @param filledColor     Filled star color
     * @param halfFilledColor Half Filled stars color
     * @param emptyStarColor  Empty stars color
     */
    @Suppress("DEPRECATION")
    fun changeRatingBarColor(
        mContext: Context,
        ratingBar: RatingBar,
        filledColor: Int,
        halfFilledColor: Int,
        emptyStarColor: Int
    ) {
        val stars = ratingBar.progressDrawable as LayerDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            stars.getDrawable(2).colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(mContext, filledColor),
                BlendMode.SRC_ATOP
            )
            stars.getDrawable(1).colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(mContext, halfFilledColor),
                BlendMode.SRC_ATOP
            )
            stars.getDrawable(0).colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(mContext, emptyStarColor),
                BlendMode.SRC_ATOP
            )
        } else {
            stars.getDrawable(2).setColorFilter(
                ContextCompat.getColor(mContext, filledColor),
                PorterDuff.Mode.SRC_ATOP
            ) // for filled stars
            stars.getDrawable(1).setColorFilter(
                ContextCompat.getColor(mContext, halfFilledColor),
                PorterDuff.Mode.SRC_ATOP
            ) // for half filled stars
            stars.getDrawable(0).setColorFilter(
                ContextCompat.getColor(mContext, emptyStarColor),
                PorterDuff.Mode.SRC_ATOP
            ) // for empty stars
        }
    }

    /**
     * To make activity FullScreen
     *
     * @param mContext Context of Activity or Fragment
     */
    fun makeFullScreen(mContext: Context) {
        val activity = mContext as AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * To check that is Email is valid or not
     *
     * @param email Email ID that you want to check
     * @return True id Email is valid otherwise returns False
     */
    fun isEmailValid(email: String): Boolean {
        val pattern: Pattern
        val emailPattern =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(emailPattern)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * To check that is Username is valid or not
     *
     * @param username Username that you want to check
     * @return True Username is valid otherwise returns False
     */
    fun isUsernameValid(username: String): Boolean {
        val p = Pattern.compile("[^A-Za-z0-9]")
        val m = p.matcher(username)
        return !m.find()
    }

    /**
     * To convert HTML text
     *
     * @param text HTML text
     * @return Spanned converted text
     */
    @Suppress("DEPRECATION")
    fun fromHtml(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(text)
        }
    }

    /**
     * To convert string to Base64Encode
     *
     * @param text string
     * @return Base64 encoded string
     */
    fun convertBase64Encode(text: String): String {
        return Base64.encodeToString(text.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * To add textWatcher on Edittext(Prevent to enter space)
     *
     * @param editText all EditTexts that you want to add
     */
    fun setTextWatcherWithoutSpace(vararg editText: EditText) {
        for (et in editText) {
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val str = s.toString()
                    if (str.isNotEmpty() && str.startsWith(" ")) {
                        et.setText(str.trim { it <= ' ' })
                    } else if (str.isNotEmpty() && str.contains(" ")) {
                        et.setText(str.replace(" ".toRegex(), ""))
                        et.setSelection(et.text.length)
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
    }

    /**
     * To add textWatcher on Edittext(Prevent First letter as space)
     *
     * @param editText all EditTexts that you want to add
     */
    fun setTextWatcherWithSpace(vararg editText: EditText) {
        for (et in editText) {
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val str = s.toString()
                    if (str.isNotEmpty() && str.startsWith(" ")) {
                        et.setText(str.trim { it <= ' ' })
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
    }

    /**
     * To make text view text with strike thru
     *
     * @param textView TextView that want to strike thru
     */
    fun makeTextViewStrikeThru(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    /**
     * To convert image into Base64
     *
     * @param imagePath Image File path
     * @return String after convert image into Base64
     */
    fun convertImageToBase64(imagePath: String): String {
        val bm = BitmapFactory.decodeFile(imagePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        val b = baos.toByteArray()
        val base64Text = "data:image/png;base64,"
        return base64Text + Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * Get Unique Android ID
     *
     * @param mContext Context of Activity or Fragment
     * @return String Android ID
     */
    @SuppressLint("HardwareIds")
    fun getUniqueAndroidID(mContext: Context): String {
        return Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * Get two digits after decimal for any double number
     *
     * @param num Number that want to convert
     * @return String Number having two digits after decimal
     */
    fun getTwoDigitsAfterDecimal(num: String?): String {
        var number = num
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val df = nf as DecimalFormat
        df.applyPattern("#0.00")
        number = if (number != null && number.isNotEmpty()) number else "0"
        return "" + df.format(java.lang.Double.parseDouble(number)).replace(",", ".")
    }

    /**
     * Get one digit after decimal for any double number
     *
     * @param num Number that want to convert
     * @return String Number having one digit after decimal
     */
    fun getOneDigitAfterDecimal(num: String?): String {
        var number = num
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val df = nf as DecimalFormat
        df.applyPattern("#0.0")
        number = if (number != null && number.isNotEmpty()) number else "0"
        return "" + DecimalFormat("#0.0").format(java.lang.Double.parseDouble(number))
            .replace(",", ".")
    }

    /**
     * Get Bitmap from image URL
     *
     * @param imageUrl Number that want to convert
     * @return Bitmap of image URL
     */
    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    /**
     * To make first letter capital of any string
     *
     * @param text To make first digit capital
     * @return String with first letter capital
     */
    fun makeFirstLetterCapital(text: String): String {
        return if (text == "") {
            ""
        } else {
            text.substring(0, 1).lowercase(Locale.getDefault()) + text.substring(1)
        }
    }

    /**
     * To get Arraylist from comma seprated String
     *
     * @param text To convert into Arraylist
     * @return Arraylist return that get from String
     */
    fun getArrayFromString(text: String): ArrayList<String> {
        return if (text.isEmpty())
            ArrayList()
        else
            ArrayList(listOf(*text.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()))
    }

    /**
     * To get comma seprated string from Arraylist
     *
     * @param list Arraylist that you want to convert into comma seprated String
     * @return String Comma seprated
     */
    fun getStringFromArray(list: ArrayList<String>): String {
        var value = ""
        for (i in list.indices) {
            value = if (value.isEmpty()) list[i] else value + "," + list[i]
        }
        return value
    }

    /**
     * To get Json string
     *
     * @param mContext context of activity or fragment
     * @param filename filename that placed in assets
     * @return String Json string
     */
    fun loadJSONFromAsset(mContext: Context, filename: String): String? {
        val json: String
        try {
            val `is` = mContext.assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    /**
     * To print HashKey in Logcat(Tag : MY KEY HASH)
     *
     * @param mContext context of activity or fragment
     */
    @Suppress("DEPRECATION")
    fun printHashKey(mContext: Context) {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val info =
                    mContext.packageManager.getPackageInfo(
                        mContext.packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    )
                for (signature in info.signingInfo.signingCertificateHistory) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            } else {
                @SuppressLint("PackageManagerGetSignatures")
                val info =
                    mContext.packageManager.getPackageInfo(
                        mContext.packageName,
                        PackageManager.GET_SIGNATURES
                    )
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    /**
     * To animate view like animate layout changes
     *
     * @param root Root view
     */
    @SuppressLint("ObsoleteSdkInt")
    fun animateView(root: ViewGroup) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root)
        }
    }

    /**
     * To make dialog transparent
     *
     * @param dialog Dialog you want to make transparent
     */
    @SuppressLint("ObsoleteSdkInt")
    fun makeDialogTransparent(dialog: Dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = dialog.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    /**
     * To navigate fragment
     *
     * @param view View on click you want to navigate fragment
     * @param fragmentID destination fragment id
     * @param actionID destination fragment action id
     * @param bundle Bundle data you want to pass to next fragment
     */
    fun navigateFragment(view: View, fragmentID: Int, actionID: Int, bundle: Bundle?) {
        val controller = Navigation.findNavController(view)
        if (controller.currentDestination?.id != fragmentID)
            controller.navigate(actionID, bundle)
    }

    /**
     * To make EditText scroll in ScrollView
     *
     * @param editText EditText
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addEditTextTouchListener(editText: EditText) {
        editText.setOnTouchListener(View.OnTouchListener { v, event ->
            if (editText.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

    /**
     * To set drawable style in Status Bar
     *
     * @param activity Activity
     * @param drawableResId Drawable
     */
    fun setStatusBarDrawable(activity: Activity, drawableResId: Int) {
        val window = activity.window
        val background = ContextCompat.getDrawable(activity, drawableResId)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(activity, android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }

    /**
     * Get Random color (Alpha parameters is to set color type : Light or Dark)
     */
    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    /**
     * Get bitmapDescriptor from vector image to add as marker on google map
     */
    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Get image bitmap in portrait
     */
    fun getImageInPortrait(file: String): Bitmap {
        return try {
            val bounds = BitmapFactory.Options()
            bounds.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file, bounds)
            val opts = BitmapFactory.Options()
            val bm = BitmapFactory.decodeFile(file, opts)
            val exif = ExifInterface(file)
            val orientString: String? = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation =
                orientString?.toInt()
            var rotationAngle = 0
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270
            val matrix = Matrix()
            matrix.setRotate(
                rotationAngle.toFloat(),
                bm.width.toFloat() / 2,
                bm.height.toFloat() / 2
            )
            Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true)
        } catch (e: Exception) {
            BitmapFactory.decodeFile(file)
        }
    }
}