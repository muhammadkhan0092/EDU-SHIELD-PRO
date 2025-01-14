package com.example.edushieldpro.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.RvAllCoursesItemBinding
import com.example.edushieldpro.databinding.RvChaptersBinding
import com.example.edushieldpro.models.Course

class ChaptersAdapter : RecyclerView.Adapter<ChaptersAdapter.AddressViewHolder>(){

    inner class AddressViewHolder(val binding : RvChaptersBinding) : ViewHolder(binding.root){
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
    ): AddressViewHolder {
        return AddressViewHolder(
            RvChaptersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvTitle.text = "3D animations"
            tvCompleted.text = "50%"
            tvTime.text = "10:50 mins"
            vi.setBackgroundColor(Color.parseColor("#423423"))
            ivThumbnail.setBackgroundColor(R.drawable.testing)
            //)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}