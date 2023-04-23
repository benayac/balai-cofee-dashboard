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
import com.example.balaicoffeedashboard.R
import com.example.balaicoffeedashboard.SharedPref
import com.example.balaicoffeedashboard.databinding.ActivityAddStockBinding
import com.example.balaicoffeedashboard.databinding.ActivityNoteFinalStockBinding
import com.example.balaicoffeedashboard.databinding.BahanPickerBottomSheetBinding
import com.example.balaicoffeedashboard.models.BahanResponseData
import com.example.balaicoffeedashboard.models.CategoryIdListRequest
import com.example.balaicoffeedashboard.models.RekapTodayRequest
import com.example.balaicoffeedashboard.models.UpdateRekapTodayRequest
import com.example.balaicoffeedashboard.viewModels.NoteFinalStockViewModel
import com.example.balaicoffeedashboard.views.BahanListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class NoteFinalStockActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityNoteFinalStockBinding
    private lateinit var viewModel: NoteFinalStockViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bahanListAdapter: BahanListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFinalStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NoteFinalStockViewModel::class.java]
        viewModel.activitySource = intent.getStringExtra(EXTRA_FINAL_NOTE_OPTION) ?: ACTIVITY_SOURCE_ONE
        initLayout()
        initObserver()
        loadAPI()
    }

    private fun setBahan() {
        if(viewModel.selectedBahanIndex >= (viewModel.bahanList.value?.size ?: 0)) {
            finish()
            return
        }
        val bahan = viewModel.bahanList.value?.get(viewModel.selectedBahanIndex)
        viewModel.selectedBahan?.value = bahan
        binding.bahanNameTextView.text = bahan?.bahanName
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getRekapTodayList(sharedPref.authenticationPref, this, RekapTodayRequest(listOf( viewModel.selectedBahan?.value?.bahanId)))
        if(bahan?.isCountSell == true) {
            binding.terpakaiEditText.visibility = View.VISIBLE
            binding.terpakaiTitleTextView.visibility = View.VISIBLE
        } else {
            binding.terpakaiEditText.visibility = View.GONE
            binding.terpakaiTitleTextView.visibility = View.GONE
        }
    }

    private fun initLayout() {
        initializeBottomSheet()
        binding.leftArrowImageView.setOnClickListener {
            finish()
        }
        binding.submitButtonCardView.setOnClickListener {
            if(viewModel.selectedBahan?.value == null || binding.stokAkhirEditText.text.isNullOrEmpty() || (viewModel.selectedBahan?.value?.isCountSell == true && binding.stokAkhirEditText.text.isNullOrEmpty())) {
                Toast.makeText(this, "Please complete all input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val rekapToday = viewModel.rekapToday.value?.get(0)
                var request = UpdateRekapTodayRequest()
                if(viewModel.selectedBahan?.value?.isCountSell == true) {
                    request.rekapId = rekapToday?.rekapId
                    request.stokAwal = rekapToday?.stokAwal
                    request.stokAkhir = binding.stokAkhirEditText.text.toString().toInt()
                    request.inData = rekapToday?.inData
                    request.terpakai = binding.terpakaiEditText.text.toString().toInt()
                } else {
                    val terpakai = (rekapToday?.inData?.plus(rekapToday.stokAwal ?: 0) ?: 0) - binding.stokAkhirEditText.text.toString().toInt()
                    request.rekapId = rekapToday?.rekapId
                    request.stokAwal = rekapToday?.stokAwal
                    request.stokAkhir = binding.stokAkhirEditText.text.toString().toInt()
                    request.inData = rekapToday?.inData
                    request.terpakai = terpakai
                }
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
            if(viewModel.activitySource == ACTIVITY_SOURCE_ALL) {
                setBahan()
            }
        }
        viewModel.rekapToday.observe(this) {
            binding.progressBar.visibility = View.GONE
        }
        viewModel.isDoneNoteFinalStockToday.observe(this) { isDone ->
            if (isDone) {
                binding.progressBar.visibility = View.GONE
            }
            if(viewModel.activitySource == ACTIVITY_SOURCE_ALL) {
                viewModel.selectedBahanIndex += 1
                setBahan()
            }
        }
    }

    private fun initializeBottomSheet() {
        if(viewModel.activitySource == ACTIVITY_SOURCE_ONE) {
            binding.bahanPickerConstraintLayout.setOnClickListener {
                showBottomSheetDialog()
            }
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
        if(bahan.isCountSell == true) {
            binding.terpakaiEditText.visibility = View.VISIBLE
            binding.terpakaiTitleTextView.visibility = View.VISIBLE
        } else {
            binding.terpakaiEditText.visibility = View.GONE
            binding.terpakaiTitleTextView.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_FINAL_NOTE_OPTION = "EXTRA_FINAL_NOTE_OPTION"
        const val ACTIVITY_SOURCE_ONE = "FROM_ONE_NOTE"
        const val ACTIVITY_SOURCE_ALL = "FROM_ALL_NOTE"
    }
}