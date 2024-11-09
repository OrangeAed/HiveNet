package com.bignerdranch.andriod.hivenet

import MyReceiver
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bignerdranch.andriod.hivenet.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }
    private val peers = mutableListOf<WifiP2pDevice>()

    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private var clientSocket: Socket? = null
    private var serverOutput: BufferedWriter? = null
    private var serverInput: BufferedReader? = null
    private var socket: Socket? = null
    private var clientOutput: BufferedWriter? = null
    private var clientInput: BufferedReader? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            playGame()
        }

        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = this.manager?.let { MyReceiver(it, channel, this) }
        }
        registerReceiver(receiver, IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION))
        binding.inviteButton.setOnClickListener {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    Snackbar.make(binding.root, "Searching for peers...", Snackbar.LENGTH_LONG).show()
                }

                override fun onFailure(reasonCode: Int) {
                    Toast.makeText(this@MainActivity, "No devices found.", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    /* register the broadcast receiver with the intent values to be matched */
    override fun onResume() {
        super.onResume()
        receiver?.also { receiver -> this.registerReceiver(receiver, intentFilter,
            RECEIVER_NOT_EXPORTED
        ) }
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        receiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

    private fun playGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {return}
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Assume this device acts as the server
                startServerSocket()
                Snackbar.make(binding.root, "Connected to ${device.deviceName}", Snackbar.LENGTH_SHORT).show()
            }

            override fun onFailure(reasonCode: Int) {
                Toast.makeText(this@MainActivity, "Connection failed: $reasonCode", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun connectToServer(address: InetAddress) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = Socket(address, 8888)

                // Set up input and output streams
                clientOutput = socket?.getOutputStream()?.bufferedWriter()
                clientInput = socket?.getInputStream()?.bufferedReader()

                // Start listening for messages from the server
                listenForServerMessages()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun sendObjectToServer(gameMessage: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gson = Gson()
                val jsonMessage = gson.toJson(gameMessage)

                clientOutput?.let {
                    it.write(jsonMessage)
                    it.newLine()
                    it.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun listenForServerMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gson = Gson()
                while (true) {
                    val receivedJson = clientInput?.readLine()
                    if (receivedJson != null) {
                        val receivedMessage = gson.fromJson(receivedJson, Any::class.java)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Server says: ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun sendObjectToClient(gameMessage: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gson = Gson()
                val jsonMessage = gson.toJson(gameMessage)

                serverOutput?.let {
                    it.write(jsonMessage)
                    it.newLine()
                    it.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun listenForClientMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            try {
                while (true) {
                    val receivedJson = serverInput?.readLine()
                    receivedJson?.let {
                        val message = gson.fromJson(it, Any::class.java)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Client: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun startServerSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverSocket = ServerSocket(8888)
                clientSocket = serverSocket.accept()

                // Set up input and output streams
                serverInput = clientSocket?.getInputStream()?.bufferedReader()
                serverOutput = clientSocket?.getOutputStream()?.bufferedWriter()

                // Start listening for messages from the client
                listenForClientMessages()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun startClientSocket(address: InetAddress) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(address, 8888)
                val outputStream = socket.getOutputStream()
                outputStream.write("Hello from client!".toByteArray())
                outputStream.close()
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
            val device = peers[0] // Connect to the first device found
            connectToDevice(device)
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }
}