package com.internshala.foodorderingapplication.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.database.FoodItems
import com.internshala.foodorderingapplication.model.RestaurantItems


class RestaurantMenuRecyclerAdapter(val context: Context, val restaurantItemList : ArrayList<RestaurantItems> , val listener: OnItemClickListener ) : RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantDetailsViewHolder>() {

    class RestaurantDetailsViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtCounter : TextView = view.findViewById(R.id.txtCounter)
        val txtNameOfItem : TextView = view.findViewById(R.id.txtNameOfItem)
        val txtPricePerItem : TextView = view.findViewById(R.id.txtPricePerItem)
        val btnAddToCart : Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoteFromCart : Button = view.findViewById(R.id.btnRemoveFromCart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row,parent,false)
        return RestaurantDetailsViewHolder(view)

    }

    override fun getItemCount(): Int {

        return restaurantItemList.size

    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: RestaurantItems)
        fun onRemoveItemClick(foodItem: RestaurantItems)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {

        val itemList = restaurantItemList[position]

        holder.txtCounter.text = (position+1).toString()
        holder.txtNameOfItem.text = itemList.itemName
        holder.txtPricePerItem.text = itemList.itemPricePerItem

        holder.btnAddToCart.setOnClickListener {



            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoteFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(itemList)

        }

        holder.btnRemoteFromCart.setOnClickListener {

            holder.btnRemoteFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(itemList)

        }

        }

}