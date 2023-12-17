package id.ac.unri.storyapp.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import id.ac.unri.storyapp.R

class NameEditText : AppCompatEditText {

    private lateinit var personIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init(){
        personIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        compoundDrawablePadding = 16

        setHint(resources.getString(R.string.hint_username))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_USERNAME)
        }

        setDrawable(personIconDrawable)
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Validasi Username
                if(!s.isNullOrEmpty() && s.length<2)
                    error = resources.getString(R.string.username_error_message)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }

}