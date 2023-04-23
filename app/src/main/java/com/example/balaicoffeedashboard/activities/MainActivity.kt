package com.example.balaicoffeedashboard.activities

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.balaicoffeedashboard.App
import com.example.balaicoffeedashboard.SharedPref
import com.example.balaicoffeedashboard.activities.NoteFinalStockActivity.Companion.ACTIVITY_SOURCE_ALL
import com.example.balaicoffeedashboard.activities.NoteFinalStockActivity.Companion.ACTIVITY_SOURCE_ONE
import com.example.balaicoffeedashboard.activities.NoteFinalStockActivity.Companion.EXTRA_FINAL_NOTE_OPTION
import com.example.balaicoffeedashboard.databinding.ActivityMainBinding
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import com.example.balaicoffeedashboard.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        if(!sharedPref.loginStatusPref) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        setLayout()
    }

    private fun setLayout() {
        if(sharedPref.userPrivilegePref == "ADMIN") {
            binding.addNewMaterialButton.visibility = View.VISIBLE
        } else {
            binding.addNewMaterialButton.visibility = View.INVISIBLE
        }
        binding.logoutLinearLayout.setOnClickListener {
            sharedPref.resetPref()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.dashboardButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        binding.addStockButton.setOnClickListener {
            startActivity(Intent(this, AddStockActivity::class.java))
        }
        binding.noteOneFinalStockButton.setOnClickListener {
            val intent = Intent(this, NoteFinalStockActivity::class.java)
            intent.putExtra(EXTRA_FINAL_NOTE_OPTION, ACTIVITY_SOURCE_ONE)
            startActivity(intent)
        }
        binding.noteAllFinalStockButton.setOnClickListener {
            val intent = Intent(this, NoteFinalStockActivity::class.java)
            intent.putExtra(EXTRA_FINAL_NOTE_OPTION, ACTIVITY_SOURCE_ALL)
            startActivity(intent)
        }
        binding.correctionButton.setOnClickListener {
            startActivity(Intent(this, CorrectionStockActivity::class.java))
        }
        binding.addNewMaterialButton.setOnClickListener {
            startActivity(Intent(this, AddNewMaterialActivity::class.java))
        }
        binding.generateRecapCardView.setOnClickListener {
            viewModel.getBahanList(sharedPref.authenticationPref, this)
        }
        binding.exportRecapCardView.setOnClickListener {
            checkPermission()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        var selectedDate = ""
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val month = String.format("%02d", monthOfYear+1)
            val day = String.format("%02d", dayOfMonth)
            selectedDate = "$year-$month-$day"
            Toast.makeText(this, "Export Rekap From Date $selectedDate", Toast.LENGTH_SHORT).show()
            viewModel.downloadExportRekap(sharedPref.authenticationPref, selectedDate, this)
        }
        val datePickerDialog = DatePickerDialog(
            this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
        )
        datePickerDialog.show()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            // Permission granted, start the download process
            showDatePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do something
                showDatePicker()
            } else {
                Toast.makeText(this, "Please give the proper permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 123
    }
}