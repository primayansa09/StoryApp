package com.example.mystories.signup

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
import com.example.mystories.MainActivity
import com.example.mystories.api.ApiConfig
import com.example.mystories.api.UserResponse
import com.example.mystories.databinding.ActivitySignupBinding
import com.example.mystories.preferences.UserPreference
import com.example.mystories.ui.MyEditText
import com.example.mystories.ui.MyTextVIew
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userPreferences: UserPreference
    private lateinit var myEditText: MyEditText
    private lateinit var myTextView: MyTextVIew

    companion object{
        private const val TAG = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreference(this)

        myEditText = binding.edRegisterPassword
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
        val result = binding.edRegisterPassword.text.toString()
        myTextView.isVisible = result.length < 8 && result.toString().isNotEmpty()
    }


    //Property Animation
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgBack, View.TRANSLATION_X, 30f).apply {
            duration = 4000
        }.start()

        ObjectAnimator.ofFloat(binding.signupTextView, View.TRANSLATION_X, 30f).apply {
            duration = 4000
        }.start()

        val nameTxt = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEdtTxt = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val nameEdtTxtLyt = ObjectAnimator.ofFloat(binding.edRegisterNameLyt, View.ALPHA, 1f).setDuration(500)
        val emailTxt = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdtTxt = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val emailEdtTxtLyt = ObjectAnimator.ofFloat(binding.edRegisterEmailLyt, View.ALPHA, 1f).setDuration(500)
        val passwordTxt = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdtTxt = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEdtTxtLyt = ObjectAnimator.ofFloat(binding.edRegisterPasswordLyt, View.ALPHA, 1f).setDuration(500)
        val signupBtn = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        val nameTogether = AnimatorSet().apply {
            playTogether(nameEdtTxt, nameEdtTxtLyt)
        }

        val emailTogether = AnimatorSet().apply {
            playTogether(emailEdtTxt, emailEdtTxtLyt)
        }

        val passwordTogether = AnimatorSet().apply {
            playTogether(passwordEdtTxt, passwordEdtTxtLyt)
        }

        AnimatorSet().apply {
            playSequentially(nameTxt, nameTogether, emailTxt, emailTogether, passwordTxt, passwordTogether, signupBtn)
            start()
        }
    }

    private fun setAction() {
        binding.imgBack.setOnClickListener{
            finish()
        }

        binding.signupButton.setOnClickListener{
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if(name.isEmpty()){
                Toast.makeText(this@SignupActivity, "Nama harus diisi", Toast.LENGTH_SHORT).show()
            }

            if(email.isEmpty()){
                Toast.makeText(this@SignupActivity, "Email harus diisi", Toast.LENGTH_SHORT).show()
            }
            if(password.isEmpty()){
                Toast.makeText(this@SignupActivity, "Password harus diisi", Toast.LENGTH_SHORT).show()
            }

            showLoading(true)
            val client = ApiConfig.getApiService().signupUser(name, email, password)
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    showLoading(false)
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if (responseBody != null){
                            moveIntent()
                            Toast.makeText(this@SignupActivity, "Daftar Berhasil", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "success: ${response.message()}")
                        }
                    }else{
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        }

        binding.edRegisterEmail.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.edRegisterEmailLyt.helperText = valueEmail()
            }
        }
    }

    private fun moveIntent() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun valueEmail(): String?
    {
        val emailTxt = binding.edRegisterEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
        {
            return "Invalid Email"
        }
        return null
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}