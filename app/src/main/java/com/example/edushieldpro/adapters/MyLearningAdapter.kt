package com.example.edushieldpro.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.RvMyLearningItemBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.models.VideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min


class MyLearningAdapter : RecyclerView.Adapter<MyLearningAdapter.MyLearningViewHolder>(){

    inner class MyLearningViewHolder(val binding : RvMyLearningItemBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<Course>(){
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.courseId==newItem.courseId
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyLearningViewHolder {
        val view = RvMyLearningItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemView = view.flexibleView

        val layoutParams = itemView.layoutParams
        itemView.layoutParams = layoutParams
        return MyLearningViewHolder(view)
    }



    override fun onBindViewHolder(holder: MyLearningViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.itemView.post {
            val www = holder.binding.flexibleView.width
            val layoutParams = holder.binding.flexibleView.layoutParams
          //  Log.d("khan","on ${www}")
           // layoutParams.width = www / item.sold
          //  holder.binding.flexibleView.layoutParams = layoutParams
            Glide.with(holder.itemView).load(item.image).into(holder.binding.imageView6)
            holder.binding.tvTitle.text = item.title
            holder.binding.tvCategory.text = item.category
            val timeData : TimeData = calculateDuration(item.videos)
            holder.binding.textView16.text = "${timeData.minutes} Mins ${timeData.seconds} Seconds"
            holder.itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }

    private fun calculateDuration(videos: MutableList<VideoData>): TimeData {
        var mins = 0
        var seconds = 0
        videos.forEach {
            mins = mins + it.timeData.minutes
            seconds = seconds + it.timeData.seconds
        }
        return TimeData(mins,seconds)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((Course) -> Unit)? = null

}