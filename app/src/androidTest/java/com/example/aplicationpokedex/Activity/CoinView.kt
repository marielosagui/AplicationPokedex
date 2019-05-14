package com.example.aplicationpokedex.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.example.aplicationpokedex.R
import com.example.aplicationpokedex.Models.Coin
import kotlinx.android.synthetic.main.viewer_coin.*

class CoinView : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewer_coin)
        val mIntent = intent
        if(mIntent!=null){
            var receiver: Coin = intent?.extras?.getParcelable("COIN") ?: Coin(
                    R.string.n_a_value.toString(), R.string.n_a_value.toString(), R.string.n_a_value.toString(), 0,0,0, R.string.n_a_value.toString(), false,
                    R.string.n_a_value.toString())
            receiver.img = mIntent.getStringExtra("img")
            receiver.review= mIntent.getStringExtra("review")
            receiver.available = mIntent.getBooleanExtra("ava",true)
            Log.d("Image URL: ", "Recibiendo: $receiver")
            init(receiver)
        }
    }


    fun init(coin: Coin){
        collapsingtoolbarviewer.title = coin.nombre
        country_main_content_fragment.text = coin.country
        value_main_content_fragment.text = coin.value.toString()
        value_us_main_content_fragment.text = coin.value_us.toString()
        year_main_content_fragment.text = coin.year.toString()
        review_main_content_fragment.text = coin.review
        available_main_content_fragment.text = coin.available.toString()


        Glide.with(this)
                .load(coin.img)
                .centerCrop()
                .into(app_bar_image_viewer)
    }
}