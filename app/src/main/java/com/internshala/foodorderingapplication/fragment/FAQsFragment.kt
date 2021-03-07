package com.internshala.foodorderingapplication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.adapter.FaqRecyclerAdapter
import com.internshala.foodorderingapplication.model.Faqs

class FAQsFragment : Fragment() {

    lateinit var recyclerFaqQues: RecyclerView
    lateinit var recyclerAdapter: FaqRecyclerAdapter
    lateinit var layoutManager : RecyclerView.LayoutManager

    val quesList = arrayListOf<String>("How can I create an account in Food Junction App?",
                                                   "I don't remember my password?",
                                                   "What are your delivery hours?",
                                                    "How much time will it take to deliver the order",
                                                    "How to view my past orders?",
                                                    "How to view restaurant menu?")

    val ansList = arrayListOf<String>(" Click on 'Don't have an account? Sign up now' at the bottom of the page. Then fill out your information and click the 'Register' button.",
                                                  "Click on 'Forgot Password?'. Fill out your phone number and registered email and click on 'Next' and then in the next page enter the Otp and reset the password.",
                                                   "Our delivery hour is from 10:00 AM to 08:00 PM.",
                                                   " Generally it takes between 45 minutes to 1 hour time to deliver the order. Due to long distance or heavy traffic, delivery might take few extra minutes.",
                                                    "You can view your past orders in 'Orders History'.",
                                                     "You can view the menu of a specific restaurant by clicking on the 'Restaurant Name'. ")

    val faqList = arrayListOf<Faqs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_f_a_qs, container, false)

       recyclerFaqQues = view.findViewById(R.id.recyclerFaqQues)
        layoutManager = LinearLayoutManager(activity as Context)

        for(i in 0 until quesList.size)
        {


           val faqObject = Faqs(

            quesList[i],
               ansList[i]

           )

            faqList.add(faqObject)

            println("FAQ LIST $i is $faqList")

            recyclerAdapter = FaqRecyclerAdapter(activity as Context , faqList)

            recyclerFaqQues.adapter = recyclerAdapter
            recyclerFaqQues.layoutManager = layoutManager

        }

        return view

    }

    }
