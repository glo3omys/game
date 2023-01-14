package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityConnTestBinding
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.*
import kotlin.properties.Delegates

class ConnTestActivity : AppCompatActivity() {

    companion object{
        var socket = Socket()
        var server = ServerSocket()
        lateinit var writeSocket: DataOutputStream
        lateinit var readSocket: DataInputStream
        lateinit var cManager: ConnectivityManager
        lateinit var myIp: String

        var ip = "192.168.0.1"
        var port = 2222
        //var mHandler = Handler()      -> API30부터 Deprecated됨. Looper를 직접 명시해야함
        var mHandler = Handler(Looper.getMainLooper())
        var serverClosed = true

        var cList = mutableListOf<Client>()
    }
    private var mBinding: ActivityConnTestBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_conn_test)

        mBinding = ActivityConnTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        server.close()
        socket.close()

        binding.buttonConnect.setOnClickListener {    //클라이언트 -> 서버 접속
            if(binding.etIp.text.isNotEmpty()) {
                ip = binding.etIp.text.toString()
                myIp = binding.etName.text.toString()
                if(binding.etPort.text.isNotEmpty()) {
                    port = binding.etPort.text.toString().toInt()
                    if(port<0 || port>65535){
                        Toast.makeText(this@ConnTestActivity, "PORT 번호는 0부터 65535까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        if(!socket.isClosed){
                            Toast.makeText(this@ConnTestActivity, ip + "에 이미 연결되어 있습니다.", Toast.LENGTH_SHORT).show()
                        }else {
                            Connect().start()
                        }
                    }

                }else{
                    Toast.makeText(this@ConnTestActivity, "PORT 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@ConnTestActivity, "IP 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDisconnect.setOnClickListener {    //클라이언트 -> 서버 접속 끊기
            if(!socket.isClosed){
                Disconnect().start()
            }else{
                Toast.makeText(this@ConnTestActivity, "서버와 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSetserver.setOnClickListener{    //서버 포트 열기
            if(binding.etPort.text.isNotEmpty()) {
                val cport = binding.etPort.text.toString().toInt()
                if(cport<0 || cport>65535){
                    Toast.makeText(this@ConnTestActivity, "PORT 번호는 0부터 65535까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                }else{
                    if(server.isClosed) {
                        port = cport
                        SetServer().start()
                    }else{
                        val tstr = port.toString() + "번 포트가 열려있습니다."
                        Toast.makeText(this@ConnTestActivity, tstr, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this@ConnTestActivity, "PORT 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCloseserver.setOnClickListener {    //서버 포트 닫기
            if(!server.isClosed){
                CloseServer().start()
            }else{
                mHandler.obtainMessage(17).apply {
                    sendToTarget()
                }
            }
        }

        binding.buttonClear.setOnClickListener {    //채팅방 내용 지우기
            binding.textStatus.text = ""
        }

        binding.buttonMsg.setOnClickListener {    //상대에게 메시지 전송
            if(socket.isClosed){
                Toast.makeText(this@ConnTestActivity, "연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            }else {
                val mThread = SendMessage()
                mThread.setMsg(2, binding.etName.text.toString(), binding.etMsg.text.toString())
                mThread.start()
            }
        }

        mHandler = object : Handler(Looper.getMainLooper()){  //Thread들로부터 Handler를 통해 메시지를 수신
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    1->Toast.makeText(this@ConnTestActivity, "IP 주소가 잘못되었거나 서버의 포트가 개방되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    2->Toast.makeText(this@ConnTestActivity, "서버 포트 "+port +"가 준비되었습니다.", Toast.LENGTH_SHORT).show()
                    3->Toast.makeText(this@ConnTestActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                    4->Toast.makeText(this@ConnTestActivity, "연결이 종료되었습니다.", Toast.LENGTH_SHORT).show()
                    5->Toast.makeText(this@ConnTestActivity, "이미 사용중인 포트입니다.", Toast.LENGTH_SHORT).show()
                    6->Toast.makeText(this@ConnTestActivity, "서버 준비에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    7->Toast.makeText(this@ConnTestActivity, "서버가 종료되었습니다.", Toast.LENGTH_SHORT).show()
                    8->Toast.makeText(this@ConnTestActivity, "서버가 정상적으로 닫히는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    9-> ((binding.textStatus.text as String) + (msg.obj as String) + "\n").also { binding.textStatus.text = it }
                    11->Toast.makeText(this@ConnTestActivity, "서버에 접속하였습니다.", Toast.LENGTH_SHORT).show()
                    12->Toast.makeText(this@ConnTestActivity, "메시지 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    13->Toast.makeText(this@ConnTestActivity, (msg.obj as String) + " 클라이언트와 연결되었습니다.",Toast.LENGTH_SHORT).show()
                    14->Toast.makeText(this@ConnTestActivity,"서버에서 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                    15->Toast.makeText(this@ConnTestActivity, "서버와의 연결을 종료합니다.", Toast.LENGTH_SHORT).show()
                    16->Toast.makeText(this@ConnTestActivity, (msg.obj as String)+" 클라이이언트와의 연결을 종료합니다.", Toast.LENGTH_SHORT).show()
                    17->Toast.makeText(this@ConnTestActivity, "포트가 이미 닫혀있습니다.", Toast.LENGTH_SHORT).show()
                    18->Toast.makeText(this@ConnTestActivity, "서버와의 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show()
                    19->Toast.makeText(this@ConnTestActivity, "인터넷이 연결되지 않았습니다. 연결 후 다시 시도하세요.", Toast.LENGTH_LONG).show()
                    20->{
                        binding.etName.setText(msg.obj as String)
                        myIp = msg.obj as String
                    }
                }
            }
        }
        ShowInfo().start()
    }
    class Connect:Thread(){

        override fun run() = try{
            socket = Socket(ip, port)
            writeSocket = DataOutputStream(socket.getOutputStream())
            readSocket = DataInputStream(socket.getInputStream())
            val b = readSocket.readInt()
            if(b==1){    //서버로부터 접속이 확인되었을 때
                mHandler.obtainMessage(11).apply {
                    sendToTarget()
                }
                ClientSocket(myIp).start()
            }else{    //서버 접속에 성공하였으나 서버가 응답을 하지 않았을 때
                mHandler.obtainMessage(14).apply {
                    sendToTarget()
                }
                socket.close()
            }
        }catch(e:Exception){    //연결 실패
            val state = 1
            mHandler.obtainMessage(state).apply {
                sendToTarget()
            }
            socket.close()
        }
    }

    //클라이언트-서버 통신 개시
    class ClientSocket(private val addr: String):Thread(){

        override fun run() {
            try{
                while (true) {
                    val ac = readSocket.readInt()
                    val cname = readSocket.readUTF()

                    if( ac == 3){
                        readSocket.readUTF()
                        if(addr != cname){
                            mHandler.obtainMessage(9, "$cname 님이 입장하였습니다.").apply {
                                sendToTarget()
                            }
                        }else{
                            mHandler.obtainMessage(9, "채팅방에 입장하였습니다.").apply {
                                sendToTarget()
                            }
                        }
                    }else if(ac == 2) {    //서버로부터 메시지 수신 명령을 받았을 때
                        val bac = readSocket.readUTF()
                        val input = bac.toString()
                        val recvInput = input.trim()

                        val clientName = cname.toString().trim()

                        val msg = mHandler.obtainMessage()
                        msg.what = 9
                        msg.obj = "$clientName> $recvInput"
                        mHandler.sendMessage(msg)
                    }else if(ac == 4){
                        readSocket.readUTF()
                        if(addr != cname) {
                            mHandler.obtainMessage(9, "$cname 님이 퇴장하였습니다.").apply {
                                sendToTarget()
                            }
                        }

                    }else if(ac == 10){    //서버로부터 접속 종료 명령을 받았을 때
                        mHandler.obtainMessage(18).apply {
                            sendToTarget()
                        }
                        mHandler.obtainMessage(9,"서버에서 연결을 끊었습니다.").apply {
                            sendToTarget()
                        }
                        socket.close()
                        break
                    }
                }
            }catch(e:SocketException){    //소켓이 닫혔을 때
                mHandler.obtainMessage(15).apply {
                    sendToTarget()
                }
                mHandler.obtainMessage(9, "채팅방을 나갔습니다.").apply {
                    sendToTarget()
                }
            }
        }
    }

    //클라이언트 접속 종료
    class Disconnect:Thread(){

        override fun run() {

            try{
                writeSocket.write(10)    //서버에게 접속 종료 명령 전송
                writeSocket.writeUTF(myIp)  //종료 요청 클라이언트 주소
                socket.close()
            }catch(e:Exception){

            }
        }
    }

    //서버 통신 개시
    class SetServer:Thread(){

        override fun run(){
            try{
                server = ServerSocket(port)    //포트 개방
                mHandler.obtainMessage(2, "").apply {
                    sendToTarget()
                }
                mHandler.obtainMessage(9, "서버가 열렸습니다.").apply {
                    sendToTarget()
                }

                while(true) {
                    socket = server.accept()    //클라이언트가 접속할 때 까지 대기
                    val client = Client(socket)    //접속한 Client의 socket을 저장
                    cList.add(client)    //접속 client socket 리스트 추가
                    client.start()    //접속한 클라이언트 전용 socket thread 실행
                }

            }catch(e:BindException) {    //이미 개방된 포트를 개방하려 시도하였을때
                mHandler.obtainMessage(5).apply {
                    sendToTarget()
                }
            }catch(e:SocketException){    //소켓이 닫혔을 때
                mHandler.obtainMessage(7).apply {
                    sendToTarget()
                }
            }
            catch(e:Exception){
                if(!serverClosed) {
                    mHandler.obtainMessage(6).apply {
                        sendToTarget()
                    }
                }else{
                    serverClosed = false
                }
            }
        }
    }

    //서버 소켓 닫기
    class CloseServer:Thread(){
        override fun run(){
            try{
                if(!socket.isClosed){
                    writeSocket.write(10)    //클라이언트에게 서버가 종료되었음을 알림
                    writeSocket.close()
                    socket.close()
                }
                server.close()
                serverClosed = true
                mHandler.obtainMessage(9, "서버가 닫혔습니다.").apply {
                    sendToTarget()
                }
            }catch(e:Exception){
                e.printStackTrace()
                mHandler.obtainMessage(8).apply {
                    sendToTarget()
                }
            }
        }
    }

    //메시지 전송
    class SendMessage:Thread(){
        private var state by Delegates.notNull<Int>()
        private lateinit var msg:String
        private lateinit var cname:String

        fun setMsg(s: Int, n:String, m:String){
            state = s
            msg = m
            cname = n
        }

        override fun run() {

            if(cList.size>0){    //메시지를 전송하는 주체가 서버일 경우
                val cIter = cList.iterator()
                while(cIter.hasNext()){
                    val client = cIter.next()
                    if (!client.isClosed()) client.sendMessage(state, cname, msg)
                    else cIter.remove()
                    mHandler.obtainMessage(9, "$cname> $msg").apply {
                        sendToTarget()
                    }
                }
            }else {
                try {
                    writeSocket.writeInt(state)    //메시지 전송 명령 전송
                    writeSocket.writeUTF(cname)    //클라이언트 이름
                    writeSocket.writeUTF(msg)    //메시지 내용
                } catch (e: Exception) {
                    e.printStackTrace()
                    mHandler.obtainMessage(12).apply {
                        sendToTarget()
                    }
                }
            }
        }
    }

    //자신의 IP주소를 표시
    class ShowInfo:Thread() {

        override fun run() {
            var ip = ""
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        ip = inetAddress.hostAddress as String
                    }
                }
            }

            if (ip == "") {
                mHandler.obtainMessage(19).apply {
                    sendToTarget()
                }
            } else {
                val msg = mHandler.obtainMessage()
                msg.what = 20
                msg.obj = ip
                mHandler.sendMessage(msg)
            }
        }
    }

    //서버에 접속한 클라이언트 소켓 제어
    class Client(socket: Socket) : Thread(){
        private lateinit var clientName: String
        private lateinit var clientAddr: String
        private lateinit var cWriteSocket: DataOutputStream
        private val cSocket: Socket=socket

        override fun run(){
            cWriteSocket = DataOutputStream(cSocket.getOutputStream())
            val cReadSocket = DataInputStream(cSocket.getInputStream())

            cWriteSocket.writeInt(1)    //클라이언트에게 서버의 소켓 생성을 알림
            val socketAddr = socket.remoteSocketAddress as InetSocketAddress
            clientAddr = socketAddr.address.hostAddress as String

            mHandler.obtainMessage(13, clientAddr).apply {
                sendToTarget()
            }
            mHandler.obtainMessage(9, clientAddr + "님이 입장하였습니다.").apply {
                sendToTarget()
            }
            Broadcast(cList, 3, clientAddr, "입장").start()
            while (true) {
                val ac = cReadSocket.read()
                clientName = cReadSocket.readUTF().toString()
                if(ac==10){    //클라이언트로부터 소켓 종료 명령 수신
                    mHandler.obtainMessage(16, clientName).apply {
                        sendToTarget()
                    }
                    mHandler.obtainMessage(9, "$clientName 님이 퇴장하였습니다.").apply {
                        sendToTarget()
                    }
                    Broadcast(cList, 4, clientName, "퇴장").start()
                    break
                }else if(ac == 2){    //클라이언트로부터 메시지 전송 명령 수신
                    val bac = cReadSocket.readUTF()
                    val input = bac.toString()
                    val recvInput = input.trim()

                    val msg = mHandler.obtainMessage()
                    msg.what = 9
                    msg.obj = "$clientName> $recvInput"
                    mHandler.sendMessage(msg)    //핸들러에게 클라이언트로 전달받은 메시지 전송

                    Broadcast(cList, 2, clientName, recvInput).start()
                }
            }
            cWriteSocket.close()
            cSocket.close()
        }

        fun isClosed(): Boolean {
            return cSocket.isClosed
        }

        fun sendMessage(state: Int, cname: String, msg: String){
            try{
                cWriteSocket.writeInt(state)    //메시지 전송 명령 전송
                cWriteSocket.writeUTF(cname)    //클라이언트 이름
                cWriteSocket.writeUTF(msg)    //메시지 내용
            }catch(e:Exception){
                e.printStackTrace()
                mHandler.obtainMessage(12).apply {
                    sendToTarget()
                }
            }
        }
    }
    //서버에 접속한 클라이언트에게 메시지 전파
    class Broadcast(private val cList: MutableList<Client>, private val state: Int, private val cname: String, private val msg: String):Thread(){

        override fun run(){
            if(cList.size>0){
                val cIter = cList.iterator()
                while(cIter.hasNext()){
                    val client = cIter.next()
                    if (!client.isClosed()) {
                        client.sendMessage(state, cname, msg)
                    }
                    else cIter.remove()
                }
            }
        }
    }

}
