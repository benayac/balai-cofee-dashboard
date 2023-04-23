package com.example.balaicoffeedashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.balaicoffeedashboard.App
import com.example.balaicoffeedashboard.SharedPref
import com.example.balaicoffeedashboard.databinding.ActivityAddNewMaterialBinding
import com.example.balaicoffeedashboard.databinding.BahanPickerBottomSheetBinding
import com.example.balaicoffeedashboard.models.*
import com.example.balaicoffeedashboard.viewModels.AddNewMaterialViewModel
import com.example.balaicoffeedashboard.views.CategoryListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddNewMaterialActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityAddNewMaterialBinding
    private lateinit var viewModel: AddNewMaterialViewModel
    private lateinit var bottomSheetCategoryDialog: BottomSheetDialog
    private lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AddNewMaterialViewModel::class.java]
        initLayout()
        initObserver()
        loadAPI()
    }

    private fun initLayout() {
        binding.leftArrowImageView.setOnClickListener {
            finish()
        }
        binding.categoryBahanPickerConstraintLayout.setOnClickListener {
            bottomSheetCategoryDialog.show()
        }
        binding.submitButtonCardView.setOnClickListener {
            if(viewModel.selectedCategory.value == null || binding.bahanNameEditText.text.isNullOrEmpty() ||
                binding.minimumAmountEditText.text.isNullOrEmpty() || binding.unitEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please complete all input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.progressBar.visibility = View.VISIBLE
                val selectedCategory = viewModel.selectedCategory.value
                val request = AddNewBahanRequest(
                    bahanName = binding.bahanNameEditText.text.toString(),
                    categoryId = selectedCategory?.categoryId ?: 0,
                    bahanUnit = binding.unitEditText.text.toString(),
                    minimumAmount = binding.minimumAmountEditText.text.toString().toInt(),
                    isCountSell = binding.countSellSwitch.isActivated
                )
                viewModel.addNewBahan(sharedPref.authenticationPref, context = this, request = request)
            }
        }
    }

    private fun initializeBottomSheet() {
        bottomSheetCategoryDialog = BottomSheetDialog(this)
        val bottomSheetInflater = LayoutInflater.from(this)
        val bottomSheetBinding = BahanPickerBottomSheetBinding.inflate(bottomSheetInflater)
        bottomSheetCategoryDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.chooseBahanTitleTextView.text = "Pilih kategori bahan"
        bottomSheetBinding.searchBahanEditText.hint = "Cari kategori bahan"
        bottomSheetBinding.closeImageView.setOnClickListener {
            bottomSheetCategoryDialog.dismiss()
        }
        bottomSheetBinding.bahanListRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomSheetBinding.bahanListRecyclerView.adapter = categoryListAdapter
        bottomSheetBinding.searchBahanEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categoryListAdapter.filter.filter(p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categoryListAdapter.filter.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                categoryListAdapter.filter.filter(p0.toString())
            }
        })
        bottomSheetCategoryDialog.setOnDismissListener {
            categoryListAdapter.filter.filter("")
        }
    }

    private fun initObserver() {
        viewModel.categoryList.observe(this) { categoryList ->
            binding.progressBar.visibility = View.GONE
            if (categoryList != null) {
                categoryListAdapter = CategoryListAdapter(categoryList) {
                    categoryListClickedListener(it)
                }
                initializeBottomSheet()
            }
        }
        viewModel.isDoneAddNewBahan.observe(this) { isDone ->
            if (isDone) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadAPI() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCategoryList(sharedPref.authenticationPref, this)
    }

    private fun categoryListClickedListener(bahan: CategoryListData) {
//        Toast.makeText(this, "Pressed bahan ${bahan.categoryName}", Toast.LENGTH_SHORT).show()
        binding.categoryNameTextView.text = bahan.categoryName
        viewModel.selectedCategory.value = bahan
        bottomSheetCategoryDialog.dismiss()
    }
}