package br.com.btctrader.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import br.com.trader.app.model.Ticker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.content_grafico.*
import java.util.*

class GraficoActivity : AppCompatActivity() {


    val qtdRegistrosGrafico = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        montarGrafico()
    }


    fun montarGrafico(){

        val qtdRegistros = SugarRecord.count<Ticker>(Ticker::class.java)

        var lstTicker : List<Ticker>

        Log.d("QTD_REGISTROS:" , "$qtdRegistros")

        if(qtdRegistros >= qtdRegistrosGrafico) {

            lstTicker = SugarRecord.listAll(Ticker::class.java)
                    .takeLast(qtdRegistrosGrafico)

        }else{
            lstTicker = SugarRecord.listAll(Ticker::class.java)
        }

        val entries = ArrayList<Entry>()

        var i =0f
        for(e in lstTicker){

            entries.add( Entry(i ,  e.preco.toFloat()))
            i++
        }


        val dataset = LineDataSet(entries, "")

        val data = LineData(dataset)

        chart.setData(data)

    }

}
