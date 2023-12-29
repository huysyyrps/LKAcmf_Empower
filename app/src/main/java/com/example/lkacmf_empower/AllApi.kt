package com.example.lkacmf_empower

import com.example.lkacmf_empower.entity.AcmfCode
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers

import retrofit2.http.POST

interface AllApi {
    /**
     * 生成授权码
     */
    @POST(ApiAddress.ACMFCODE)
    @Headers("Content-Type:application/json; charset=UTF-8")
    fun getVersionInfo(@Body body: RequestBody?): Observable<AcmfCode?>?

}