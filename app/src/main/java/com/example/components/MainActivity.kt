package com.example.components
// https://android--code.blogspot.com/2018/03/android-kotlin-service-example.html
// https://developer.android.com/guide/components/bound-services?hl=ru
// https://www.techotopia.com/index.php/Android_Local_Bound_Services_–_A_Kotlin_Example
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var myService: RandomNumberService? = null
    var isBound = false

    private val myConnection = object : ServiceConnection {
        // method will be called when the client binds successfully to the service:
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as RandomNumberService.MyLocalBinder
            myService = binder.getService()
            isBound = true
        }

        // called when the connection ends and simply sets the Boolean flag to false.
        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceClass = RandomNumberService::class.java
        val intent = Intent(this, serviceClass)

        button_start.setOnClickListener{
            if (!isServiceRunning(serviceClass)) {
                startService(intent)
            } else {
                toast("Service already running.")
            }
        }

        button_bind.setOnClickListener{
            bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
            // Третий параметр представляет собой флаг, указывающий параметры привязки.
            // Обычно им является BIND_AUTO_CREATE, создающий службу, если она уже не выполняется.
            // Другие возможные значения: BIND_DEBUG_UNBIND и BIND_NOT_FOREGROUND или 0,
            // если значение отсутствует.
        }

        buttonGetCount.setOnClickListener{
            if (isBound){
                val currentCount = myService?.getCount()
                textView.text = currentCount.toString()
            }
        }

        buttonUnbound.setOnClickListener{
            if (isBound) {
                unbindService(myConnection)
                isBound = false;
            }
        }

        button_stop.setOnClickListener{
            if (isServiceRunning(serviceClass)) {// If the service is running then stop it
                stopService(intent)// Stop the service. Не сработает, если есть подписчики
            } else {
                toast("Service already stopped.")
            }
        }

        button_stats.setOnClickListener{
            if (isServiceRunning(serviceClass)) {
                toast("Service is running.")
            } else {
                toast("Service is stopped.")
            }
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}

fun Context.toast(message:String){// Extension function to show toast message
    Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
}