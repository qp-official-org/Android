package com.example.qp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qp.databinding.ItemHashtagBinding

class WriteQuestionTagRVAdapter(val activity: AppCompatActivity, val text: ArrayList<String>?):RecyclerView.Adapter<WriteQuestionTagRVAdapter.ViewHolder>() {
    private  var items=ArrayList<String>()
    init {
        if (text != null) {
            items.addAll(text)
        }
    }

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
                if(activity is SearchActivity){
                    AppData.searchRecord.removeAt(position)
                }
            }
            binding.hashtagText.setOnClickListener{
                if(activity is SearchActivity){
                    activity.textListner.onQueryTextSubmit(items[position])
                }
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

