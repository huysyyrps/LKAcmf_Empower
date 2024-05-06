package com.example.lkacmf_empower

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.google.gson.Gson
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.permissionx.guolindev.PermissionX
import com.sahooz.library.countryregionpicker.PickFragment
import kotlinx.android.synthetic.main.activity_main.btnSend
import kotlinx.android.synthetic.main.activity_main.tvActivationCode
import kotlinx.android.synthetic.main.activity_main.tvDate
import kotlinx.android.synthetic.main.activity_main.tvReceivedData
import kotlinx.android.synthetic.main.activity_qrdata_insert.iv_left
import kotlinx.android.synthetic.main.activity_qrdata_insert.tvCountry
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.lang.Float
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.Int
import kotlin.String
import kotlin.booleanArrayOf


class QrDataInsertActivity : AppCompatActivity(), View.OnClickListener {
    var dateString = ""
    var receivedData = ""
    var activationCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrdata_insert)

        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.CAMERA)
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        val currentDate = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime: String = dateFormat.format(currentDate)
        tvDate.text = currentTime

        dateString = currentTime

        tvDate.setOnClickListener(this)
        btnSend.setOnClickListener(this)
        iv_left.setOnClickListener(this)
        tvCountry.setOnClickListener(this)

        if (requestList.isNotEmpty()) {
            PermissionX.init(this)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        Log.e("TAG", "所有申请的权限都已通过")
                        ScanUtil.startScan(this, 1, null);
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        finish()
                    }
                }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDate -> {
                val selectedDate = Calendar.getInstance()
                val startDate = Calendar.getInstance()
                val endDate = Calendar.getInstance()
                //正确设置方式 原因：注意事项有说明
                startDate[2023, 0] = 1
                endDate[2030, 11] = 31
                var pvTime = TimePickerBuilder(this) { date, v -> //选中事件回调
                    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    dateString = sdf.format(date)
                    tvDate.text = dateString
                }
                    .setType(booleanArrayOf(true, true, true, true, true, false)) // 默认全部显示
                    .setCancelText("取消") //取消按钮文字
                    .setSubmitText("确定") //确认按钮文字
                    .setTitleSize(20) //标题文字大小
                    .setTitleText("时间选择") //标题文字
                    .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(true) //是否循环滚动
                    .setDate(selectedDate) // 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate) //起始终止年月日设定
                    .setLabel("年", "月", "日", "时", "分", "秒") //默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false) //是否显示为对话框样式
                    .build()

                pvTime.show()
            }
            R.id.btnSend->{
                dateString = dateString.replace("-", "")
                dateString = dateString.replace(":", "")
                dateString = dateString.replace(" ", "")
                dateString = BinaryChange.tenToHex(dateString.substring(0, 2).toInt()) +
                        BinaryChange.tenToHex(dateString.substring(2, 4).toInt()) +
                        BinaryChange.tenToHex(dateString.substring(4, 6).toInt()) +
                        BinaryChange.tenToHex(dateString.substring(6, 8).toInt())
//                        BinaryChange.tenToHex(dateString.substring(8, 10).toInt()) +
//                        BinaryChange.tenToHex(dateString.substring(10, 12).toInt())
//                val float1 = Float.intBitsToFloat(Integer.valueOf(dateString, 16))
                val float1 = Float.intBitsToFloat(dateString.toLong(16).toInt())

                val hex2 = receivedData.substring(0, 8)
                val hex3 = receivedData.substring(8, 16)
                val hex4 = receivedData.substring(16, 24)
//                val float2 = Float.intBitsToFloat(Integer.valueOf(hex2, 16))
//                val float3 = Float.intBitsToFloat(Integer.valueOf(hex3, 16))
//                val float4 = Float.intBitsToFloat(Integer.valueOf(hex4, 16))
                val float2 = Float.intBitsToFloat(hex2.toLong(16).toInt())
                val float3 = Float.intBitsToFloat(hex3.toLong(16).toInt())
                val float4 = Float.intBitsToFloat(hex4.toLong(16).toInt())

                val count1 = (float2 + float3 + float4) / float1.toFloat()
                val count2 = (float1 + float3 + float4) / float2.toFloat()
                val count3 = (float1 + float2 + float4) / float3.toFloat()
                val count4 = (float1 + float2 + float3) / float4.toFloat()

                val hexThicken1: String = StringToHex(count1)
                val hexThicken2: String = StringToHex(count2)
                val hexThicken3: String = StringToHex(count3)
                val hexThicken4: String = StringToHex(count4)

//                val hexA = dateString.substring(0, 8)
//                val hexB = dateString.substring(4, 12)
//                val intA = Integer.valueOf(hexA, 16)
//                val intB = Integer.valueOf(hexB, 16)
//                val countA = (intA.toFloat() - intB) / (intA + intB)
//                val thicken: String = Integer.toHexString(Float.floatToIntBits(countA))
//                var hexThicken = BinaryChange.addZeroForNum(thicken, 8)
//                var date = receivedData + activationCode + dateString + hexThicken
                var data = dateString+hexThicken1 + hexThicken2 + hexThicken3 + hexThicken4
                val params = HashMap<String, String>()
                params["received_data"] = receivedData
                params["activation_code"] = activationCode
                params["use_date"] = data
                val gson = Gson()
                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    gson.toJson(params)
                )
//                acmfCodePresenter.getAcmfCode(requestBody)
            }
            R.id.iv_left->{
                finish()
            }
            R.id.tvCountry->{
            }
        }
    }

    fun StringToHex(data: kotlin.Float): String {
        return Integer.toHexString(Float.floatToIntBits(data))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        if (requestCode == 1) {
            val obj: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                //展示解码结果
                Log.e("DefinedActivity", obj.originalValue.toString())
                var data = obj.originalValue.toString().split("/")
                receivedData = data[0]
                activationCode = data[1]
                tvReceivedData.text = receivedData
                tvActivationCode.text = activationCode
            }
        }
    }

}