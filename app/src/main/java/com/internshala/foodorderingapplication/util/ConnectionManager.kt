package com.internshala.foodorderingapplication.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat.getSystemService

class ConnectionManager {

    fun checkConnectivity(context : Context) : Boolean{

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetworkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo

        if(activeNetworkInfo?.isConnected != null)
            return activeNetworkInfo.isConnected
        else
            return false

    }

}