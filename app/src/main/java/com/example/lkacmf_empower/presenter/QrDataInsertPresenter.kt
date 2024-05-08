package com.example.lkacmf_empower.presenter

import android.content.Context
import com.example.lkacmf_empower.R
import com.example.lkacmf_empower.entity.QrDataInsertBack
import com.example.lkacmf_empower.module.QrDataInsertContract
import com.example.lkacmf_empower.network.BaseObserverNoEntry
import com.example.lkacmf_empower.network.NetStat
import com.example.lkacmf_empower.network.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class QrDataInsertPresenter constructor(context : Context, view: QrDataInsertContract.View)  : QrDataInsertContract.presenter {

    var context: Context = context
    var view: QrDataInsertContract.View = view


    override fun insertQrData(company: RequestBody?) {
        RetrofitUtil().getInstanceRetrofit()?.initRetrofitMain()?.insertQrData(company)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : BaseObserverNoEntry<QrDataInsertBack?>(
                context!!,
                context!!.resources.getString(R.string.handler_data)
            ) {

                override fun onSuccees(t: QrDataInsertBack?) {
                    if (t?.state == 200) {
                        view.setQrDataInsert(t)
                    } else {
                        view.setQrDataInsertMessage(context.resources.getString(R.string.date_error))
                    }
                }

                override fun onFailure(e: Throwable?, isNetWorkError: Boolean) {
                    if (NetStat.isNetworkConnected(context)) {
                        view.setQrDataInsertMessage("" + e!!.message)
                    } else {
                        view.setQrDataInsertMessage(context.resources.getString(R.string.net_error))
                    }
                }
            })
    }
}
