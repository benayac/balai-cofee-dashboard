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
import com.example.balaicoffeedashboard.databinding.ActivityAddStockBinding
import com.example.balaicoffeedashboard.databinding.BahanPickerBottomSheetBinding
import com.example.balaicoffeedashboard.models.BahanResponseData
import com.example.balaicoffeedashboard.models.CategoryIdListRequest
import com.example.balaicoffeedashboard.models.RekapTodayRequest
import com.example.balaicoffeedashboard.models.UpdateRekapTodayRequest
import com.example.balaicoffeedashboard.viewModels.AddStockViewModel
import com.example.balaicoffeedashboard.views.BahanListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddStockActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityAddStockBinding
    private lateinit var viewModel: AddStockViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bahanListAdapter: BahanListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AddStockViewModel::class.java]
        initLayout()
        initializeBottomSheet()
        initObserver()
        loadAPI()
    }

    private fun initLayout() {
        binding.leftArrowImageView.setOnClickListener {
            finish()
        }
        binding.submitButtonCardView.setOnClickListener {
            if(viewModel.selectedBahan?.value == null || binding.inEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please complete all input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val rekapToday = viewModel.rekapToday.value?.get(0)
                val request = UpdateRekapTodayRequest(
                    rekapId = rekapToday?.rekapId,
                    stokAwal = rekapToday?.stokAwal,
                    stokAkhir = rekapToday?.stokAkhir,
                    inData = rekapToday?.inData?.plus(binding.inEditText.text.toString().toInt()),
                    terpakai = rekapToday?.terpakai
                )
                binding.progressBar.visibility = View.VISIBLE
                viewModel.updateRekapToday(sharedPref.authenticationPref, this, request)
            }
        }
    }

    private fun loadAPI() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCategoryList(sharedPref.authenticationPref, this)
    }

    private fun initObserver() {
        viewModel.isDoneGetCategoryListLiveData.observe(this) { isDone ->
            if (isDone) {
                val idList: MutableList<Int> = mutableListOf()
                viewModel.categoryList?.forEach {
                    idList.add(it?.categoryId ?: 0)
                }
                viewModel.getBahanFromCategoryList(auth = sharedPref.authenticationPref, context = this, request = CategoryIdListRequest(idList))
            }
        }
        viewModel.bahanList.observe(this) { bahanList ->
            bahanListAdapter = BahanListAdapter(bahanList) {
                bahanListClickedListener(it)
            }
            binding.progressBar.visibility = View.GONE
        }
        viewModel.rekapToday.observe(this) {
            binding.progressBar.visibility = View.GONE
        }
        viewModel.isDoneUpdateRekapToday.observe(this) { isDone ->
            if (isDone) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun initializeBottomSheet() {
        binding.bahanPickerConstraintLayout.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetInflater = LayoutInflater.from(this)
        val bottomSheetBinding = BahanPickerBottomSheetBinding.inflate(bottomSheetInflater)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.closeImageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.bahanListRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomSheetBinding.bahanListRecyclerView.adapter = bahanListAdapter
        bottomSheetDialog.show()
        bottomSheetBinding.searchBahanEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bahanListAdapter.filter.filter(p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bahanListAdapter.filter.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                bahanListAdapter.filter.filter(p0.toString())
            }
        })
        bottomSheetDialog.setOnDismissListener {
            bahanListAdapter.filter.filter("")
        }
    }

    private fun bahanListClickedListener(bahan: BahanResponseData) {
        Toast.makeText(this, "Pressed bahan ${bahan.bahanName}", Toast.LENGTH_SHORT).show()
        binding.bahanNameTextView.text = bahan.bahanName
        viewModel.selectedBahan?.value = bahan
        viewModel.getRekapTodayList(sharedPref.authenticationPref, this, RekapTodayRequest(listOf( viewModel.selectedBahan?.value?.bahanId)))
        binding.progressBar.visibility = View.VISIBLE
        bottomSheetDialog.dismiss()
    }
}