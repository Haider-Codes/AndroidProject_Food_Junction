package com.internshala.foodorderingapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {

    @Insert
    fun insertFoodItem(foodItems: FoodItems)

    @Delete
    fun deleteFoodItem(foodItems: FoodItems)

    @Query("SELECT * FROM Food_Items WHERE restaurant_id = :restaurantId")
    fun getAllOrders(restaurantId : String) : List<FoodItems>

    @Query("DELETE FROM FOOD_ITEMS WHERE restaurant_id = :restaurantId")
    fun deleteOrders(restaurantId: String)

}