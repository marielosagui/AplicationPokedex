package com.example.aplicationpokedex.Activity

import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.aplicationpokedex.Models.Coin
import com.example.aplicationpokedex.Fragments.List_Fragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainListFragment.ListenerTools {

    private var dbHelper = Database(this)
    private var coinList = ArrayList<Coin>()
    private lateinit var mainFragment: List_Fragment
    private lateinit var mainContentFragment: MainDetailsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener{ view ->

            deleteCoins()

            FetchCoinTask().execute("")

            Snackbar.make(view, "Actualizando monedas", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)

        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        coinList = ArrayList<Coin>()

        var coins = readCoins()

        Log.d("Read", coins.toString())
        if(coins.isNotEmpty())
        {
            coinList = coins as ArrayList<Coin>
            Log.d("Read", "Leido desde la base de datos local")
            Toast.makeText(this@MainActivity, "Leido desde la base de datos local", Toast.LENGTH_LONG).show()

        }
        else

        {
            FetchCoinTask().execute("")
        }

        initMainFragment()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(AppConstants.dataset_saveinstance_key, coinList)
        super.onSaveInstanceState(outState)
    }

    override fun managePortraitItemClick(item: Coin) {
        val coinBundle = Bundle()
        coinBundle.putParcelable("COIN", item)
        startActivity(Intent(this, CoinViewer::class.java).putExtras(coinBundle).putExtra("img",item.img).putExtra("review", item.review).putExtra("ava",item.available))
        Log.d("image","Enviado + $coinBundle")
    }

    override fun manageLandscapeItemClick(item: Coin) {
        mainContentFragment = MainDetailsFragment.newInstance(item)
        changeFragment(R.id.land_main_cont_fragment, mainContentFragment)
    }


    fun initMainFragment() {
        mainFragment = MainListFragment.newInstance(coinList)
        val resource = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            R.id.main_fragment

        else{
            mainContentFragment = MainDetailsFragment.newInstance(Coin(R.string.n_a_value.toString(),
                    R.string.n_a_value.toString(),
                    R.string.n_a_value.toString(),
                    0,
                    0,
                    0,
                    R.string.n_a_value.toString(),
                    false,
                    R.string.n_a_value.toString()))

            changeFragment(R.id.land_main_cont_fragment, mainContentFragment)
            R.id.land_main_fragment
        }

        changeFragment(resource, mainFragment)

    }

    /*fun addCoinToList(coin: Coin){

        coinList.add(coin)

        mainFragment.updateCoinAdapter(coinList)

        Log.d("Number", coinList.size.toString())

    }*/

    private fun changeFragment(id: Int, frag: Fragment) {
        supportFragmentManager.beginTransaction().replace(id, frag).commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {

            drawer_layout.closeDrawer(GravityCompat.START)

        } else {

            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_all -> {
                coinList = ArrayList<Coin>()
                var coins = readCoins()
                Log.d("Read", coins.toString())
                coinList = coins as ArrayList<Coin>
                Log.d("Read", "Mostrando todas las monedas")
                initMainFragment()
            }
            R.id.nav_el_salvador -> {
                showCoins("El Salvador")
            }
            R.id.nav_guatemala -> {
                showCoins("Guatemala")
            }
            R.id.nav_honduras -> {
                showCoins("Honduras")
            }
            R.id.nav_costa_rica -> {
                showCoins("Costa Rica")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    fun showCoins(country: String){
        coinList = ArrayList<Coin>()
        var coins = filterCoins(country)
        coinList = coins as ArrayList<Coin>
        Log.d("Read", "Monedas de $country")
        Log.d("Read", coins.toString())
        Toast.makeText(this@MainActivity, "Filtro: $country", Toast.LENGTH_LONG).show()
        initMainFragment()
    }
    private inner class FetchCoinTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg query: String): String {
            if (query.isNullOrEmpty()) return ""
            val ID = query[0]
            val pokeAPI = NetworkUtils().buildUrl("",ID)
            return try {
                NetworkUtils().getResponseFromHttpUrl(pokeAPI)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }

        override fun onPostExecute(coinInfo: String){
            Log.d("msg", coinInfo)
            deleteCoins()
            val root = JSONObject(coinInfo)
            val results = root.getJSONArray("buscadores")
            var x = 4
            while(x < results.length()){
                val result = JSONObject(results[x].toString())
                var coin = Coin(result.getString("_id"),
                        result.getString("nombre"),
                        result.getString("country"),
                        result.getInt("value"),
                        result.getInt("value_us"),
                        result.getInt("year"),
                        result.getString("review"),
                        result.getBoolean("available"),
                        result.getString("img"))
                coinList.add(coin)
                createCoin(coin)
                x++
            }

            mainFragment.updateCoinAdapter(coinList)
            Log.d("Read", "Leido desde api")
            Toast.makeText(this@MainActivity, "Leido desde api", Toast.LENGTH_LONG).show()
        }
    }
    private fun readCoins(): List<Coin> {
        val db = dbHelper.readableDatabase
        var coinList2 = ArrayList<Coin>()
        val projection = arrayOf(
                BaseColumns._ID,
                DatabaseContract.CoinEntry.COLUMN_NOMBRE,
                DatabaseContract.CoinEntry.COLUMN_COUNTRY,
                DatabaseContract.CoinEntry.COLUMN_VALUE,
                DatabaseContract.CoinEntry.COLUMN_VALUE_US,
                DatabaseContract.CoinEntry.COLUMN_YEAR,
                DatabaseContract.CoinEntry.COLUMN_REVIEW,
                DatabaseContract.CoinEntry.COLUMN_AVAILABLE,
                DatabaseContract.CoinEntry.COLUMN_IMG
        )
        val sortOrder = "${DatabaseContract.CoinEntry.COLUMN_NOMBRE} DESC"
        val cursor = db.query(
                DatabaseContract.CoinEntry.TABLE_NAME, // nombre de la tabla
                projection, // columnas que se devolverán
                null, // Columns where clausule
                null, // values Where clausule
                null, // Do not group rows
                null, // do not filter by row
                sortOrder // sort order
        )
        with(cursor) {
            while (moveToNext()) {
                var coin = Coin(
                        getString(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_NOMBRE)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_COUNTRY)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_VALUE)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_VALUE_US)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_YEAR)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_REVIEW)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_AVAILABLE)).toBoolean(),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_IMG))
                )
                coinList2.add(coin)
            }
        }
        return coinList2
    }
    private fun filterCoins(country:String): List<Coin> {
        val db = dbHelper.readableDatabase
        var coinList2 = ArrayList<Coin>()
        val projection = arrayOf(
                BaseColumns._ID,
                DatabaseContract.CoinEntry.COLUMN_NOMBRE,
                DatabaseContract.CoinEntry.COLUMN_COUNTRY,
                DatabaseContract.CoinEntry.COLUMN_VALUE,
                DatabaseContract.CoinEntry.COLUMN_VALUE_US,
                DatabaseContract.CoinEntry.COLUMN_YEAR,
                DatabaseContract.CoinEntry.COLUMN_REVIEW,
                DatabaseContract.CoinEntry.COLUMN_AVAILABLE,
                DatabaseContract.CoinEntry.COLUMN_IMG
        )
        val sortOrder = "${DatabaseContract.CoinEntry.COLUMN_NOMBRE} DESC"
        val whereClause = DatabaseContract.CoinEntry.COLUMN_COUNTRY + "=?"
        val whereArgs = arrayOf<String>(country)
        val cursor = db.query(
                DatabaseContract.CoinEntry.TABLE_NAME, // nombre de la tabla
                projection, // columnas que se devolverán
                whereClause, // Columns where clausule
                whereArgs, // values Where clausule
                null, // Do not group rows
                null, // do not filter by row
                sortOrder // sort order
        )
        with(cursor) {
            while (moveToNext()) {
                var coin = Coin(
                        getString(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_NOMBRE)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_COUNTRY)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_VALUE)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_VALUE_US)),
                        getInt(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_YEAR)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_REVIEW)),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_AVAILABLE)).toBoolean(),
                        getString(getColumnIndexOrThrow(DatabaseContract.CoinEntry.COLUMN_IMG))
                )
                coinList2.add(coin)
            }
        }
        return coinList2
    }

    private fun createCoin(item:Coin)
    {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.CoinEntry.COLUMN_COUNTRY, item.country)
            put(DatabaseContract.CoinEntry.COLUMN_IMG, item.img)
            put(DatabaseContract.CoinEntry.COLUMN_YEAR, item.year)
            put(DatabaseContract.CoinEntry.COLUMN_REVIEW, item.review)
            put(DatabaseContract.CoinEntry.COLUMN_AVAILABLE, item.available.toString())
            put(DatabaseContract.CoinEntry.COLUMN_ID, item._id)
            put(DatabaseContract.CoinEntry.COLUMN_NOMBRE, item.nombre)
            put(DatabaseContract.CoinEntry.COLUMN_VALUE, item.value)
            put(DatabaseContract.CoinEntry.COLUMN_VALUE_US, item.value_us)
        }

        val newRowId = db?.insert(DatabaseContract.CoinEntry.TABLE_NAME, null, values)
    }
    private fun deleteCoins()
    {
        val db = dbHelper.writableDatabase
        val newRowId = db?.delete(DatabaseContract.CoinEntry.TABLE_NAME, null, null)
    }
}