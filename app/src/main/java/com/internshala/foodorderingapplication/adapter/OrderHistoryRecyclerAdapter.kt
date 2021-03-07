package com.internshala.foodorderingapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.model.RestaurantItems
import com.internshala.foodorderingapplication.model.RestaurantOrders

class OrderHistoryRecyclerAdapter(val context : Context, val orderHistoryList: List<RestaurantOrders>) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>()   {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtNameOfRestaurant : TextView = view.findViewById(R.id.txtNameOfRestaurant)
        val txtOrderDate : TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerOrderFoodHistory : RecyclerView = view.findViewById(R.id.recyclerOrderFoodHistory)
        lateinit var recyclerAdapter : CartRecyclerAdapter

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row , parent , false)
        return OrderHistoryViewHolder(view)

    }

    override fun getItemCount(): Int {

        return orderHistoryList.size

    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {

        val order = orderHistoryList[position]
        holder.txtNameOfRestaurant.text = order.restaurantName
        holder.txtOrderDate.text = order.orderPlacedAt
        setUpRecycler(holder.recyclerOrderFoodHistory , order)

     }


    fun setUpRecycler(recyclerFood : RecyclerView ,  orderFoodList : RestaurantOrders ){

        val food = ArrayList<RestaurantItems>()

            for(i in 0 until orderFoodList.foodOrder.length()){


                val foodObject = orderFoodList.foodOrder.getJSONObject(i)
                val foodItems = RestaurantItems(

                    foodObject.getString("food_item_id"),
                    foodObject.getString("name"),
                    foodObject.getString("cost")

                )
                food.add(foodItems)

            }

        val recyclerAdapter = CartRecyclerAdapter(context , food)
        val layoutManager = LinearLayoutManager(context)
        recyclerFood.adapter = recyclerAdapter
        recyclerFood.layoutManager = layoutManager

    }


}