package com.internshala.foodorderingapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.activity.LoginActivity
import com.internshala.foodorderingapplication.activity.MainActivity

class MyProfileFragment : Fragment() {

    lateinit var txtProfileName : TextView
    lateinit var txtProfilePhoneNo : TextView
    lateinit var txtProfileEmail : TextView
    lateinit var txtProfileAddress : TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences = (activity as Context).getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfilePhoneNo = view.findViewById(R.id.txtProfilePhoneNo)
        txtProfileEmail = view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress = view.findViewById(R.id.txtProfileAddress)

        txtProfileName.text = sharedPreferences.getString("UserName" , "John Doe")
        txtProfilePhoneNo.text = sharedPreferences.getString("UserMobileNumber" , "+91-1115555555")
        txtProfileEmail.text = sharedPreferences.getString("UserEmail" , "john_doe@gmail.com")
        txtProfileAddress.text = sharedPreferences.getString("UserAddress" , "Gurugram")

        return view

    }

 }