package br.com.trader.app.Extensions

import android.app.Activity
import android.util.Log

/**
 * Created by kassianoresende on 26/10/17.
 */
class GlobalExtensions{

    fun Activity.print(s:String){
        Log.d("PRINT:", s)
    }

    fun String.lala() : String{

        return "lala"
    }


}