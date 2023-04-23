package com.example.balaicoffeedashboard.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.balaicoffeedashboard.R
import com.example.balaicoffeedashboard.accordion_library.AccordionAdapter
import com.example.balaicoffeedashboard.accordion_library.AccordionView
import com.example.balaicoffeedashboard.databinding.ComponentMaterialCategoryBinding
import com.example.balaicoffeedashboard.databinding.ComponentMaterialListBinding
import com.example.balaicoffeedashboard.models.MaterialDashboardModel

class MaterialDashboardAdapter(val dataArray: List<MaterialDashboardModel?>?): AccordionAdapter {
    private lateinit var bindingTitle: ComponentMaterialCategoryBinding
    private lateinit var bindingComponent: ComponentMaterialListBinding
    override fun onCreateViewHolderForTitle(parent: ViewGroup): AccordionView.ViewHolder {
        val layoutInflater = TitleViewHolder.create(parent)
        bindingTitle = ComponentMaterialCategoryBinding.inflate(layoutInflater)
        return TitleViewHolder(bindingTitle.root)
    }

    override fun onCreateViewHolderForContent(parent: ViewGroup): AccordionView.ViewHolder {
        val layoutInflater = ContentViewHolder.create(parent)
        bindingComponent = ComponentMaterialListBinding.inflate(layoutInflater)
        return ContentViewHolder(bindingComponent.root)
    }

    override fun onBindViewForTitle(
        viewHolder: AccordionView.ViewHolder,
        position: Int,
        arrowDirection: AccordionAdapter.ArrowDirection
    ) {
        val dataModel = dataArray?.get(position)
        (viewHolder as TitleViewHolder).itemView.apply {
            bindingTitle.titleComponentTextView.text = dataModel?.categoryName
            when (arrowDirection) {
                AccordionAdapter.ArrowDirection.UP -> bindingTitle.arrowIconImageView.setImageResource(R.drawable.arrow_up)
                AccordionAdapter.ArrowDirection.DOWN -> bindingTitle.arrowIconImageView.setImageResource(R.drawable.arrow_down)
                AccordionAdapter.ArrowDirection.NONE -> bindingTitle.arrowIconImageView.setImageResource(R.drawable.arrow_down)
            }
        }
    }

    override fun onBindViewForContent(viewHolder: AccordionView.ViewHolder, position: Int) {
        val dataModel = dataArray?.get(position) ?: MaterialDashboardModel()
        (viewHolder as ContentViewHolder).itemView.apply {
            bindingComponent.linearLayout.removeAllViews()
            dataModel?.bahanRekapList?.forEach {
                val component = MaterialComponentView(context)
                component.initComponent(it)
                bindingComponent.linearLayout.addView(component)
            }
        }
    }

    override fun getItemCount() = dataArray?.size ?: 0

}

class TitleViewHolder(itemView: View): AccordionView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): LayoutInflater {
            return LayoutInflater.from(parent.context)
        }
    }
}

class ContentViewHolder(itemView: View): AccordionView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): LayoutInflater {
            return LayoutInflater.from(parent.context)
        }
    }
}