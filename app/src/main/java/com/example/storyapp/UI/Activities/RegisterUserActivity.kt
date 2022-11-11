package com.example.storyapp.UI

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Model.LoginRequest
import com.example.storyapp.Model.RequestRegister
import com.example.storyapp.Preference.ListStoryRepositoryPrefernces
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.R
import com.example.storyapp.UI.ViewModel.*
import com.example.storyapp.databinding.ActivityRegisterUserBinding



class RegisterUserActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterUserBinding
    private val registerViewModel: RegisterUserViewModel by viewModels{
        ListStoryRepositoryPrefernces(this)
    }
    private val loginUserViewModel: LoginUserViewModel by viewModels{
        ListStoryRepositoryPrefernces(this)
    }
    lateinit var userName: String
    private lateinit var email: String
    private lateinit var password: String
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "register")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        clickCreateAccountUser()
        animationIsActive()
        observation()
        preferenceObserve()
        showUserPassword()
        setThemeView()
        messageAccount()
        loadingForCreated()
    }

    private fun setThemeView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private  fun preferenceObserve(){

        val preferences = StoryUserPreference.getAccountUser(data)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]

        storyUserViewModel.getUserLoginAuth().observe(this) { create ->
            if (create) {
                val createUser =
                    Intent(this@RegisterUserActivity, ListStoryUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(createUser)
                finish()
            } else {
                storyUserViewModel.saveTokenAuth("")
                storyUserViewModel.saveUserName("")
                welcomeRegister()
            }

            loginUserViewModel.accountUser.observe(this) {
                storyUserViewModel.saveUserLoginAuth(true)
                storyUserViewModel.saveTokenAuth(it.loginResult.token)
                storyUserViewModel.saveUserName(it.loginResult.name)
            }
        }

    }

    private fun  welcomeRegister(){
        Toast.makeText(
            this,
            getString(R.string.walcome_to_register_page),
            Toast.LENGTH_SHORT
        )
            .show()
    }









    private  fun observation(){
        registerViewModel.loadingIsActive.observe(this) {
            loadingIsActive(it)
        }

    }
    private fun  loadingForCreated(){
        loginUserViewModel.loadingIsActive.observe(this) {
            loadingIsActive(it)
        }
    }

    private  fun messageAccount(){
        registerViewModel.messages.observe(this) {
            checkResponseAccountUser(it)
        }
    }

    private fun clickCreateAccountUser() {
        registerBinding.btnRegister.setOnClickListener {
            registerBinding.apply {
                edEmailRegister.clearFocus()
                edNameRegister.clearFocus()
                edPasswordRegister.clearFocus()
            }
            checkAccountUser()

        }
    }

    private  fun checkAccountUser(){
        if (dataIsValid()) {
            userName = registerBinding.edNameRegister.text.toString().trim()
            email = registerBinding.edEmailRegister.text.toString().trim()
            password = registerBinding.edPasswordRegister.text.toString().trim()
            val register = RequestRegister(
                userName,
                email,
                password
            )
            registerViewModel.createAccountUser(register)
        } else {
            showErrorMessage()
        }
    }



    private  fun showUserPassword(){
        registerBinding.cbSeePassword.setOnClickListener {
            if (registerBinding.cbSeePassword.isChecked) {
                hideMyPassword()
            } else {
                showMyPassword()
            }
        }
    }

    private  fun hideMyPassword(){
        registerBinding.edPasswordRegister.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private  fun showMyPassword(){
        registerBinding.edPasswordRegister.transformationMethod = PasswordTransformationMethod.getInstance()
    }





    private fun checkResponseAccountUser(message: String) {
        if (message == "User created") {
           userWasCreated()
            val created = LoginRequest(
                email,
                password
            )
            loginUserViewModel.getLoginAccountUser(created)
        } else {
            when (message) {
                "Bad Request" -> {
                    showBadRequest(message)
                    registerBinding.edEmailRegister.apply {
                        setText("")
                        requestFocus()
                    }
                }
                "timeout" -> {
                 showTimeOut(message)
                }
                else -> {
                  showErrorMessage()
                }
            }
        }
    }

    private  fun userWasCreated(){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.information))
            setMessage(getString(R.string.the_user_created))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->
                val intent = Intent(this@RegisterUserActivity, ListStoryUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
    private fun showBadRequest(message: String){

        Toast.makeText(this, "${getString(R.string.email_already)},$message", Toast.LENGTH_LONG).show()

    }

    private  fun showErrorMessage(){
        Toast.makeText(this, getString(R.string.timeout_time), Toast.LENGTH_LONG)
            .show()
    }



    private fun showTimeOut(message: String){
        Toast.makeText(this, "${getString(R.string.timeout_time)} $message", Toast.LENGTH_LONG).show()
    }

    private fun dataIsValid(): Boolean {
        return registerBinding.edNameRegister.userNameIsValid && registerBinding.edEmailRegister.userEmailIsValid &&
                registerBinding.edPasswordRegister.userPasswordIsValid
    }


    private fun loadingIsActive(loadingIsActive: Boolean) {
        registerBinding.loadingProcessing.visibility =
            if (loadingIsActive){
            View.VISIBLE
        } else{
            View.GONE
        }
    }


    private fun animationIsActive() {
        val titleRegister = ObjectAnimator.ofFloat(registerBinding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val nameRegister = ObjectAnimator.ofFloat(registerBinding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tilName, View.ALPHA, 1f).setDuration(500)
        val emailTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayoutRegister = ObjectAnimator.ofFloat(registerBinding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayoutRegister = ObjectAnimator.ofFloat(registerBinding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val seePassword= ObjectAnimator.ofFloat(registerBinding.cbSeePassword, View.ALPHA, 1f).setDuration(500)
        val buttonRegister = ObjectAnimator.ofFloat(registerBinding.btnRegister, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(
                titleRegister,
                nameRegister,
                nameTextViewRegister,
                emailTextViewRegister,
                emailEditTextLayoutRegister,
                passwordTextViewRegister,
                passwordEditTextLayoutRegister,
                seePassword,
                buttonRegister,
            )
            startDelay = 500
        }.start()
    }


}