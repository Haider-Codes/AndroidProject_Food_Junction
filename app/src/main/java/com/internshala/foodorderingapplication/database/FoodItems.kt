package com.internshala.foodorderingapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Food_Items")
data class FoodItems(

    val restaurant_id : String,
    @PrimaryKey val food: String

)