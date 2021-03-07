package com.internshala.foodorderingapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class , FoodItems::class] , version = 1)
abstract  class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao

    abstract fun foodDao() : FoodDao

}