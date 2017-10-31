package br.com.trader.app.model


import com.orm.SugarRecord
import java.text.NumberFormat
import java.util.*

/**
 * Created by kassianoresende on 21/10/17.
 */
class Ticker() : SugarRecord(){

    var preco : Double = 0.0
    var data: Date? =null

    override fun toString(): String {
        val f = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return f.format(preco)
    }
}