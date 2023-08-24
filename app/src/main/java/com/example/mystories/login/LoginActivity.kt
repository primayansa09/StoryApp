package com.example.mystories.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mystories.api.ApiConfig
import com.example.mystories.api.LoginResponse
import com.example.mystories.databinding.ActivityLoginBinding
import com.example.mystories.preferences.UserPreference
import com.example.mystories.ui.HomeActivity
import com.example.mystories.ui.MyEditText
import com.example.mystories.ui.MyTextVIew
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreference
    private lateinit var myTextView: MyTextVIew
    private lateinit var myEditText: MyEditText

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreference(this)

        myEditText = binding.edLoginPassword
        myTextView = binding.txtValidatedPassword

        setAction()
        playAnimation()
        setMyTextViewEnable()

        myEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyTextViewEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setMyTextViewEnable() {
        val result = binding.edLoginPassword.text.toString()
        myTextView.isVisible = result.length < 8 && result.toString().isNotEmpty()
    }

    private fun postLogin() {
        showLoading(true)
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()

        when{
            email.isEmpty() -> {
                showLoading(false)
                Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                showLoading(false)
                Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
            }

            else ->{
                val client = ApiConfig.getApiService().loginUser(email, password)
                client.enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful){
                            showLoading(false)
                            val responseBody = response.body()
                            if(responseBody != null){
                                Log.e(TAG, "success: ${response.body()}")
                                response.body()?.let { userPreferences.setToken(it.loginResult?.token) }
                                response.body()?.let { userPreferences.setSession(true) }
                                moveIntent()
                                Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            showLoading(false)
                            Log.e(TAG, "onFailure: ${response.message()}")
                            Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            }
        }
    }

    private fun moveIntent() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun setAction() {
        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.loginButton.setOnClickListener {
            postLogin()

        }

        binding.edLoginEmail.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.edLoginEmailLyt.helperText = valueEmail()
            }
        }

    }
    //Property Animation
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.btnBack, View.TRANSLATION_X, 30f).apply {
            duration = 4000
        }.start()

        ObjectAnimator.ofFloat(binding.txtLogin, View.TRANSLATION_X, 30f).apply {
            duration = 4000
        }.start()

        val emailTxt = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdtTxt = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val emailEdtTxtLyt = ObjectAnimator.ofFloat(binding.edLoginEmailLyt, View.ALPHA, 1f).setDuration(500)
        val passwordTxt = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdtTxt = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEdtTxtLyt = ObjectAnimator.ofFloat(binding.edLoginPasswordLyt, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(emailTxt, emailEdtTxt, emailEdtTxtLyt, passwordTxt, passwordEdtTxtLyt, passwordEdtTxt, loginBtn)
        }.start()
    }

    private fun valueEmail(): String?
    {
        val emailTxt = binding.edLoginEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
        {
            return "Invalid Email"
        }
        return null
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}