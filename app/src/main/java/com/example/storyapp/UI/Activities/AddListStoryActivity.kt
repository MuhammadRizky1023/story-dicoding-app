package com.example.storyapp.UI

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import com.example.storyapp.Helper.LocationConvertData
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.UI.Activities.LocationUserActivity
import com.example.storyapp.UI.ViewModel.AddListStoryUserViewModel
import com.example.storyapp.Preference.ListStoryRepositoryPrefernces
import com.example.storyapp.UI.Activities.MapUserActivity
import com.example.storyapp.databinding.ActivityAddListStoryBinding
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class AddListStoryActivity() : AppCompatActivity() {
    private var isEnding = false
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "addStory")
    private lateinit var addStoryBinding: ActivityAddListStoryBinding
    private var uploadedFile: File? = null
    private  val addStoryViewModel: AddListStoryUserViewModel by viewModels{
        ListStoryRepositoryPrefernces(this)
    }
    private  lateinit var  token: String
    private var whateverImage= false
    private lateinit var actualPhotoRoute: String
    private var locationUser: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list_story)
        addStoryBinding = ActivityAddListStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)
        observation()
        preferences()
        locationUser()
        getLocationFromMap()
        clickAddStory()
        clickPhoto()
    }

    private  fun observation(){
        addStoryViewModel.loadingIsActive.observe(this){
            showLoading(it)
        }
        addStoryViewModel.messages.observe(this){
            showToast(it)
        }
    }

    private val locationUserData = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { upload: ActivityResult? ->
        if (upload?.resultCode == Activity.RESULT_OK) {

            val latitude = upload.data?.getDoubleExtra(LocationUserActivity.LATITUDE, 1.1)
            val longitude = upload.data?.getDoubleExtra(LocationUserActivity.LONGITUDE, 1.1)
            if (latitude != null && longitude != null) {
                locationUser= LatLng(latitude, longitude)
                addStoryBinding.tvLocation.text = LocationConvertData.getLocationConvert(locationUser, this)
            } else {
                locationNoFound()
            }
        }
    }

    private  fun locationUser(){
        addStoryBinding.llAddLocation.setOnClickListener {
            val intent = Intent(this, LocationUserActivity::class.java)
            locationUserData.launch(intent)
        }
    }

    private  fun  clickAddStory(){
        addStoryBinding.btnAdd.setOnClickListener {
            userUploadImageFile()
        }
    }

    private  fun  clickPhoto(){
        addStoryBinding.ivAddPhoto.setOnClickListener {
            if (!grantedAllPermissions()){
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRE_PERMISSION_UPLOAD_FILE,
                    REQUEST_PERMISSIONS_UPLOAD_FILE
                )
            }
            dialogUserSelected()
        }
    }


    private  fun preferences(){
        val preferences = StoryUserPreference.getAccountUser(dataStore)
        val userStoryPreferences = ViewModelProvider(this,
            ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]

        userStoryPreferences.getTokenAuth().observe(this){
            token = it
        }
        userStoryPreferences.getUserName().observe(this){
            addStoryBinding.tvName.text = StringBuilder(getString(R.string.post_as)).append(" ").append(it)
        }
    }

    private fun urlUploaded(selectImage: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val fileImage = buildCustomImageFile(context)

        val storageInputStream = contentResolver.openInputStream(selectImage) as InputStream
        val storageOutputStream: OutputStream = FileOutputStream(fileImage)
        val buffering = ByteArray(1024)
        var int: Int
        while (storageInputStream.read(buffering).also { int = it } > 0) storageOutputStream.write(buffering, 0, int)
        storageInputStream.close()
        storageOutputStream.close()

        return fileImage
    }

    private val launchingIntentGalleryFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectImage: Uri = result.data?.data as Uri
            val fileImage = urlUploaded(selectImage, this@AddListStoryActivity)
              uploadedFile = fileImage
            addStoryBinding.ivAddPhoto.setImageURI(selectImage)
            addStoryBinding.edAddDesc.requestFocus()
        }
    }

    private fun playGallery() {
        val gallery = Intent()
        gallery.action = Intent.ACTION_GET_CONTENT
        gallery.type = "image/*"
        val chooser = Intent.createChooser(gallery, getString(R.string.choose_picture))
        launchingIntentGalleryFile.launch(chooser)
    }



    private val launchingIntentCameraFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val imageFile = File(actualPhotoRoute)
            uploadedFile = imageFile
            val uploaded = BitmapFactory.decodeFile(imageFile.path)
            whateverImage = true
            addStoryBinding.ivAddPhoto.setImageBitmap(uploaded)
            addStoryBinding.edAddDesc.requestFocus()
        }
    }
    private val stopWatch: String = SimpleDateFormat(
        FILE_IMAGE_FORMAT_DATE,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun buildCustomImageFile(context: Context): File {
        val storageFile: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(stopWatch, ".jpg", storageFile)
    }

    private fun playTakePhoto() {
        val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhoto.resolveActivity(packageManager)
        buildCustomImageFile(application).also {
            val urlFilePhoto: Uri = FileProvider.getUriForFile(
                this@AddListStoryActivity,
                getString(R.string.package_name),
                it
            )
            actualPhotoRoute = it.absolutePath
            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, urlFilePhoto)
            launchingIntentCameraFile.launch(takePhoto)
        }
    }




    private fun userUploadImageFile() {
        val description = addStoryBinding.edAddDesc.text.toString()
        when {
            uploadedFile == null -> {
                Toast.makeText(
                    this@AddListStoryActivity,
                    getString(R.string.input_picture),
                    Toast.LENGTH_SHORT
                ).show()

            }
            description.trim().isEmpty() -> {
                Toast.makeText(
                    this@AddListStoryActivity,
                    getString(R.string.input_desc),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val fileType = uploadedFile as File
                val desc = description.toRequestBody("text/plain".toMediaType())
                val requestImageFileType = fileType.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartImage: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    fileType.name,
                    requestImageFileType
                )
                addStoryViewModel.uploadStory(multipartImage, desc, token, locationUser?.latitude, locationUser?.longitude)

            }
        }


    }

    private fun showToast(message: String) {
        if (message == "Story created successfully") {
            successUploadStory()
            finish()
        } else {
            showMessage(message)
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(
            this@AddListStoryActivity,
            StringBuilder(getString(R.string.message)).append(message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private  fun successUploadStory(){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.information))
            setMessage(getString(R.string.upload_successfully))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->
                val intent = Intent(this@AddListStoryActivity, ListStoryUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }


    private  fun showLoading(loadingIsActive: Boolean){
        addStoryBinding.loadingProcess.visibility = if (loadingIsActive) View.VISIBLE else View.GONE
    }


    private fun  dialogUserSelected() {
        val subjects = arrayOf<CharSequence>(

            getString(R.string.from_gallery),
            getString(R.string.take_a_picture),
            getString(R.string.cancel)
        )

        val header= TextView(this)
        header.text = getString(R.string.select_photo)
        header.gravity = Gravity.CENTER
        header.setPadding(10, 15, 15, 10)
        header.setTextColor(resources.getColor(R.color.dark_blue, theme))
        header.textSize = 22f
        val maker = android.app.AlertDialog.Builder(
            this
        )
        maker.setCustomTitle(header)
        maker.setItems(subjects) { dialog, subject ->
            when {
                subjects[subject] == getString(R.string.from_gallery) -> {
                    playGallery()

                }
                subjects[subject] == getString(R.string.take_a_picture) -> {
                    playTakePhoto()

                }
                subjects[subject] == getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        maker.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_UPLOAD_FILE){
            if (!grantedAllPermissions()){
                requestCode()
            }
        }
    }

    private fun  requestCode(){
        Toast.makeText(this, "No have permission",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private  fun  grantedAllPermissions() =  REQUIRE_PERMISSION_UPLOAD_FILE.all{
        checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun locationNoFound(){
        Toast.makeText(this, getString(R.string.no_have_location), Toast.LENGTH_SHORT).show()
    }

    private  fun  getLocationFromMap(){
        val latitude = intent.getFloatExtra(LATITUDE, 10000f)
        val longitude = intent.getFloatExtra(LONGITUDE, 10000f)

        if (latitude != 10000f && longitude != 10000f){
            val myLocation = "your latitude = $latitude \n your Longitude = $longitude"
            addStoryBinding.tvLocation.text = myLocation
        }
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(subjectItem: MenuItem): Boolean {
        return  when(subjectItem.itemId) {
            R.id.language_menu -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }
            R.id.logout_menu -> {
                logOutAlertDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_map -> {
                goToMap()
                true
            }


            else -> true
        }
    }

    private fun  goToMap(){
        val mapActivity = Intent(this@AddListStoryActivity, MapUserActivity::class.java)
        startActivity(mapActivity)
    }

    private  fun logOutAlertDialog(){
        val maker = AlertDialog.Builder(this)
        val dialog= maker.create()
        maker
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure_to_logout))
            .setPositiveButton(getString(R.string.no)){_, _ ->
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.yes)){_, _ ->
                userLogOutNow()
            }
            .show()
    }

    private  fun userLogOutNow(){
        val preferences = StoryUserPreference.getAccountUser(dataStore)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]
        storyUserViewModel.apply {
            saveUserLoginAuth(false)
            saveTokenAuth("")
            saveUserName("")
        }
        isEnding= true
        val logOut = Intent(this@AddListStoryActivity, LoginUserActivity::class.java)
        startActivity(logOut)
        finish()
    }
    companion object {
        const val   FILE_IMAGE_FORMAT_DATE = "yyyyMMdd"
        private  val REQUIRE_PERMISSION_UPLOAD_FILE = arrayOf(Manifest.permission.CAMERA)
        private const  val REQUEST_PERMISSIONS_UPLOAD_FILE = 5
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
    }
}