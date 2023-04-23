package com.example.balaicoffeedashboard.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.balaicoffeedashboard.R
import com.example.balaicoffeedashboard.databinding.ComponentMaterialBinding
import com.example.balaicoffeedashboard.models.BahanRekapData

class MaterialComponentView: FrameLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    private var binding: ComponentMaterialBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = ComponentMaterialBinding.inflate(layoutInflater)
        addView(binding.root)
    }

    fun initComponent(bahanRekapData: BahanRekapData) {
        binding.materialTitleTextView.text = "${bahanRekapData.bahan?.bahanName} (${bahanRekapData.bahan?.bahanUnit})"
        binding.stokAwalTextView.text = "Stok awal ${bahanRekapData.rekapTodayData?.stokAwal.toString()}"
        binding.stokAkhirTextView.text = "Stok akhir ${bahanRekapData.rekapTodayData?.stokAkhir.toString()}"
        binding.inTextView.text = "In ${bahanRekapData.rekapTodayData?.inData.toString()}"
        binding.usedTextView.text = "Terpakai ${bahanRekapData.rekapTodayData?.terpakai.toString()}"
        if ((bahanRekapData.rekapTodayData?.stokAkhir ?: 0) <= (bahanRekapData.bahan?.minimumAmount ?: 0)) {
            binding.stockLowLinearLayout.visibility = View.VISIBLE
        } else {
            binding.stockLowLinearLayout.visibility = View.GONE
        }
    }
}