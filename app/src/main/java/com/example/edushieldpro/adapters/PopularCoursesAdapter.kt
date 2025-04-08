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
import com.example.edushieldpro.databinding.RvPopularCoursesBinding
import com.example.edushieldpro.databinding.RvTopTeachersBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPopularCourses
import com.example.edushieldpro.models.DataTopTeachers
import com.example.edushieldpro.models.VideoData

class PopularCoursesAdapter : RecyclerView.Adapter<PopularCoursesAdapter.PopularCoursesViewHolder>(){

    inner class PopularCoursesViewHolder(val binding : RvPopularCoursesBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<DataPopularCourses>(){
        override fun areItemsTheSame(oldItem: DataPopularCourses, newItem: DataPopularCourses): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(oldItem: DataPopularCourses, newItem: DataPopularCourses): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularCoursesViewHolder {
        return PopularCoursesViewHolder(
            RvPopularCoursesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PopularCoursesViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvName.text = item.title
            tvCourses.text = "Students : " + item.students
            if(!item.image.isNullOrBlank()){
                Glide.with(holder.itemView).load(item.image).into(imageEdit)
            }
            else
            {
                Glide.with(holder.itemView).load(R.drawable.testing).into(imageEdit)
            }
            holder.itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }


    }


    var onClick : ((DataPopularCourses) -> Unit)? = null



}