package com.example.edushieldpro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.edushieldpro.databinding.RvVideoBinding
import com.example.edushieldpro.models.VideoData

class VideosAdapter : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>(){

    inner class VideoViewHolder(val binding : RvVideoBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<VideoData>(){
        override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        return VideoViewHolder(
            RvVideoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateList(newList: List<VideoData>) {
        differ.submitList(newList.toList()) // Ensure a new list instance is provided
    }






    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
           Glide.with(holder.itemView).load(item.videoThumbnail).into(imageView6)
            tvCategory.text = "pleaj"
        }

        holder.binding.imageView17.setOnClickListener {
            onDel?.invoke(position,item)
        }
    }



    var onDel : ((Int,VideoData) -> Unit)? = null
}