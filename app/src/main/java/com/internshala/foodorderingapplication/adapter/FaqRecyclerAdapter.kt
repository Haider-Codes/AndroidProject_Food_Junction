package com.internshala.foodorderingapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.model.Faqs

class FaqRecyclerAdapter(val context: Context, val quesAnsList : List<Faqs>) : RecyclerView.Adapter<FaqRecyclerAdapter.FaqQuesViewHolder>() {

    class FaqQuesViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtQuesCounter : TextView = view.findViewById(R.id.txtQuesCounter)
        val txtQues : TextView = view.findViewById(R.id.txtQues)

        val txtAnsCounter : TextView = view.findViewById(R.id.txtAnsCounter)
        val txtAns : TextView = view.findViewById(R.id.txtAns)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqQuesViewHolder{

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faq_single_row , parent , false)
        return FaqQuesViewHolder(view)


    }

    override fun getItemCount(): Int {

        return quesAnsList.size

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FaqQuesViewHolder, position: Int) {

        val ques = quesAnsList[position]
        holder.txtQuesCounter.text = "Q.${(position+1)}"
        holder.txtQues.text = ques.question
        holder.txtAnsCounter.text = "A.${(position+1)}"
        holder.txtAns.text = ques.answer

        println("Ques is $ques")
        println("QuesList is $quesAnsList")

    }

}