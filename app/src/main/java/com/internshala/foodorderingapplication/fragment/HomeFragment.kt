package com.internshala.foodorderingapplication.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.activity.MainActivity
import com.internshala.foodorderingapplication.adapter.HomeRecyclerAdapter
import com.internshala.foodorderingapplication.model.Restaurants
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    var restaurantNameList = arrayListOf<Restaurants>()

    var ratingComparator = Comparator<Restaurants> { restaurant1, restaurant2 ->

        if (restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true) == 0)
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        else
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
    }

    var costComparator = Comparator<Restaurants>{restaurant1 , restaurant2 ->

        if(restaurant1.pricePerPerson.compareTo(restaurant2.pricePerPerson , true) ==0)
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        else
            restaurant1.pricePerPerson.compareTo(restaurant2.pricePerPerson , true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        sharedPreferences = (activity as Context).getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )

        val userId = sharedPreferences.getString("UserId", "1").toString()
        println("User Id is $userId")

        setHasOptionsMenu(true)

        (context as MainActivity).toolbar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        if (ConnectionManager().checkConnectivity(activity as Context)) {


            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {
                        println("Response for Restaurants is $it")

                        progressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE

                        val data1 = it.getJSONObject("data")

                        val success = data1.getBoolean("success")
                        if (success) {

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val restaurantJSONObject = data.getJSONObject(i)

                                val restaurantObject = Restaurants(

                                    userId,
                                    restaurantJSONObject.getString("id"),
                                    restaurantJSONObject.getString("name"),
                                    restaurantJSONObject.getString("rating"),
                                    restaurantJSONObject.getString("cost_for_one"),
                                    restaurantJSONObject.getString("image_url")

                                )

                                restaurantNameList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantNameList)

                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(context, "Some Error Has Occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: JSONException) {

                        Toast.makeText(context, "JSON Error Has Occurred", Toast.LENGTH_SHORT)
                            .show()

                    }
                }, Response.ErrorListener {

                    Toast.makeText(context, "Some Error Has Occurred", Toast.LENGTH_SHORT).show()

                }) {

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4f1fadec00b2ac"
                        return headers

                    }


                }

            queue.add(jsonObjectRequest)
        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setNegativeButton("Exit") { text, listener ->

                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.setPositiveButton("Open Settings") { text, listener ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }

            dialog.create()
            dialog.show()

        }

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_home, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id == R.id.action_sort) {

            val alertDialog = AlertDialog.Builder(activity as Context)
            alertDialog.setTitle("Sort By?")
            val items = arrayOf<String>("Cost(Low to High)", "Cost(High to Low)", "Ratings")
            var selectedItem = "Cost(Low to High)"
            alertDialog.setSingleChoiceItems(
                items,
                -1,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    
                    selectedItem = items[i]
                    
                })
            
            alertDialog.setPositiveButton("Ok" , DialogInterface.OnClickListener { dialogInterface, i ->

                when(selectedItem){

                    "Ratings"->{

                        Collections.sort(restaurantNameList , ratingComparator)
                        restaurantNameList.reverse()

                        recyclerAdapter.notifyDataSetChanged()

                              }

                    "Cost(High to Low)"->{

                        Collections.sort(restaurantNameList , costComparator)
                        restaurantNameList.reverse()

                        recyclerAdapter.notifyDataSetChanged()

                                         }

                    "Cost(Low to High)"->{

                        Collections.sort(restaurantNameList , costComparator)

                        recyclerAdapter.notifyDataSetChanged()

                                         }

                    else ->{Toast.makeText(context , "Some Error has Occurred" , Toast.LENGTH_SHORT).show()}

                }

            })
            alertDialog.setNegativeButton("Cancel" , DialogInterface.OnClickListener { dialogInterface, i ->  })

            alertDialog.create()
            alertDialog.show()

        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)

    }

}