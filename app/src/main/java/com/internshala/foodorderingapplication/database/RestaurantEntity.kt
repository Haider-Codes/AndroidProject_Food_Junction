package com.internshala.foodorderingapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurants" , primaryKeys = arrayOf("userId" , "restaurantId") )

data class RestaurantEntity(


    val userId : String,
    val restaurantId : String,
    @ColumnInfo(name = "restaurant_name") val restaurantName : String,
    @ColumnInfo(name = "restaurant_rating") val restaurantRating : String,
    @ColumnInfo(name = "restaurant_price_per_person") val pricePerPerson : String,
    @ColumnInfo(name = "restaurant_image") val restaurantImage : String


)