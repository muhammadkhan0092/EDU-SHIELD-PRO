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
import com.example.edushieldpro.databinding.RvTopTeachersBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataTopTeachers
import com.example.edushieldpro.models.VideoData

class TopTeachersAdapter : RecyclerView.Adapter<TopTeachersAdapter.TopTeachersViewHolder>(){

    inner class TopTeachersViewHolder(val binding : RvTopTeachersBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<DataTopTeachers>(){
        override fun areItemsTheSame(oldItem: DataTopTeachers, newItem: DataTopTeachers): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(oldItem: DataTopTeachers, newItem: DataTopTeachers): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopTeachersViewHolder {
        return TopTeachersViewHolder(
            RvTopTeachersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TopTeachersViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvName.text = item.name
            tvCourses.text = "Courses" + item.coursesSold
            if(!item.image.isNullOrBlank()){
                Glide.with(holder.itemView).load(item.image).into(imageEdit)
            }
            else
            {
                Glide.with(holder.itemView).load(R.drawable.testing).into(imageEdit)
            }
        }


    }


    var onClick : ((Course) -> Unit)? = null



}