package com.example.balaicoffeedashboard.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.balaicoffeedashboard.databinding.BahanListLayoutBinding
import com.example.balaicoffeedashboard.models.CategoryListData

class CategoryListAdapter(private val categoryList: MutableList<CategoryListData?>?, private val itemClickListener: (CategoryListData) -> Unit): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>(),
    Filterable {
    private lateinit var binding: BahanListLayoutBinding
    private var categoryFiltered = categoryList

    class ViewHolder(view: View, private val binding: BahanListLayoutBinding): RecyclerView.ViewHolder(view) {
        fun bind(data: CategoryListData?, clickListener: (CategoryListData) -> Unit) {
            binding.bahanNameTextView.text = data?.categoryName
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
        holder.bind(categoryFiltered?.get(position), itemClickListener)
    }

    override fun getItemCount(): Int {
        return categoryFiltered?.size ?: 0
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    if (categoryList != null) {
                        categoryFiltered = categoryList
                    }
                } else {
                    val filteredList: MutableList<CategoryListData?>? = mutableListOf()
                    if (categoryList != null) {
                        for (row in categoryList) {
                            if (row?.categoryName?.lowercase()?.contains(charString.toLowerCase()) == true) {
                                filteredList?.add(row)
                            }
                        }
                    }
                    categoryFiltered = filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = categoryFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                categoryFiltered = results?.values as (MutableList<CategoryListData?>?)
                notifyDataSetChanged()
            }
        }
    }
}