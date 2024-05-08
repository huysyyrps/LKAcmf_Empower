package com.example.lkacmf_empower.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lkacmf_empower.DialogUtil
import com.example.lkacmf_empower.R
import com.example.lkacmf_empower.adapter.AdapterPositionCallBack
import com.example.lkacmf_empower.adapter.CountryListAdapter
import com.sahooz.library.countryregionpicker.CountryOrRegion
import kotlinx.android.synthetic.main.activity_pick_country.etSearch
import kotlinx.android.synthetic.main.activity_pick_country.iv_left
import kotlinx.android.synthetic.main.activity_pick_country.recyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PickCountryActivity : AppCompatActivity() {
    var location: Location? = null
    var selectIndex = 0
    private lateinit var adapter: CountryListAdapter
    private var countryList = java.util.ArrayList<CountryOrRegion>()
    private var selectCountryList = java.util.ArrayList<CountryOrRegion>()
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingInflatedId", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_country)


        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        var permissionTag = DialogUtil.requestPermission(this,requestList)




        iv_left.setOnClickListener{
            finish()
        }

        countryList = java.util.ArrayList<CountryOrRegion>()
        val br = BufferedReader(InputStreamReader(resources.assets.open("code.json")))
        var line: String?
        val sb = StringBuilder()
        while (br.readLine().also { line = it } != null) sb.append(line)
        br.close()
        val ja = JSONArray(sb.toString())
        for (i in 0 until ja.length()) {
            val jo = ja.getJSONObject(i)
            var flag = 0
            var translate = ""
            val locale = jo.getString("locale")
            if (!TextUtils.isEmpty(locale)) {
                flag = resources.getIdentifier("flag_" + locale.lowercase(Locale.getDefault()), "drawable", packageName)
                translate = jo.getString("name")
            }
            val name = translate
            countryList.add(
                CountryOrRegion(
                    jo.getInt("code"),
                    name,
                    translate,  //                    inChina ? jo.getString("pinyin") : name,
                    name,
                    locale,
                    flag
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(countryList, Comparator.comparing { obj: CountryOrRegion -> obj.getPinyin() })
        }
        selectCountryList.clear()
        selectCountryList.addAll(countryList)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = CountryListAdapter(
            selectCountryList,
            selectIndex,
            this,
            object : AdapterPositionCallBack {
                override fun backPosition(index: Int) {
                    selectIndex = index
                    val intent = Intent()
                    intent.putExtra("country", selectCountryList[index].name)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val string = s.toString()
                selectCountryList.clear()
                for (countryOrRegion in countryList) {
                    if (countryOrRegion.name.lowercase(Locale.getDefault())
                            .contains(string.lowercase(Locale.getDefault()))
                        || countryOrRegion.translate.lowercase(Locale.getDefault()).contains(
                            string.lowercase(
                                Locale.getDefault()
                            )
                        )
                        || countryOrRegion.getPinyin().lowercase(Locale.getDefault()).contains(
                            string.lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) selectCountryList.add(countryOrRegion)
                }
                adapter.update(selectCountryList)
            }
        })


        var manger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manger .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Log.e("TAG","无gps模块")
        }else{
            initSystemTime()
        }
    }

    /**
     * 用于获取GPS时间
     */
    private var locationManager: LocationManager? = null

    /**
     * 取消后台定位的时间
     */
    private val CANCEL_LOCATION_DELAY = 5 * 60 * 1000

    /**
     * 避免多次取消定位和反注册
     */
    private var isHadCancelLocation = false

    /**
     * 如果网络不可用，使用GPS进行刷新系统时间，系统时间更新后会自动更新TextClock和DateClock；如果有网络，理论上会自动刷新系统时间，所以不处理。
     */
    @SuppressLint("RestrictedApi")
    private fun initSystemTime() {
//        if (!NetworkHelper.isNetworkAvailable(getContext())) {
//            getLocationBySystem()
//            // 注意，这里请用原始Handler.postDelay代替，我用的自定义延时类。
//            ArchTaskExecutor.getInstance().postToMainThreadDelay(cancelLocationRunnable, CANCEL_LOCATION_DELAY)
//        }
        getLocationBySystem()
    }

    // 注意，普通app需要申请定位权限，我的是系统级app，所以忽略。
    @SuppressLint("MissingPermission")
    private fun getLocationBySystem() {
        Log.e("TAG","无网络启动获取Gps定位")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        // 注册位置监听器
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    /**
     * 通过GPS获取定位信息
     */
    private val locationListener = LocationListener { location ->
        if (null != location) {
            // 就这里是关键获取代码，其实高德地图Api能自己触发系统时间的更新，不知道怎么实现的，只要打开他们的地图就可以，不管是第三方的高德地图app还是自己内嵌的高德地图api模块，理论上也是location.getTime()获取到的，不理解的是他们为什么有权限更新系统时间。
            val gpsTime = location.time
            cancelLocationUpdates()
            Log.e("TAG","Gps定位获取成功并注销定位监听,GPS时间 = $gpsTime")
            // 用过这个进行模拟测试，resetSystemTime(1712800932000L);显示的时间是2024-04-11 10:02:12,正确
            resetSystemTime(gpsTime)
        }
    }

    private val cancelLocationRunnable = Runnable {
        Log.e("TAG","获取定位超时，注销定位监听")
        cancelLocationUpdates()
    }

    /**
     * 获取定位时间成功取消定位
     */
    fun cancelLocationUpdates() {
        Log.e("TAG","注销定位监听$isHadCancelLocation")
        if (locationManager != null && !isHadCancelLocation) {
            locationManager!!.removeUpdates(locationListener)
            isHadCancelLocation = true
            locationManager = null
        }
    }

    /**
     * 注意，我的是系统级app，所以忽略动态申请权限，普通应用无法设置系统时间但是可以参考取值。
     * 系统级app配置 <uses-permission android:name="android.permission.SET_TIME"></uses-permission> 权限即可
     */
    @SuppressLint("MissingPermission")
    private fun resetSystemTime(utcTimeMillis: Long) {
        try {
            // 将UTC时间转换为Date对象
            val locationTime = Date(utcTimeMillis)
            val localTimeMillis = locationTime.time

            // 获取AlarmManager实例
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            // 设置系统时间（本地时间）
            if (alarmManager != null) {
                alarmManager.setTime(localTimeMillis)
                Log.e("TAG","重置系统时间成功$localTimeMillis")
            }
        } catch (e: Exception) {
            Log.e("TAG","重置系统时间失败")
        }
    }
}