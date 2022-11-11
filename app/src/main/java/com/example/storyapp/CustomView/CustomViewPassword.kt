package com.example.storyapp.CustomView

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomViewPassword : AppCompatEditText, View.OnTouchListener {
    var userPasswordIsValid: Boolean = false
    private lateinit var passwordUserIcon: Drawable


    constructor(customContext: Context) : super(customContext) {
       password()
    }

    constructor(customContext: Context, passwordUserAttribute: AttributeSet) : super(customContext, passwordUserAttribute) {
        password()
    }

    constructor(customContext: Context, passwordUserAttribute: AttributeSet,selectStyleAttribute: Int) : super(
        customContext,
        passwordUserAttribute,
        selectStyleAttribute,
    ) {
        password()
    }

    private fun password() {
        passwordUserIcon= ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
        transformationMethod = PasswordTransformationMethod.getInstance()
        showPasswordUserVisibility(passwordUserIcon)
    }

    private fun showPasswordUserVisibility(passwordIcon: Drawable) {
        setButtonDrawables(startingPasswordUser = passwordIcon)
    }

    private fun setButtonDrawables(
        startingPasswordUser: Drawable? = null,
        passwordUserTop: Drawable? = null,
        endingPasswordUser: Drawable? = null,
        passwordUserBottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startingPasswordUser,
            passwordUserTop,
            endingPasswordUser,
            passwordUserBottom
        )

    }

    private fun checkPasswordUser() {
        val password = text?.toString()?.trim()
        when {
            password.isNullOrEmpty() -> {
                userPasswordIsValid = false
                error = resources.getString(R.string.password_is_empty)
            }
            password.length < 6 -> {
                userPasswordIsValid = false
                error = resources.getString(R.string.password_length)
            }
            else -> {
                userPasswordIsValid = true
            }
        }
    }

    override fun onFocusChanged(focuse: Boolean, directionFocus: Int, previousFocus: Rect?) {
        super.onFocusChanged(focuse, directionFocus, previousFocus)
        if (!focuse) checkPasswordUser()
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        return false
    }
}