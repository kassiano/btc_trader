package br.com.btctrader.app

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.trader.app.model.Ticker
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val API_URL = "https://api.bitvalor.com/v1/ticker.json"

    var ultimo_preco_btc: Double = 0.0
    var saldoAtual = 0.0

    lateinit var preferences: SharedPreferences
    lateinit var adapter : ArrayAdapter<Ticker>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val qtd = preferences.getString("qtd_btc", "")
        val valor_investido = preferences.getString("valor_investido", "")

        if (!qtd!!.isEmpty()) {
            txt_qtd_btc.setText(qtd)
        }

        if (!valor_investido!!.isEmpty()) {
            txt_valor_investido.setText(valor_investido.toString())
        }

        atualizarCotacao()

        fab.setOnClickListener{
            atualizarCotacao()
        }

        //adapter = ArrayAdapter<Ticker>(this, android.R.layout.simple_list_item_1);
        //list_view.adapter =  adapter

        Log.d("BTC", txt_qtd_btc.text.toString());
        Log.d("BTC", txt_valor_investido.text.toString());

       // txt_qtd_btc.setText("0.08462991")
       // txt_valor_investido.setText("1500")


    }


    fun atualizarCotacao() {


        if(!isNetworkAvailable()){
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
            return
        }


        linear_saldo.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        doAsync {

            val response  = HttpConnection.get(API_URL)

            uiThread {

                linear_saldo.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE

                atualizarPrecoVenda(response)
                atualizarSaldo()
                atualizarValorizacao()
                atualizarLucroLiquido()
               // atualizarLista()
            }
        }

    }

    private fun atualizarValorizacao() {

        if(txt_valor_investido.text.toString().isEmpty()){
            return
        }

        val invest_inicial = txt_valor_investido.text.toString().toDouble()

        val lucro = saldoAtual - invest_inicial

        val porc = lucro / invest_inicial


        val f = NumberFormat.getPercentInstance()
        f.minimumFractionDigits = 2

        txt_valorizacao.text =  f.format(porc)

    }

    private fun atualizarSaldo() {

        if (!txt_qtd_btc.text.toString().isEmpty()) {

            val btc = txt_qtd_btc.text.toString().toDouble()

            saldoAtual = btc * ultimo_preco_btc

            txt_saldo_projetado.text = convertToMoney(saldoAtual)

            preferences.edit().putString("qtd_btc", btc.toString()).apply()
        }


        if (!txt_valor_investido.text.toString().isEmpty()) {


            preferences.edit().putString("valor_investido",
                    txt_valor_investido.text.toString()).apply()

        }


    }


    fun atualizarPrecoVenda(response: String) {
        try {

            val fox = JSONObject(response)
                    .getJSONObject("ticker_1h")
                    .getJSONObject("exchanges")
                    .getJSONObject("FOX")

            ultimo_preco_btc = fox.getDouble("last")

            txt_preco_venda.text = "Valor de venda: ${convertToMoney(ultimo_preco_btc)}"


            val t = Ticker()
            t.preco = ultimo_preco_btc
            t.data = getDate()
            //t.save()

        } catch (e: Exception) {
            e.printStackTrace()

            Toast.makeText(this, "Problemas na conexão", Toast.LENGTH_SHORT).show()
        }

    }


    fun atualizarLucroLiquido(){

        if(txt_valor_investido.text.toString().isEmpty())
            return

        val valor_investido = txt_valor_investido.text.toString().toDouble()

        val lucro = saldoAtual - valor_investido

        txt_lucro_liquido.text = convertToMoney(lucro)

    }


    fun convertToMoney(valor: Double?): String {
        val f = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return f.format(valor)
    }

    fun atualizarLista(){

        val lstTicker = SugarRecord.listAll(Ticker::class.java);

        Log.d("PRINT", lstTicker.size.toString() )

        adapter.clear()
        adapter.addAll(lstTicker)

    }


    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.action_graficos){

            //startActivity(Intent(this, GraficoActivity::class.java))

            startActivity<GraficoActivity>()
        }

        return super.onOptionsItemSelected(item)
    }


    fun getDate() : Date{
        return Calendar.getInstance().time
    }

}
