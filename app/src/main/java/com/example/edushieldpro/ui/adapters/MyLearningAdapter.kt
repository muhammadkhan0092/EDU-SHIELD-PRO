package com.example.edushieldpro.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.RvMyLearningItemBinding
import com.example.edushieldpro.ui.models.Course
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            Log.d("khan","on ${www}")
            val layoutParams = holder.binding.flexibleView.layoutParams
            layoutParams.width = www / item.sold
            holder.binding.flexibleView.layoutParams = layoutParams
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}