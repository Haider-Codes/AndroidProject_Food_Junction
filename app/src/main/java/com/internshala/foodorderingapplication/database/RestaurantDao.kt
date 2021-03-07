package com.internshala.foodorderingapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM Restaurants")
    fun getAllRestaurants() : List<RestaurantEntity>

    @Query("SELECT * FROM Restaurants WHERE restaurantId = :restaurantId and userid = :userId")
    fun getRestaurantById(restaurantId : String , userId: String) : RestaurantEntity

    @Query("SELECT * FROM Restaurants WHERE userid = :userId")
    fun getRestaurantByUserId(userId : String) : List<RestaurantEntity>

}