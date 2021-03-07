package com.internshala.foodorderingapplication.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.activity.MainActivity
import com.internshala.foodorderingapplication.database.RestaurantDatabase
import com.internshala.foodorderingapplication.database.RestaurantEntity
import com.internshala.foodorderingapplication.fragment.RestaurantMenuFragment
import com.internshala.foodorderingapplication.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context , val restaurantsList : List<Restaurants>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val linearLayoutContent : LinearLayout = view.findViewById(R.id.linearLayoutContent)
        val imgRestaurant : ImageView = view.findViewById(R.id.imgRestaurant)
        val txtNameOfRestaurant : TextView = view.findViewById(R.id.txtNameOfRestaurant)
        val txtPricePerPerson : TextView = view.findViewById(R.id.txtPricePerPerson)
        val imgAddToFavourites : ImageView = view.findViewById(R.id.imgAddToFavourite)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtRestaurantRating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)

    }

    override fun getItemCount(): Int {

        return restaurantsList.size

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

    val restaurant = restaurantsList[position]
        holder.txtNameOfRestaurant.text = restaurant.restaurantName
        holder.txtPricePerPerson.text = restaurant.pricePerPerson
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurant_default).into(holder.imgRestaurant)


        val restaurantEntity = RestaurantEntity(

            restaurant.userId,
            restaurant.restaurantId,
            holder.txtNameOfRestaurant.text.toString(),
            holder.txtRestaurantRating.text.toString(),
            holder.txtPricePerPerson.text.toString(),
            restaurant.restaurantImage

        )

        val checkFav = DBAsyncTask(context, restaurantEntity , 1).execute()
        val ifFav=checkFav.get()
        if(ifFav)
            holder.imgAddToFavourites.setImageResource(R.drawable.ic_favourite1_asset)
        else
            holder.imgAddToFavourites.setImageResource(R.drawable.ic_favourite_asset)

            holder.imgAddToFavourites.setOnClickListener {

                if(!DBAsyncTask(context , restaurantEntity , 1).execute().get()){
                    //This block will get Execute when the Restaurant is not a Favourite
                    val async = DBAsyncTask(context , restaurantEntity ,2).execute()
                    val result = async.get()

                    if(result){
                        Toast.makeText(context ,"Restaurant Added To Favourite",Toast.LENGTH_SHORT).show()
                        holder.imgAddToFavourites.setImageResource(R.drawable.ic_favourite1_asset)
                    }
                    else{
                        Toast.makeText(context, "Some Error Has Occurred" , Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    //This block will be executed when the Restaurant is a Favourite
                    val async = DBAsyncTask(context , restaurantEntity ,3).execute()
                    val result = async.get()

                    if(result){
                        Toast.makeText(context,"Restaurant Removed From Favourite",Toast.LENGTH_SHORT).show()
                        holder.imgAddToFavourites.setImageResource(R.drawable.ic_favourite_asset)
                    }
                    else{
                        Toast.makeText(context ,"Some Error Has Occurred",Toast.LENGTH_SHORT).show()
                    }

                }

            }

        holder.linearLayoutContent.setOnClickListener {

            val bundle = Bundle()
            val fragment = RestaurantMenuFragment()
            bundle.putString("restaurant_id" , restaurant.restaurantId)
            bundle.putString("restaurant_name" , restaurant.restaurantName)
            fragment.arguments = bundle
            println("Id is ${restaurant.restaurantId}")

            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.frameLayout , fragment).commit()

            (context as AppCompatActivity).supportActionBar?.title = restaurant.restaurantName

        }

    }

    class DBAsyncTask( val context: Context , val restaurantEntity: RestaurantEntity , val mode : Int) : AsyncTask<Void , Void , Boolean>(){

        /*
       Mode 1 ---> Check DB if the Restaurant is favourite or not

       Mode 2---> Save the Restaurant into DB into favourites

       Mode 3---> Remove the favourite Restaurant
        */

        val db = Room.databaseBuilder(context , RestaurantDatabase::class.java,"restaurant_db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode)
            {
                
                1->  {

                    // Check DB if the Restaurant is favourite or not

                    val restaurant : RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.restaurantId , restaurantEntity.userId)
                    db.close()

                    return restaurant!=null

                     }

                2->{

                    //Save the Restaurant into DB into favourites

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                   }

                3->{

                    //Remove the favourite Restaurant

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                   }
                
            }

          return false
        }

    }

}