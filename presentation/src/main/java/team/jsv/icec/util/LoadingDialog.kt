package team.jsv.icec.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import team.jsv.presentation.R
import team.jsv.util_kotlin.IcecClientException

class LoadingDialog(context: Context) : Dialog(context){

    companion object {
        const val errorMessage = "Window Error"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)

        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) ?: throw IcecClientException(message = errorMessage)

    }

}