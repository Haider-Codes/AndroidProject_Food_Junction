
package com.internshala.foodorderingapplication.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.activity.CartActivity
import com.internshala.foodorderingapplication.activity.MainActivity
import com.internshala.foodorderingapplication.adapter.RestaurantMenuRecyclerAdapter
import com.internshala.foodorderingapplication.database.FoodItems
import com.internshala.foodorderingapplication.database.RestaurantDatabase
import com.internshala.foodorderingapplication.model.RestaurantItems
import com.internshala.foodorderingapplication.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class RestaurantMenuFragment : Fragment() {

    lateinit var recyclerRestaurantMenu : RecyclerView
    lateinit var btnProceedToCart: Button
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter

    lateinit var toolbar: Toolbar

    lateinit var linearLayoutContent2: LinearLayout

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    var restaurant_name: String? = "Name Of Restaurant"
    var res_id: String = "1"


    var userId = "1"

    val restaurantItems = arrayListOf<RestaurantItems>()
    val orderList = arrayListOf<RestaurantItems>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false)

        recyclerRestaurantMenu = view.findViewById(R.id.recyclerRestaurantMenu)
        btnProceedToCart = view.findViewById(R.id.btnProceedToCart)
        layoutManager = LinearLayoutManager(context)

        linearLayoutContent2 = view.findViewById(R.id.linearLayoutContent2)

        (context as MainActivity).toolbar.visibility = View.GONE

        toolbar = view.findViewById(R.id.Toolbar)

            res_id = arguments?.getString("restaurant_id").toString()
            restaurant_name = arguments?.getString("restaurant_name")

        toolbar.title = restaurant_name


        toolbar.setNavigationIcon(R.drawable.ic_back_button_asset)


        toolbar.setNavigationOnClickListener {

            (context as MainActivity).onBackPressed()
           // (context as MainActivity).toolbar.visibility = View.VISIBLE

        }

        println("Restaurant id is $res_id")

        println("Restaurant name is $restaurant_name")
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        sharedPreferences = (activity as Context).getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )

        userId = sharedPreferences.getString("UserId", "1").toString()

        progressLayout.visibility = View.VISIBLE

        println("Length is ${(context as MainActivity).supportFragmentManager.backStackEntryCount}")
        btnProceedToCart.visibility = View.INVISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$res_id"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {

                        println("Response of Restaurant Details is $it")

                        progressLayout.visibility = View.GONE

                        val data1 = it.getJSONObject("data")

                        val success = data1.getBoolean("success")
                        if (success) {

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val restaurantItemsJsonObject = data.getJSONObject(i)
                                val restaurantItemsObject = RestaurantItems(

                                   // userId,
                                    restaurantItemsJsonObject.getString("id"),
                                    restaurantItemsJsonObject.getString("name"),
                                    restaurantItemsJsonObject.getString("cost_for_one")
                                    //restaurantItemsJsonObject.getString("restaurant_id")

                                )


                                restaurantItems.add(restaurantItemsObject)


                                recyclerAdapter = RestaurantMenuRecyclerAdapter(
                                    activity as Context,
                                    restaurantItems,object : RestaurantMenuRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: RestaurantItems) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                btnProceedToCart.visibility = View.VISIBLE
                                            }
                                            sharedPreferences.edit().putBoolean("item_clicked" , true).apply()
                                        }

                                        override fun onRemoveItemClick(foodItem: RestaurantItems) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                btnProceedToCart.visibility = View.GONE
                                            }
                                            sharedPreferences.edit().putBoolean("item_clicked" , false).apply()
                                        }
                                    })

                                recyclerRestaurantMenu.adapter = recyclerAdapter
                                recyclerRestaurantMenu.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Some Error Has Occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {

                        Toast.makeText(
                            context,
                            "Json Error Has Occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }

                }, Response.ErrorListener {

                    Toast.makeText(
                        context,
                        "Some Error Has Occurred",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }) {

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4f1fadec00b2ac"
                        return headers

                    }

                }

            queue.add(jsonObjectRequest)

        }

        btnProceedToCart.setOnClickListener {

            val gson = Gson()
            val foodItems = gson.toJson(orderList)
            println("Food Items is $foodItems")
            //println("Order is $orderList")
            val async = DBCart(activity as Context , res_id , foodItems , 1 ).execute()
            val result = async.get()
            if(result){

                val data = Bundle()
                data.putString("res_Id" , res_id)
                data.putString("resName", restaurant_name)
                val intent = Intent(activity, CartActivity::class.java)
                intent.putExtra("data" , data)
                startActivity(intent)

            } else {
                Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                    .show()
            }

            }

            return view
        }


    class DBCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant_db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.foodDao().insertFoodItem(FoodItems(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.foodDao().deleteFoodItem(FoodItems(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }

}



