package mushi.taibai

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var a: String? = null
        a ?: kotlin.run {
            a = "ssss"
//            return
        }

        var b = true
        a?.takeIf { b }?.let { Log.d("log", a) }
    }
}
