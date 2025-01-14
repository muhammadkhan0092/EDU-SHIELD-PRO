package com.example.edushieldpro.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.RvAllCoursesItemBinding
import com.example.edushieldpro.models.Course

class AllCoursesAdapter : RecyclerView.Adapter<AllCoursesAdapter.AddressViewHolder>(){

    inner class AddressViewHolder(val binding : RvAllCoursesItemBinding) : ViewHolder(binding.root){
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
    ): AddressViewHolder {
        return AddressViewHolder(
            RvAllCoursesItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            imageView6.setImageResource(R.drawable.testing)
            tvCategory.text = item.category
            tvTitle.text = item.title
            tvRating.text = item.rating.toString()
        }
    }




}