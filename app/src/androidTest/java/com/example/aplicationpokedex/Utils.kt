package com.example.aplicationpokedex

import com.example.aplicationpokedex.Models.Coin

object AppConstants{
    val dataset_saveinstance_key = "CLE"
    val MAIN_LIST_KEY = "key_list_coin"
}

interface MyAdapter {
    fun changeDataSet(newDataSet : ArrayList<Coin>)
}