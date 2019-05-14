package com.example.aplicationpokedex.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.coin_list.view.*
import com.example.aplicationpokedex.Models.Coin
import com.example.aplicationpokedex.MyAdapter
import com.example.aplicationpokedex.R


class AdaptCoin (var items: ArrayList<Coin>, val clickListener: (Coin)->Unit) : RecyclerView.Adapter<CoinAdapter.ViewHolder>() ,
        MyAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.coin_list,parent, false)
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
            tv_name.text = item.nombre
            tv_country.text = item.country
            Glide.with(itemView.context)
                    .load(item.img)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.img_coin)
            itemView.setOnClickListener{(clickListener(item))}
        }
    }
}