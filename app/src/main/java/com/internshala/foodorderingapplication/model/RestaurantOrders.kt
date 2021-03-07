package com.internshala.foodorderingapplication.model

import org.json.JSONArray

data class RestaurantOrders(

    val orderId : String,
    val restaurantName : String,
    val totalCost : String,
    val orderPlacedAt : String,
    val foodOrder : JSONArray

)