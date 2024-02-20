package com.example.qp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.qp.databinding.ItemQuestionBinding

class QuestionRVAdapter(private val qList: ArrayList<QuestionInfo>)
    : RecyclerView.Adapter<QuestionRVAdapter.ViewHolder>() {

    interface MyItemClickListner{
        fun onItemClick(questionInfo: QuestionInfo)
    }
    private lateinit var myItemClickListner: MyItemClickListner
    fun setMyItemClickListner(itemClickListner: MyItemClickListner){
        myItemClickListner = itemClickListner
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemQuestionBinding =ItemQuestionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, viewGroup.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(qList[position])
        holder.itemView.setOnClickListener{
            myItemClickListner.onItemClick(qList[position])
        }
    }

    override fun getItemCount(): Int = qList.size

    inner class ViewHolder(val binding: ItemQuestionBinding, val con: Context) : RecyclerView.ViewHolder(binding.root){
        fun bind(questionInfo: QuestionInfo){
            binding.itemTimeTv.text = getTime(questionInfo.createdAt.toString())
            binding.itemQuestionTv.text = questionInfo.title
            binding.answerCommentTv.text = questionInfo.answerCount.toString()
            binding.answerExpertTv.text = questionInfo.expertCount.toString()
            if(questionInfo.user!!.profileImage!=""){
                setStringImage(questionInfo.user!!.profileImage, binding.itemUserIv, con)
            }
            binding.itemChildTv.isVisible = questionInfo.childStatus.equals("ACTIVE")
            val tagList=questionInfo.hashtags
            if(tagList?.size==1){
                binding.itemCategory1Tv.text = "#"+tagList[0].hashtag
                binding.itemCategory2Tv.text = ""
                binding.itemCategory3Tv.text = ""
            }
            else if(tagList?.size==2){
                binding.itemCategory1Tv.text = "#"+tagList[0].hashtag
                binding.itemCategory2Tv.text = "#"+tagList[1].hashtag
                binding.itemCategory3Tv.text = ""
            }
            else if(tagList?.size==3){
                binding.itemCategory1Tv.text = "#"+tagList[0].hashtag
                binding.itemCategory2Tv.text = "#"+tagList[1].hashtag
                binding.itemCategory3Tv.text = "#"+tagList[2].hashtag
            }
            else{ //해시태그 X
                binding.itemCategory1Tv.text = ""
                binding.itemCategory2Tv.text = ""
                binding.itemCategory3Tv.text = ""
            }
        }
    }

    // 이미지뷰에 문자열 형태의 이미지를 설정
    fun setStringImage(imageUrl: String, imageView: ImageView, con: Context) {
        Glide.with(con)
            .load(imageUrl)
            .apply(RequestOptions().transform(CircleCrop()))
            .into(imageView)
    }

}