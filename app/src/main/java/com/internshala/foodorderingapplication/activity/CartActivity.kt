package com.internshala.foodorderingapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.adapter.CartRecyclerAdapter
import com.internshala.foodorderingapplication.database.FoodItems
import com.internshala.foodorderingapplication.database.RestaurantDatabase
import com.internshala.foodorderingapplication.database.RestaurantEntity
import com.internshala.foodorderingapplication.fragment.RestaurantMenuFragment
import com.internshala.foodorderingapplication.fragment.user_id
import com.internshala.foodorderingapplication.model.RestaurantItems
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var txtOrderRestaurant : TextView
    lateinit var recyclerCart : RecyclerView
    lateinit var btnPlaceOrder : Button
    lateinit var toolbar: Toolbar
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter

    lateinit var btnConfirm : Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar : ProgressBar

    lateinit var relativeContent: RelativeLayout
    lateinit var relativeContent1 : RelativeLayout


    lateinit var sharedPreferences: SharedPreferences

    var userId = "123"
    var resId = "1"
    var restaurantName = "Name of Restaurant"

    var orderList  = listOf<FoodItems>()
    var order = arrayListOf<RestaurantItems>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        txtOrderRestaurant = findViewById(R.id.txtOrderRestaurant)
        recyclerCart = findViewById(R.id.recyclerCart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        toolbar = findViewById(R.id.Toolbar)

        btnConfirm = findViewById(R.id.btnConfirm)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        relativeContent = findViewById(R.id.RelativeContent)
        relativeContent1 = findViewById(R.id.RelativeContent1)

        relativeContent1.visibility = View.GONE
        btnConfirm.visibility = View.GONE

        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE



        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("UserId" , "123").toString()

        println("USER ID is $userId")

        setUpToolbar()

        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getString("res_Id").toString()
         restaurantName = bundle?.getString("resName").toString()

        txtOrderRestaurant.text = restaurantName

        layoutManager = LinearLayoutManager(this@CartActivity)

         orderList = RetrieveOrder(this,resId).execute().get()

        println("Order List is $orderList")

        for(data in orderList) {
            order.addAll(

                Gson().fromJson(data.food, Array<RestaurantItems>::class.java).asList()

            )
            println("Data is ${data.food}")
        }
        println("Food is $order[0]")

       recyclerAdapter = CartRecyclerAdapter(this@CartActivity , order)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager =layoutManager

        var total_cost = 0
        for(i in 0 until order.size ){

            total_cost+=order[i].itemPricePerItem.toInt()

        }



        btnPlaceOrder.text = "PLACE ORDER(TOTAL: RS. $total_cost)"

        btnPlaceOrder.setOnClickListener {

            relativeContent.visibility = View.GONE
            btnPlaceOrder.visibility = View.GONE


            progressLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

            val queue = Volley.newRequestQueue(this)

            val url = "http://13.235.250.119/v2/place_order/fetch_result/"



            val jsonParams = JSONObject()
            jsonParams.put("user_id" , userId)
            jsonParams.put("restaurant_id" , resId)
            jsonParams.put("total_cost" , total_cost)
            val food = JSONArray()

            for(i in 0 until order.size)
            {

                val foodItem = JSONObject()
                foodItem.put("food_item_id" , order[i].itemId)
                food.put(i , foodItem)

            }

            jsonParams.put("food" , food)



            println("length is ${supportFragmentManager.backStackEntryCount}")

            if (ConnectionManager().checkConnectivity(this)){

                val jsonRequest = object : JsonObjectRequest(Method.POST , url , jsonParams , Response.Listener {

                    println("Response For Cart is $it")
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            println("Response is $it")
                            DeleteOrder(this , resId).execute().get()

                            relativeContent1.visibility = View.VISIBLE
                            btnConfirm.visibility = View.VISIBLE
                            btnConfirm.setOnClickListener {

                                val intent = Intent(this@CartActivity , MainActivity::class.java)
                                intent.putExtra("order_success" , true)
                                sharedPreferences.edit().putBoolean("item_clicked" , false).apply()
                                startActivity(intent)
                                finish()

                            }

                        } else {
                            Toast.makeText(this, "Some Error has Occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    catch (e : Exception){

                        Toast.makeText(this, "JSON Error has Occurred", Toast.LENGTH_SHORT)
                            .show()

                    }


                },Response.ErrorListener {


                    Toast.makeText(this, "Some  Unexpected Error has Occurred", Toast.LENGTH_SHORT)
                        .show()

                }){

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "4f1fadec00b2ac"
                        return headers
                    }



                    }

                queue.add(jsonRequest)

            }
            else
            {

                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this)

                }
                dialog.setPositiveButton("Open Settings") { text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()


                }

                dialog.create()
                dialog.show()

            }

        }

        }



    class RetrieveOrder(val context: Context , val restaurantId : String) : AsyncTask<Void, Void, List<FoodItems>>(){



        override fun doInBackground(vararg p0: Void?): List<FoodItems> {

            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant_db").build()
            return db.foodDao().getAllOrders(restaurantId)

        }


    }
    class DeleteOrder(val context: Context , val restaurantId : String) : AsyncTask<Void, Void, Boolean>(){



        override fun doInBackground(vararg p0: Void?) : Boolean {

            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant_db").build()
            db.foodDao().deleteOrders(restaurantId)
            db.close()
            return true

        }


    }


    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {

        DeleteOrder(this@CartActivity , resId).execute().get()

        super.onBackPressed()
    }

    override fun onPause() {

        DeleteOrder(this@CartActivity , resId).execute().get()
        super.onPause()
    }


}