package com.example.lkacmf_empower.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lkacmf_empower.R
import com.sahooz.library.countryregionpicker.CountryOrRegion
import java.util.Locale

internal class CountryListAdapter(
    var dataList: ArrayList<CountryOrRegion>,
    private var selectIndex: Int,
    var context: Activity,
    val adapterPositionCallBack: AdapterPositionCallBack
)  : RecyclerView.Adapter<CountryListAdapter.ViewHolder>(){
    //在内部类里面获取到item里面的组件
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val linCountry: LinearLayout = view.findViewById(R.id.linCountry)
        val ivFlag: ImageView = view.findViewById(R.id.ivFlag)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    //重写的第一个方法，用来给制定加载那个类型的Recycler布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_country,parent,false)
        var viewHolder= ViewHolder(view)
        //单机事件
        viewHolder.linCountry.setOnClickListener {
            var position= viewHolder.layoutPosition
            selectIndex = position
            notifyDataSetChanged()
            adapterPositionCallBack.backPosition(selectIndex)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position].name
        val locale: String = dataList[position].locale
        var flag = context.resources.getIdentifier(
            "flag_" + locale.lowercase(Locale.getDefault()),
            "drawable",
            context.packageName
        )
        holder.ivFlag.setImageResource(flag)
    }

    @JvmName("setSelectIndex1")
    fun update(selectCountryList: ArrayList<CountryOrRegion>,){
        dataList = selectCountryList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}