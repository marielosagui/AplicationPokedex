package com.example.aplicationpokedex.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplicationpokedex.Models.Coin
import com.example.aplicationpokedex.MyAdapter
import com.example.aplicationpokedex.R
import kotlinx.android.synthetic.main.coin_detail.view.*
import kotlinx.android.synthetic.main.coin_list.view.*

class LandCoinAdapt(var items: ArrayList<Coin>, val clickListener: (Coin)->Unit) : RecyclerView.Adapter<LandCoinAdapt.ViewHolder>() ,

        MyAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.coin_detail,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], clickListener)

    override fun changeDataSet(newDataSet: ArrayList<Coin>) {

        this.items = newDataSet

        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item: Coin, clickListener:  (Coin) -> Unit) = with(itemView){
            tv_det_name.text = item.nombre
            tv_det_country.text = item.country

            itemView.setOnClickListener{(clickListener(item))}

        }

    }

}