package com.sabiantools.controls.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.sabiantools.controls.texts.SabianCondensedEditText

inline fun EditText.onTextChanged(
    crossinline listener: (text: CharSequence?) -> Unit
) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) = Unit

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            listener(s)
        }

        override fun afterTextChanged(s: Editable?) = Unit
    }

    if (this is SabianCondensedEditText) {
        setTextChangeListener(watcher)
    } else {
        addTextChangedListener(watcher)
    }
}