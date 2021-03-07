package com.internshala.foodorderingapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.adapter.HomeRecyclerAdapter
import com.internshala.foodorderingapplication.database.RestaurantDatabase
import com.internshala.foodorderingapplication.database.RestaurantEntity
import com.internshala.foodorderingapplication.model.Restaurants

lateinit var sharedPreferences: SharedPreferences
    var user_id : String = "1"

class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavouriteRestaurants : RecyclerView
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar
    lateinit var relativeContent2 : RelativeLayout
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter  : HomeRecyclerAdapter


    var dbFavouriteRestaurantList = listOf<RestaurantEntity>()
    var favouritesRestaurants = arrayListOf<Restaurants>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        sharedPreferences = (activity as Context).getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)
        user_id = sharedPreferences.getString("UserId" , "1").toString()

        recyclerFavouriteRestaurants = view.findViewById(R.id.recyclerFavouriteRestaurant)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        relativeContent2 = view.findViewById(R.id.RelativeContent2)

        layoutManager = LinearLayoutManager(activity as Context)

        dbFavouriteRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        if(dbFavouriteRestaurantList.isNotEmpty())
            relativeContent2.visibility=View.GONE

        for(i in dbFavouriteRestaurantList){

            val fav = Restaurants(

                    i.userId,
                i.restaurantId,
                i.restaurantName,
                i.restaurantRating,
                i.pricePerPerson,
                i.restaurantImage

            )

            favouritesRestaurants.add(fav)


        }

        if(activity != null){
            progressLayout.visibility=View.GONE
            recyclerAdapter= HomeRecyclerAdapter(activity as Context , favouritesRestaurants)
            recyclerFavouriteRestaurants.adapter=recyclerAdapter
            recyclerFavouriteRestaurants.layoutManager=layoutManager
        }

        return view

    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void , Void , List<RestaurantEntity>>(){

        override fun doInBackground(vararg p0: Void?):  List<RestaurantEntity>{

            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant_db").build()
            return db.restaurantDao().getRestaurantByUserId(user_id)

        }


    }

    }