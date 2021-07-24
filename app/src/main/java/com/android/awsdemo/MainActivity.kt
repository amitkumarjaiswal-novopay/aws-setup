package com.android.awsdemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var mBound = false
    private var mService: AwsService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!this.mBound || this.mService == null || mService!!.shouldConnect()) {
            val intent = AwsService.startMqttService(this)
            bindService(intent, this.mConnection, BIND_IMPORTANT)
        }

        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
            mService?.sendMessage();

        })
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: AwsService.LocalBinder = service as AwsService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

}