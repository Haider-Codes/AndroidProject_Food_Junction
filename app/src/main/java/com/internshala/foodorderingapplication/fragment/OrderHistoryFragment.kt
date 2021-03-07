package com.internshala.foodorderingapplication.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.activity.MainActivity
import com.internshala.foodorderingapplication.adapter.OrderHistoryRecyclerAdapter
import com.internshala.foodorderingapplication.model.RestaurantOrders
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter

    lateinit var relativeContent3 : RelativeLayout
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar : ProgressBar


    var userId = "123"

    var orderHistoryList = arrayListOf<RestaurantOrders>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity as Context)

        relativeContent3 = view.findViewById(R.id.RelativeContent3)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        sharedPreferences = (context as MainActivity).getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )

        userId = sharedPreferences.getString("UserId", "123").toString()


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(Method.GET, url, null , Response.Listener {

                progressLayout.visibility = View.GONE
                progressBar.visibility = View.GONE

                try {

                    println("Response for Order History is $it")
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success){

                        val orders = data.getJSONArray("data")
                        println("Length of Data is ${orders.length()}")
                        for(i in 0 until orders.length()) {

                            println("Response for Data Array is $orders[i]")

                            val orderObject = orders.getJSONObject(i)
                            val orderDetail = RestaurantOrders(

                                orderObject.getString("order_id"),
                                orderObject.getString("restaurant_name"),
                                orderObject.getString("total_cost"),
                                orderObject.getString("order_placed_at"),
                                orderObject.getJSONArray("food_items")

                            )
                            orderHistoryList.add(orderDetail)
                            if(orderHistoryList.isNotEmpty())
                                relativeContent3.visibility = View.GONE

                            recyclerAdapter = OrderHistoryRecyclerAdapter(
                                activity as Context,
                                orderHistoryList )

                            recyclerOrderHistory.adapter = recyclerAdapter
                            recyclerOrderHistory.layoutManager = layoutManager

                        }

                    }
                    else{

                        Toast.makeText(context , "Some Error Has Occurred" , Toast.LENGTH_SHORT).show()

                    }

                }
                catch (e : JSONException){

                    Toast.makeText(context , "JSON ERROR Has Occurred" , Toast.LENGTH_SHORT).show()

                }

            }, Response.ErrorListener {


                Toast.makeText(context , "Some Unexpected Error Has Occurred" , Toast.LENGTH_SHORT).show()

            }) {

                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "4f1fadec00b2ac"
                    return headers

                }


            }

            queue.add(jsonRequest)

        }
        else
        {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setNegativeButton("Exit"){text,listener->

                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.setPositiveButton("Open Settings"){text,listener->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }

            dialog.create()
            dialog.show()

        }

        return view

    }
}