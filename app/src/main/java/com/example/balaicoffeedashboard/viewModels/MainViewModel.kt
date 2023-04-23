package com.example.balaicoffeedashboard.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balaicoffeedashboard.activities.MainActivity
import com.example.balaicoffeedashboard.models.BaseResponse
import com.example.balaicoffeedashboard.models.GetBahanIdsResponseModel
import com.example.balaicoffeedashboard.models.RekapTodayRequest
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class MainViewModel: ViewModel() {

    var bahanIdList: List<Int?>? = null

    fun getBahanList(auth: String, context: Context) {
        RetrofitBuilder().getService().getAllBahanId(authHeader = auth).enqueue(object:
            Callback<GetBahanIdsResponseModel>{
            override fun onResponse(
                call: Call<GetBahanIdsResponseModel>,
                response: Response<GetBahanIdsResponseModel>
            ) {
                bahanIdList = response.body()?.data
                generateRekap(auth, context)
            }

            override fun onFailure(call: Call<GetBahanIdsResponseModel>, t: Throwable) {
                Toast.makeText(context, "Fail to fetch bahan list", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun generateRekap(auth: String, context: Context) {
        val request = RekapTodayRequest(
            bahanIds = bahanIdList
        )
        RetrofitBuilder().getService().generateRekap(authHeader = auth, listIds = request).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                Toast.makeText(context, "Success generate recap", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(context, "Fail to generate recap. ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun downloadExportRekap(auth: String, rekapDate: String, context: Context) {
        Log.d("DOWNLOADEXPORT", "RekapDate $rekapDate")
        RetrofitBuilder().getService().exportRekap(authHeader = auth, rekapDate = rekapDate)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d("DOWNLOADEXPORT", "Response Success Request: ${call.request()}")
                        Log.d("DOWNLOADEXPORT", "RESPONSE: ${response.message()}")
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                val filename = "balai-kopi-export.xlsx"
                                val file = File(
                                    Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS
                                    ), filename
                                )
                                var inputStream: InputStream? = null
                                var outputStream: OutputStream? = null
                                try {
                                    inputStream = response.body()?.byteStream()
                                    outputStream = FileOutputStream(file)

                                    val buffer = ByteArray(4096)
                                    var bytesRead: Int
                                    while (inputStream?.read(buffer)
                                            .also { bytesRead = it!! } != -1
                                    ) {
                                        outputStream.write(buffer, 0, bytesRead)
                                    }

                                    outputStream.flush()

                                    // Add file to Media Store to make it available in the system's Downloads app
                                    MediaScannerConnection.scanFile(
                                        context,
                                        arrayOf(file.toString()),
                                        null,
                                        null
                                    )

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    inputStream?.close()
                                    outputStream?.close()
                                }
                            }
                        }
                    }
                    Toast.makeText(context, "File downloaded to downloads directory as balai-kopi-export.xlsx", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Fail to download export. ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}