package com.example.androidfi.forRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfi.R

class CustomRecyclerAdapterForExams(private val models: List<String>,
                                    private val numbers: List<Int>):
    RecyclerView.Adapter<CustomRecyclerAdapterForExams.MyViewHolder>()
{
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val layoutItem: ConstraintLayout = itemView.findViewById(R.id.layoutItem)
        val textViewNum: TextView = itemView.findViewById(R.id.textViewNumItem)
        val textViewModel: TextView = itemView.findViewById(R.id.textViewModelItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.textViewModel.text = models[position]
        holder.textViewNum.text = numbers[position].toString()
    }

    override fun getItemCount() = models.size
}