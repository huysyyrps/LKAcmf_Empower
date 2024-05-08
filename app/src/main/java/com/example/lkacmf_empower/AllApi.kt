package com.example.lkacmf_empower

import com.example.lkacmf_empower.entity.QrDataInsertBack
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers

import retrofit2.http.POST

interface AllApi {
    /**
     * 二维码数据插入
     */
    @POST(ApiAddress.ACMFCODE)
    @Headers("Content-Type:application/json; charset=UTF-8")
    fun insertQrData(@Body body: RequestBody?): Observable<QrDataInsertBack?>?

}