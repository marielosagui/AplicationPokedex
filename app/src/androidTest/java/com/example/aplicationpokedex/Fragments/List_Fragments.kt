package com.example.aplicationpokedex.Fragments


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplicationpokedex.AppConstants
import com.example.aplicationpokedex.Adapters.AdaptCoin
import com.example.aplicationpokedex.Adapters.CoinLandAdapter
import com.example.aplicationpokedex.Models.Coin
import com.example.aplicationpokedex.MyAdapter
import com.example.aplicationpokedex.R
import kotlinx.android.synthetic.main.fragment_main_list.view.*

class List_Fragment : Fragment() {
    private lateinit var  listCoin :ArrayList<Coin>
    private lateinit var CoinAdapter : MyAdapter
    var listenerTools :  ListenerTools? = null
    companion object {
        fun newInstance(dataset : ArrayList<Coin>): List_Fragment {
            val newFragment = List_Fragment()
            newFragment.listCoin = dataset
            return newFragment
        }
    }

    interface ListenerTools{
        fun managePortraitItemClick(item: Coin)
        fun manageLandscapeItemClick(item: Coin)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main_list, container, false)
        if(savedInstanceState != null) listCoin = savedInstanceState.getParcelableArrayList<Coin>(AppConstants.MAIN_LIST_KEY)!!
        initRecyclerView(resources.configuration.orientation, view)
        return view
    }

    fun initRecyclerView(orientation:Int, container:View){
        val linearLayoutManager = LinearLayoutManager(this.context)
        val gridLayoutManager = GridLayoutManager(this.context,2)
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            CoinAdapter =
                    CoinAdapter(
                            listCoin,
                            { item: Coin -> listenerTools?.managePortraitItemClick(item) })


            container.rv_coins.adapter = CoinAdapter as AdaptCoin
            container.rv_coins.apply {
                setHasFixedSize(true)
                layoutManager = gridLayoutManager
            }
        }

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            CoinAdapter =
                    CoinLandAdapter(
                            listCoin,
                            { item: Coin -> listenerTools?.manageLandscapeItemClick(item) })
            container.rv_coins.adapter = CoinAdapter as CoinLandAdapter
            container.rv_coins.apply {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
            }
        }

    }

    fun updateCoinAdapter(coinList: ArrayList<Coin>){ CoinAdapter.changeDataSet(coinList) }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ListenerTools) {
            listenerTools = context
        } else {
            throw RuntimeException("Se necesita una implementación de  la interfaz")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(AppConstants.MAIN_LIST_KEY, listCoin)
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        listenerTools = null
    }
}