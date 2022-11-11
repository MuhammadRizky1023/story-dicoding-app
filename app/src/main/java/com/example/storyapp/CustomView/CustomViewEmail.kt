package com.example.storyapp.CustomView

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomViewEmail : AppCompatEditText, View.OnTouchListener {
    var userEmailIsValid: Boolean = false
    private lateinit var emailUserIcon: Drawable

    constructor(customContext: Context) : super(customContext) {
       email()
    }

    constructor(customContext: Context, emailAttribute: AttributeSet) : super(customContext, emailAttribute) {
        email()
    }

    constructor(customContext: Context, emailAttribute: AttributeSet, selectStyleAttribute: Int) : super(
        customContext,
        emailAttribute,
        selectStyleAttribute
    ) {
      email()
    }

    private fun email() {
        emailUserIcon = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        showEmailIconVisibility(emailUserIcon)
    }

    private fun showEmailIconVisibility(iconEmailUserDrawable: Drawable) {
       settingEmailUserButton(startingEmailUserDrawable =  iconEmailUserDrawable)
    }

    private fun settingEmailUserButton(
        startingEmailUserDrawable: Drawable? = null,
        emailUserDrawableTop: Drawable? = null,
        endingEmailUserDrawable: Drawable? = null,
        bottomEmailUserDrawable: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startingEmailUserDrawable,
            emailUserDrawableTop,
            endingEmailUserDrawable,
            bottomEmailUserDrawable
        )

    }

    private fun checkEmailUserAddress() {
        val emailUserAddress = text?.trim()
        if (emailUserAddress.isNullOrEmpty()) {
            userEmailIsValid = false
            error = resources.getString(R.string.input_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUserAddress).matches()) {
            userEmailIsValid = false
            error = resources.getString(R.string.valid_email_address)
        } else {
            userEmailIsValid = true
        }
    }

    override fun onFocusChanged(emailUserFocuse: Boolean, emailUserDirect: Int, emailUserPreviouslyFocus: Rect?) {
        super.onFocusChanged(emailUserFocuse, emailUserDirect, emailUserPreviouslyFocus)
        if (!emailUserFocuse) checkEmailUserAddress()
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        return false
    }

}