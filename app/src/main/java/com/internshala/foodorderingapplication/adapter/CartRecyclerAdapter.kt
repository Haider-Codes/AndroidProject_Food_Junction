package com.internshala.foodorderingapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.database.FoodItems
import com.internshala.foodorderingapplication.database.RestaurantEntity
import com.internshala.foodorderingapplication.model.RestaurantItems

class CartRecyclerAdapter(val context : Context, val orderList: List<RestaurantItems>)  : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtNameOfOrder : TextView = view.findViewById(R.id.txtNameOfOrder)
        val txtPriceOfOrder : TextView = view.findViewById(R.id.txtPriceOfOrder)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row , parent , false)
        return CartViewHolder(view)


    }

    override fun getItemCount(): Int {

        return orderList.size

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val order = orderList[position]
        holder.txtNameOfOrder.text = order.itemName
        holder.txtPriceOfOrder.text = order.itemPricePerItem

    }


}