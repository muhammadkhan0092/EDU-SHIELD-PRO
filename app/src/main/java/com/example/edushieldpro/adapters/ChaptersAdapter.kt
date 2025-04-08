package com.example.edushieldpro.adapters

import android.graphics.Color
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
import com.example.edushieldpro.databinding.RvAllCoursesItemBinding
import com.example.edushieldpro.databinding.RvChaptersBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.VideoData
import kotlin.math.roundToInt

class ChaptersAdapter : RecyclerView.Adapter<ChaptersAdapter.AddressViewHolder>(){

    inner class AddressViewHolder(val binding : RvChaptersBinding) : ViewHolder(binding.root){
        }
    var type:Int = 0
    private val diffUtil =object : DiffUtil.ItemCallback<VideoData>(){
        override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressViewHolder {
        return AddressViewHolder(
            RvChaptersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = differ.currentList[position]
        var percentage : Float = 0.0f
        holder.itemView.post {
            holder.binding.apply {
            if (type == 2) {
                holder.binding.cvv.visibility = View.INVISIBLE
                holder.binding.ivPlay.visibility = View.INVISIBLE
                holder.binding.vi.visibility = View.INVISIBLE
                holder.binding.tvCompleted.visibility = View.INVISIBLE
            }
            tvTitle.text = item.title
            tvCompleted.text = "50%"
            tvTime.text = "${item.timeData.minutes} : ${item.timeData.seconds} mins"
            Glide.with(holder.itemView).load(item.videoThumbnail).into(ivThumbnail)
            val totalSeconds = (item.timeData.seconds) + (item.timeData.minutes * 60)
            val watchedSeconds = (item.watchedData.seconds) + (item.watchedData.minutes * 60)
                percentage = (watchedSeconds.toFloat() / totalSeconds.toFloat()) * 100
            val a = percentage.roundToInt()
            Log.d("khan", "$watchedSeconds / $totalSeconds = $percentage")
            tvCompleted.text = a.toString() + " %"
        }
            val width = holder.binding.vi.width
            val layoutParams = holder.binding.vi.layoutParams
              Log.d("khan","on ${width}")
             layoutParams.width = (width * (percentage/100)).toInt() + 2
            Log.d("khan","percentage $percentage = ${width * (percentage/100)}")
              holder.binding.vi.layoutParams = layoutParams
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((VideoData) -> Unit)? = null


}