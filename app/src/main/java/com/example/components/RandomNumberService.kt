package com.example.components

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import java.util.*

class RandomNumberService : Service() {

    private var mHandler: Handler? = null
    private lateinit var mRunnable: Runnable
    private var count = 0
    private var stateMess = ""

    // Binder given to clients
    private val mBinder: IBinder = MyLocalBinder()

    // Random number generator
    private val mGenerator = Random()

    inner class MyLocalBinder : Binder() {
        fun getService() : RandomNumberService = this@RandomNumberService // Return this instance of LocalService so clients can call public methods
    }

    // Если служба запущена путем вызова метода startService(), то необходимо явным образом остановить службу, вызвав метод stopSelf() или stopService()
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        stateMess = "Service started."
        toast(stateMess)

        mRunnable = Runnable { showRandomNumber() }
        mHandler = Handler()
        mHandler?.postDelayed(mRunnable, 5000)

        return START_REDELIVER_INTENT // START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        stateMess = "A client is binding to the service with bindService()"
        toast(stateMess)
        return mBinder // A client is binding to the service with bindService()
    }// методы stopService() и stopSelf() фактически не останавливают службу,
    // пока не будет отменена привязка всех клиентов.

    override fun onUnbind(intent: Intent): Boolean {
        stateMess = "All clients have unbound with unbindService()"
        toast(stateMess)
        return true // All clients have unbound with unbindService()
    }

    override fun onRebind(intent: Intent){
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        stateMess = "Service onRebind()"
        toast(stateMess)
    }

    override fun onDestroy() {
        super.onDestroy()
        stateMess = "Service onDestroy()"
        toast(stateMess)
        mHandler?.removeCallbacks(mRunnable)
    }

    /** method for clients  */
    fun getCount(): Int = count

    private fun showRandomNumber() {
        if (count > 10){
            stopSelf() // serviсe останавливает себя, если нет подписчиков
        }
        count++
        val number = mGenerator.nextInt(100)
        toast("Random Number : $number")
        mHandler?.postDelayed(mRunnable, 5000)
    }
}