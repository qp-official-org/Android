package com.example.qp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemHashtagBinding

class WriteQuestionTagRVAdapter(context: Context):RecyclerView.Adapter<WriteQuestionTagRVAdapter.ViewHolder>() {
    private  var items=ArrayList<String>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteQuestionTagRVAdapter.ViewHolder {
        val binding=ItemHashtagBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WriteQuestionTagRVAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =items.size

    inner class ViewHolder(val binding:ItemHashtagBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position:Int){
            binding.hashtagText.text=items[position]

            binding.hashtagRemoveBtn.setOnClickListener {
                removeItem(position)
            }
        }
    }

    fun addItem(str:String){
        items.add(str)
        notifyDataSetChanged()
    }
    fun removeItem(position:Int){
        items.removeAt(position)
        notifyDataSetChanged()
    }

    fun dupCheck(s:String):Boolean{
        return !items.contains(s)
    }

    fun getItems():ArrayList<String>{
        return items
    }



}

