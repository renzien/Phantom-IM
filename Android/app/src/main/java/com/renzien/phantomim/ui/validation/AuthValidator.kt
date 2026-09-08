package com.renzien.phantomim.ui.validation

import android.util.Patterns
import androidx.annotation.StringRes
import com.renzien.phantomim.R

object AuthValidator {

    private val usernamePattern = Regex("[A-Za-z0-9_]+")

    @StringRes
    fun validateLogin(
        email: CharSequence,
        password: CharSequence
    ): Int? {
        val cleanEmail = email.toString().trim()

        if (cleanEmail.isEmpty()) {
            return R.string.auth_error_email_required
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return R.string.auth_error_email_invalid
        }

        if (password.isEmpty()) {
            return R.string.auth_error_password_required
        }

        return null
    }

    @StringRes
    fun validateSignUp(
        username: CharSequence,
        email: CharSequence,
        password: CharSequence
    ): Int? {
        val cleanUsername = username.toString().trim()

        if (cleanUsername.isEmpty()) {
            return R.string.auth_error_username_required
        }

        if (cleanUsername.length !in 3..20) {
            return R.string.auth_error_username_length
        }

        if (!usernamePattern.matches(cleanUsername)) {
            return R.string.auth_error_username_characters
        }

        // Reuse the email and empty-password checks.
        val loginError = validateLogin(
            email = email,
            password = password
        )

        if (loginError != null) {
            return loginError
        }

        if (password.isBlank()) {
            return R.string.auth_error_password_required
        }

        // Count Unicode code points without changing the password.
        val passwordLength = Character.codePointCount(
            password,
            0,
            password.length
        )

        if (passwordLength !in 15..128) {
            return R.string.auth_error_password_length
        }

        return null
    }
}