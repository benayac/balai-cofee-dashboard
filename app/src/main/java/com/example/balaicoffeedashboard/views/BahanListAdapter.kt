package com.example.balaicoffeedashboard.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.balaicoffeedashboard.databinding.BahanListLayoutBinding
import com.example.balaicoffeedashboard.models.BahanResponseData

class BahanListAdapter(private val bahanList: MutableList<BahanResponseData?>?, private val itemClickListener: (BahanResponseData) -> Unit): RecyclerView.Adapter<BahanListAdapter.ViewHolder>(), Filterable {

    private lateinit var binding: BahanListLayoutBinding
    private var bahanFiltered = bahanList

    class ViewHolder(view: View, private val binding: BahanListLayoutBinding): RecyclerView.ViewHolder(view) {
        fun bind(data: BahanResponseData?, clickListener: (BahanResponseData) -> Unit) {
            binding.bahanNameTextView.text = data?.bahanName
            binding.constraintLayout.setOnClickListener {
                if(data != null) {
                    clickListener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = BahanListLayoutBinding.inflate(layoutInflater)
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bahanFiltered?.get(position), itemClickListener)
    }

    override fun getItemCount(): Int {
        return bahanFiltered?.size ?: 0
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    if (bahanList != null) {
                        bahanFiltered = bahanList
                    }
                } else {
                    val filteredList: MutableList<BahanResponseData?>? = mutableListOf()
                    if (bahanList != null) {
                        for (row in bahanList) {
                            if (row?.bahanName?.lowercase()?.contains(charString.toLowerCase()) == true) {
                                filteredList?.add(row)
                            }
                        }
                    }
                    bahanFiltered = filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = bahanFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                bahanFiltered = results?.values as (MutableList<BahanResponseData?>?)
                notifyDataSetChanged()
            }
        }
    }
}