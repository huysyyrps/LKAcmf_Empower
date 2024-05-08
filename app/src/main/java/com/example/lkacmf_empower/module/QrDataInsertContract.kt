package com.example.lkacmf_empower.module

import com.example.lkacmf_empower.entity.QrDataInsertBack
import com.example.lkacmf_empower.network.BaseEView
import com.example.lkacmf_empower.network.BasePresenter
import okhttp3.RequestBody

interface QrDataInsertContract {
    interface View : BaseEView<presenter?> {
        //获取版本信息
        @Throws(Exception::class)
        fun setQrDataInsert(acmfCode: QrDataInsertBack?)
        fun setQrDataInsertMessage(message: String?)
    }

    interface presenter : BasePresenter {
        //版本信息回调
        fun insertQrData(company: RequestBody?)
    }
}