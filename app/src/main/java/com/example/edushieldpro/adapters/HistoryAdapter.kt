package com.example.edushieldpro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.RvAllCoursesItemBinding
import com.example.edushieldpro.databinding.RvHistoryBinding
import com.example.edushieldpro.databinding.RvTopTeachersBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPurchaseHistory
import com.example.edushieldpro.models.DataTopTeachers
import com.example.edushieldpro.models.VideoData

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryAdapter>(){

    inner class HistoryAdapter(val binding : RvHistoryBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<DataPurchaseHistory>(){
        override fun areItemsTheSame(oldItem: DataPurchaseHistory, newItem: DataPurchaseHistory): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(oldItem: DataPurchaseHistory, newItem: DataPurchaseHistory): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter {
        return HistoryAdapter(
            RvHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HistoryAdapter, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvName.text = item.title
            tvCourses.text = "Student : " + item.studentEmail
            tvDate.text = item.date
            tvTime.text = item.time
            if(!item.image.isNullOrBlank()){
                Glide.with(holder.itemView).load(item.image).into(imageEdit)
            }
            else
            {
                Glide.with(holder.itemView).load(R.drawable.testing).into(imageEdit)
            }
        }


    }





}