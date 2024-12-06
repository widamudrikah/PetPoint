package com.capstone.petpoint.ui.myreport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.petpoint.databinding.ItemReportUserBinding
import com.capstone.petpoint.response.DataItem

class MyReportAdapter(private val onItemClick: (DataItem) -> Unit) : ListAdapter<DataItem, MyReportAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReportAdapter.MyViewHolder {
        val binding = ItemReportUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val report = getItem(position)
        holder.bind(report)
    }

    class MyViewHolder(private val binding: ItemReportUserBinding, private val onItemClick: (DataItem) -> Unit): RecyclerView.ViewHolder(binding.root) {
        fun bind(report: DataItem) {
            binding.tvReportDate.text = report.dateCreated
            binding.tvReportHour.text = report.timeCreated
            binding.tvPetCategoryData.text = report.petCategory
            binding.tvStatusData.text = report.petStatus
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.emId == newItem.emId
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}