package com.example.mystories.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.mystories.R

class MyTextVIew : AppCompatTextView {
    private var txtColor: Int = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        setTextColor(txtColor)
        textSize = 12f
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        text = if (isVisible) context.getString(R.string.passwordEnable) else ""
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.darker_gray)
    }
}