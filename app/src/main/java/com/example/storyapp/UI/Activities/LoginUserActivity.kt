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
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.ViewModel.LoginUserViewModel
import com.example.storyapp.Preference.ListStoryRepositoryPrefernces
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.Preference.ViewModelAccountPreferences
import com.example.storyapp.databinding.ActivityLoginUserBinding

class LoginUserActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginUserBinding
    private val loginViewModel: LoginUserViewModel  by viewModels{
        ListStoryRepositoryPrefernces(this)
    }
    private val Context.data: DataStore<Preferences> by preferencesDataStore(name = "login")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            loginBinding= ActivityLoginUserBinding.inflate(layoutInflater)
            setContentView(loginBinding.root)
            setLoginNow()
            animationIsActive()
            setThemeView()
            preferenceObserve()
            showPassword()
            toRegister()
            loadingSuccessLogin()

        }
        private  fun preferenceObserve(){
            val preferences = StoryUserPreference.getAccountUser(data)
            val storyUserViewModel =
                ViewModelProvider(this, ViewModelAccountPreferences(preferences))[StoryUserViewModel::class.java]
            storyUserViewModel.getUserLoginAuth().observe(this) { success ->
                if (success) {
                    val goToList = Intent(this, ListStoryUserActivity::class.java)
                    startActivity(goToList)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.walcome_to_login_page),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            loginViewModel.message.observe(this) {
                val account = loginViewModel.accountUser.value
                checkResponseLoginUser(it, account?.loginResult?.token, storyUserViewModel)
            }
        }

        private  fun  loadingSuccessLogin(){
            loginViewModel.loadingIsActive.observe(this) {
                loadingIsActive(it)
            }
        }


        private fun setLoginNow() {
            loginBinding.btnLogin.setOnClickListener {
                loginBinding.edEmailLogin.clearFocus()
                loginBinding.edPasswordLogin.clearFocus()
                checkUserData()
            }
        }


       private fun checkUserData(){
           if (dataUserIsValid()) {
             dataVerify()
           }
       }

        private  fun dataVerify(){
            val userLogin = LoginRequest(
                loginBinding.edEmailLogin.text.toString().trim(),
                loginBinding.edPasswordLogin.text.toString().trim()
            )
            loginViewModel.getLoginAccountUser(userLogin)
        }

           private fun toRegister(){
               loginBinding.tvRegister.setOnClickListener {
                   val intent = Intent(this, RegisterUserActivity::class.java)
                   startActivity(intent)
               }
           }

           private  fun showPassword(){
               loginBinding.cbSeePassword.setOnClickListener {
                   if (loginBinding.cbSeePassword.isChecked) {
                       loginBinding.edPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
                   } else {
                       loginBinding.edPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
                   }
               }
           }

        private fun dataUserIsValid(): Boolean {
            return loginBinding.edEmailLogin.userEmailIsValid &&  loginBinding.edPasswordLogin.userPasswordIsValid
        }

        private fun checkResponseLoginUser(
            message: String,
            token: String?,
            viewModel: StoryUserViewModel,
        ) {
            if (message.contains("Login as")) {
                loginSuccessfully(message)
                viewModel.saveUserLoginAuth(true)
                if (token != null) {
                    viewModel.saveTokenAuth(token)
                }
                viewModel.saveUserName(loginViewModel.accountUser.value?.loginResult?.name.toString())
            } else {
                when (message) {
                    "unauthorized" -> {
                        unauthorized()
                        loginBinding.edEmailLogin.apply {
                            setText("")
                            requestFocus()
                        }
                        loginBinding.edPasswordLogin.setText("")

                    }
                    "timeout" -> {
                       timeOut()
                    }
                    else -> {
                       errorMessage(message)
                    }
                }
            }
        }

       private  fun loginSuccessfully(message: String){
           AlertDialog.Builder(this).apply {
               setTitle(getString(R.string.information))
               setMessage("${getString(R.string.login_successfully)} $message")
               setPositiveButton(getString(R.string.continue_)) { _, _ ->
                   val intent = Intent(this@LoginUserActivity, ListStoryUserActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                   startActivity(intent)
                   finish()
               }
               create()
               show()
           }
       }

        private  fun unauthorized(){
            Toast.makeText(this, getString(R.string.this_account_unauthorized), Toast.LENGTH_SHORT)
                .show()

        }

        private  fun errorMessage(massage: String){
            Toast.makeText(
                this,
                "${getString(R.string.error_message)} $massage",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        private  fun timeOut(){
            Toast.makeText(this, getString(R.string.timeout_time), Toast.LENGTH_SHORT)
                .show()
        }



        private fun loadingIsActive(loadingIsActive: Boolean) {
            loginBinding.loadingProcessing.visibility = if (loadingIsActive) View.VISIBLE else View.GONE
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

        private fun animationIsActive() {
            val titleLogin = ObjectAnimator.ofFloat(loginBinding.tvTitleLogin, View.ALPHA, 1f).setDuration(500)
            val emailTextViewLogin = ObjectAnimator.ofFloat(loginBinding.tvEmail, View.ALPHA, 1f).setDuration(500)
            val emailEditTextLayoutLogin = ObjectAnimator.ofFloat(loginBinding.tilEmail, View.ALPHA, 1f).setDuration(500)
            val passwordTextViewLogin = ObjectAnimator.ofFloat(loginBinding.tvPassword, View.ALPHA, 1f).setDuration(500)
            val passwordEditTextLayoutLogin = ObjectAnimator.ofFloat(loginBinding.tilPassword, View.ALPHA, 1f).setDuration(500)
            val seePassword= ObjectAnimator.ofFloat(loginBinding.cbSeePassword, View.ALPHA, 1f).setDuration(500)
            val buttonLogin = ObjectAnimator.ofFloat(loginBinding.btnLogin, View.ALPHA, 1f).setDuration(500)
            val donTHaveAnAccount= ObjectAnimator.ofFloat(loginBinding.tvDonTHaveAnAccount, View.ALPHA, 1f).setDuration(500)
            val textViewRegister= ObjectAnimator.ofFloat(loginBinding.tvRegister, View.ALPHA, 1f).setDuration(500)
            AnimatorSet().apply {
                playSequentially(
                    titleLogin,
                    emailTextViewLogin,
                    emailEditTextLayoutLogin,
                    passwordTextViewLogin,
                    passwordEditTextLayoutLogin,
                    seePassword,
                    buttonLogin,
                    donTHaveAnAccount,
                    textViewRegister
                )
                startDelay = 500
            }.start()
        }

}