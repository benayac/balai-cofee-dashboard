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
import com.example.balaicoffeedashboard.databinding.ActivityCorrectionStockBinding
import com.example.balaicoffeedashboard.databinding.BahanPickerBottomSheetBinding
import com.example.balaicoffeedashboard.databinding.CorrectionPickerBottomSheetBinding
import com.example.balaicoffeedashboard.models.BahanResponseData
import com.example.balaicoffeedashboard.models.CategoryIdListRequest
import com.example.balaicoffeedashboard.models.RekapTodayRequest
import com.example.balaicoffeedashboard.models.UpdateRekapTodayRequest
import com.example.balaicoffeedashboard.viewModels.CorrectionStockViewModel
import com.example.balaicoffeedashboard.views.BahanListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class CorrectionStockActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityCorrectionStockBinding
    private lateinit var viewModel: CorrectionStockViewModel
    private lateinit var bottomSheetBahanDialog: BottomSheetDialog
    private lateinit var bottomSheetCorrectionDialog: BottomSheetDialog
    private lateinit var bahanListAdapter: BahanListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCorrectionStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CorrectionStockViewModel::class.java]
        initLayout()
        initObserver()
        loadAPI()
    }

    private fun initLayout() {
        initializeBottomSheet()
        binding.leftArrowImageView.setOnClickListener {
            finish()
        }
        binding.submitButtonCardView.setOnClickListener {
            if(viewModel.selectedBahan?.value == null || binding.quantityEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please complete all input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val rekapToday = viewModel.rekapToday.value?.get(0)
                val request = UpdateRekapTodayRequest()
                if(viewModel.selectedCorrection.value == SELECTED_CORRECTION_IN) {
                    request.rekapId = rekapToday?.rekapId
                    request.stokAwal = rekapToday?.stokAwal
                    request.stokAkhir = rekapToday?.stokAkhir
                    request.inData = binding.quantityEditText.text.toString().toInt()
                    //request.terpakai = request.stokAwal?.plus(request.inData ?: 0)?.minus(request.stokAkhir ?: 0)
                } else if (viewModel.selectedCorrection.value == SELECTED_CORRECTION_STOK_AKHIR){
                    request.rekapId = rekapToday?.rekapId
                    request.stokAwal = rekapToday?.stokAwal
                    request.stokAkhir = binding.quantityEditText.text.toString().toInt()
                    request.inData = rekapToday?.inData
                    //request.terpakai = request.stokAwal?.plus(request.inData ?: 0)?.minus(request.stokAkhir ?: 0)
                }
                request.terpakai = (rekapToday?.stokAwal?.plus(request.inData ?: 0))?.minus(request.stokAkhir ?: 0)
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
        viewModel.isDoneCorrectionStock.observe(this) { isDone ->
            if (isDone) {
                binding.progressBar.visibility = View.GONE
            }
        }
        viewModel.selectedCorrection.observe(this) { selectedCorrection ->
            if(selectedCorrection == SELECTED_CORRECTION_IN) {
                binding.pageNameTitleView.text = "Koreksi stok tambahan"
                binding.submitButtonTextView.text = "Tambah stok"
            } else if (selectedCorrection == SELECTED_CORRECTION_STOK_AKHIR) {
                binding.pageNameTitleView.text = "Koreksi stok akhir"
                binding.submitButtonTextView.text = "Simpan stok"
            }
        }
    }

    private fun initializeBottomSheet() {
        bottomSheetCorrectionDialog = BottomSheetDialog(this)
        val bottomSheetInflater = LayoutInflater.from(this)
        val bottomSheetBinding = CorrectionPickerBottomSheetBinding.inflate(bottomSheetInflater)
        bottomSheetCorrectionDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.closeImageView.setOnClickListener {
            bottomSheetCorrectionDialog.dismiss()
        }
        bottomSheetBinding.inConstraintLayout.setOnClickListener {
            viewModel.selectedCorrection.value = SELECTED_CORRECTION_IN
            bottomSheetCorrectionDialog.dismiss()
        }
        bottomSheetBinding.stokAKhirConstraintLayout.setOnClickListener {
            viewModel.selectedCorrection.value = SELECTED_CORRECTION_STOK_AKHIR
            bottomSheetCorrectionDialog.dismiss()
        }
        bottomSheetCorrectionDialog.show()

        binding.bahanPickerConstraintLayout.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        bottomSheetBahanDialog = BottomSheetDialog(this)
        val bottomSheetInflater = LayoutInflater.from(this)
        val bottomSheetBinding = BahanPickerBottomSheetBinding.inflate(bottomSheetInflater)
        bottomSheetBahanDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.closeImageView.setOnClickListener {
            bottomSheetBahanDialog.dismiss()
        }
        bottomSheetBinding.bahanListRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomSheetBinding.bahanListRecyclerView.adapter = bahanListAdapter
        bottomSheetBahanDialog.show()
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
        bottomSheetBahanDialog.setOnDismissListener {
            bahanListAdapter.filter.filter("")
        }
    }

    private fun bahanListClickedListener(bahan: BahanResponseData) {
        binding.bahanNameTextView.text = bahan.bahanName
        viewModel.selectedBahan?.value = bahan
        viewModel.getRekapTodayList(sharedPref.authenticationPref, this, RekapTodayRequest(listOf( viewModel.selectedBahan?.value?.bahanId)))
        binding.progressBar.visibility = View.VISIBLE
        bottomSheetBahanDialog.dismiss()
    }

    companion object {
        const val SELECTED_CORRECTION_IN = "SELECTED_CORRECTION_IN"
        const val SELECTED_CORRECTION_STOK_AKHIR = "SELECTED_CORRECTION_STOK_AKHIR"
    }
}