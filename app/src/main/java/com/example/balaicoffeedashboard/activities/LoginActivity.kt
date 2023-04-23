package com.example.balaicoffeedashboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.balaicoffeedashboard.App
import com.example.balaicoffeedashboard.SharedPref
import com.example.balaicoffeedashboard.databinding.ActivityLoginBinding
import com.example.balaicoffeedashboard.models.BaseResponse
import com.example.balaicoffeedashboard.models.LoginRequest
import com.example.balaicoffeedashboard.models.LoginResponse
import com.example.balaicoffeedashboard.models.RegisterRequest
import com.example.balaicoffeedashboard.networks.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { App.prefs!! }
    private lateinit var binding: ActivityLoginBinding
    private var isLogin: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        isLogin = intent.getBooleanExtra(EXTRA_IS_LOGIN, true)
        setLayout()
    }

    private fun loginUser() {
        binding.progressBar.visibility = View.VISIBLE
        val loginRequest = LoginRequest(
            username = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toString()
        )
        RetrofitBuilder().getService().loginUser(loginRequest).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LOGIN_RESPONSE", response.body().toString())
                val responseBody = response.body()
                if(responseBody?.status == true && responseBody.data?.authentication?.isNotEmpty() == true) {
                    binding.progressBar.visibility = View.GONE
                    sharedPref.loginStatusPref = true
                    sharedPref.userPrivilegePref = responseBody.data.credential_type ?: "USER"
                    sharedPref.authenticationPref = response.body()?.data?.authentication.toString()
                    val mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.d("LOGIN", "success but error: ${response.body()}")
                    Toast.makeText(this@LoginActivity, "Fail to login, wrong credential", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.d("LOGIN", "error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Fail to login, wrong credential", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerUser() {
        binding.progressBar.visibility = View.VISIBLE
        val registerRequest = RegisterRequest(
            username = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
            credentialType = "USER"
        )
        RetrofitBuilder().getService().registerUser(registerRequest).enqueue(object: Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Register Success", Toast.LENGTH_SHORT).show()
                swapLoginFlag()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Fail to register, please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun swapLoginFlag() {
        isLogin = !isLogin
        intent.putExtra(EXTRA_IS_LOGIN, this.isLogin)
        startActivity(intent)
        finish()
    }

    private fun setLayout() {
        binding.footnoteHyperlinkTextView.setOnClickListener {
            swapLoginFlag()
        }
        binding.submitButton.setOnClickListener {
            if(isLogin) {
                loginUser()
            } else {
                registerUser()
            }
        }
        if(isLogin) {
            binding.contentHeaderTextView.text = "Masuk"
            binding.submitButtonTextView.text = "Masuk"
            binding.footnoteTextView.text = "Belum punya akun? "
            binding.footnoteHyperlinkTextView.text = "Daftar sekarang."
        } else {
            binding.contentHeaderTextView.text = "Registrasi"
            binding.submitButtonTextView.text = "Daftar"
            binding.footnoteTextView.text = "Sudah punya akun? "
            binding.footnoteHyperlinkTextView.text = "Masuk sekarang."
        }
    }

    companion object {
        const val EXTRA_IS_LOGIN = "EXTRA_IS_LOGIN"
    }
}