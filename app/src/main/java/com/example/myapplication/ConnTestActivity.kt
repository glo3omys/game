package com.example.myapplication

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityConnTestBinding

class ConnTestActivity : AppCompatActivity() {

    private var mBinding: ActivityConnTestBinding? = null
    private val binding get() = mBinding!!

    lateinit var myServer: Server
    lateinit var myClient: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conn_test)

        mBinding = ActivityConnTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //myServer = Server()

        binding.btnServerGen.setOnClickListener() {
            //myServer = Server()
            val startServer = StartServer()
            startServer.start()
        }
        binding.btnServerConn.setOnClickListener() {
            //myClient = Client()
            val startClient = StartClient()
            startClient.start()
        }
    }

    inner class StartServer : Thread() {
        override fun run() {
            myServer = Server()
        }
    }
    inner class StartClient : Thread() {
        override fun run() {
            myClient = Client()
        }
    }
}
