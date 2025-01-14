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
import com.example.edushieldpro.databinding.RvInstructorCoursesBinding
import com.example.edushieldpro.models.Course

class InstructorCoursesAdapter : RecyclerView.Adapter<InstructorCoursesAdapter.InstructorCoursesAdapterViewHolder>(){

    inner class InstructorCoursesAdapterViewHolder(val binding : RvInstructorCoursesBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<Course>(){
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.courseId == newItem.courseId
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstructorCoursesAdapterViewHolder {
        return InstructorCoursesAdapterViewHolder(
            RvInstructorCoursesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: InstructorCoursesAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(item.image).into(imageView6)
            tvCategory.text = item.category
            tvTitle.text = item.title
            tvRating.text = item.rating.toString()
            tvNewPrice.text = item.price
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    var onClick : ((Course) -> Unit)? = null




}