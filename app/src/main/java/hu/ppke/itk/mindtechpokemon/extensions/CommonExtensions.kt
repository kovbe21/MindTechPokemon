package hu.ppke.itk.mindtechpokemon.extensions

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnack(message: String, length: Int = Snackbar.LENGTH_LONG): Snackbar? {
    return view?.let { showSnack(it, message, length) }
}

fun Fragment.showSnack(@StringRes messageId: Int, length: Int = Snackbar.LENGTH_LONG): Snackbar? {
    return view?.let { showSnack(it, null, messageId, length) }
}

fun showSnack(view: View, message: String? = null, @StringRes messageId: Int? = null, length: Int = Snackbar.LENGTH_LONG): Snackbar? {
    val snack = message?.let { Snackbar.make(view, message, length) }
        ?: messageId?.let { Snackbar.make(view, messageId, length) }
        ?: return null
    snack.show()
    return snack
}