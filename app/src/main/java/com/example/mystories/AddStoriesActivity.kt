package com.example.mystories

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.mystories.api.ApiConfig
import com.example.mystories.api.UserResponse
import com.example.mystories.databinding.ActivityAddStoriesBinding
import com.example.mystories.preferences.UserPreference
import com.example.mystories.ui.HomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoriesBinding
    private lateinit var tokenPreferences: UserPreference
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    companion object {
        const val TAG = "AddStoriesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenPreferences = UserPreference(this)

        setAction()
        playAnimation()

    }

    private fun setAction() {
        binding.toolbarAdd.btnBack.setOnClickListener(View.OnClickListener {
            finish()
        })
        binding.addContent.btnGalery.setOnClickListener{ startGalery() }
        binding.addContent.btnCamera.setOnClickListener{startTakePhoto()}
        binding.addContent.buttonAdd.setOnClickListener{uploadImage()}
    }


    //Property Animation
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.toolbarAdd.btnBack, View.TRANSLATION_X, 30f).apply {
            duration = 400
        }.start()

        val image = ObjectAnimator.ofFloat(binding.addContent.priviewImage, View.ALPHA,1f).setDuration(500)
        val btnGalery = ObjectAnimator.ofFloat(binding.addContent.btnGalery, View.ALPHA, 1f).setDuration(2000)
        val btnCamera = ObjectAnimator.ofFloat(binding.addContent.btnCamera, View.ALPHA, 1f).setDuration(2000)
        val txtDescription = ObjectAnimator.ofFloat(binding.addContent.descriptionTextView, View.ALPHA, 1f).setDuration(500)
        val txtInputLyt = ObjectAnimator.ofFloat(binding.addContent.edAddDescriptionLyt, View.ALPHA, 1f).setDuration(500)
        val btnUpload = ObjectAnimator.ofFloat(binding.addContent.buttonAdd, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(btnGalery, btnCamera)
        }

        AnimatorSet().apply {
            playSequentially(image, together, txtDescription, txtInputLyt, btnUpload)
            start()
        }
    }

    private fun uploadImage(){
        showLoading(true)

        if (getFile != null){
            val file = reduceFileImage(getFile as File)
            val edtDesc = binding.addContent.edAddDescription.text.toString().trim()

            val description = edtDesc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val token = "Bearer ${tokenPreferences.getToken()}"
            val apiService = ApiConfig.getApiService()
            val uploadImageRequest = apiService.setStories(token,imageMultipart, description)
            uploadImageRequest.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful){
                        val responseBody = response.body()
                        if (responseBody != null){
                            val intent = Intent(this@AddStoriesActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            Toast.makeText(this@AddStoriesActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "success: ${response.message()}")
                        }
                    }else{
                        showLoading(false)
                        Toast.makeText(this@AddStoriesActivity,"Deskripsi harus diisi" , Toast.LENGTH_SHORT).show()
//                        response.message()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })

        }else{
            showLoading(false)
            Log.e(TAG, "onFailure:")
            Toast.makeText(this@AddStoriesActivity, "Silahkan masukan berkas gambar terlebih daluhu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoriesActivity,
                "com.example.mystories",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGalery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGalery.launch(chooser)
    }

    private val launcherIntentGalery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            val selected = result.data?.data as Uri
            selected.let { uri ->
                val myFile = uriToFile(uri, this@AddStoriesActivity)
                getFile = myFile
                binding.addContent.priviewImage.setImageURI(uri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.addContent.priviewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.addContent.progressBar.visibility = View.VISIBLE
        }else{
            binding.addContent.progressBar.visibility = View.GONE
        }
    }
}