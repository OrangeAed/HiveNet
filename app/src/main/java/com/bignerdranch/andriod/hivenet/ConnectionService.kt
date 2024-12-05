package com.bignerdranch.andriod.hivenet

import MyReceiver
import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.bignerdranch.andriod.hivenet.MainActivity.Companion.PERMISSION_REQUEST_CODE
import com.bignerdranch.andriod.hivenet.MainActivity.Companion.SERVER_PORT
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class ConnectionService : Service() {
    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var inputReader: BufferedReader? = null
    private var outputWriter: BufferedWriter? = null
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: MyReceiver
    private val peers = mutableListOf<WifiP2pDevice>()
    private var connectionInfo: WifiP2pInfo? = null

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): ConnectionService = this@ConnectionService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Wi-Fi P2P manager and channel
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        // Register the receiver for Wi-Fi P2P events
        receiver = MyReceiver(this)
        val filter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
        }
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the service is destroyed
        unregisterReceiver(receiver)
        resetConnection()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_CONNECTION_INFO_AVAILABLE -> {
                val info = intent.getParcelableExtra(EXTRA_INFO, WifiP2pInfo::class.java)
                info?.let { handleConnectionInfo(info) }
            }
            ACTION_RESET_CONNECTION -> resetConnection()
        }
        return START_NOT_STICKY
    }

    private fun handleConnectionInfo(info: WifiP2pInfo) {
        // Handle the connection info, either start the server or client
        Toast.makeText(this, "Handling Connection info", Toast.LENGTH_SHORT).show()
        connectionInfo = info
        if (info.groupFormed) {
            if (info.isGroupOwner) {
                startServerSocket()
            } else {
                info.groupOwnerAddress?.let { startClientSocket(it) }
            }
        }
    }

    private fun startServerSocket() {
        Toast.makeText(this, "Starting Server Socket", Toast.LENGTH_SHORT).show()
        thread {
            try {
                serverSocket = ServerSocket(SERVER_PORT)
                Log.d(TAG, "Server started, waiting for client...")
                val clientSocket = serverSocket?.accept()
                Log.d(TAG, "Client connected")
                clientSocket?.let { initializeSocketStreams(it) }
            } catch (e: IOException) {
                Log.e(TAG, "Error in server socket: ${e.message}")
            }
        }
    }

    private fun startClientSocket(address: InetAddress) {
        Toast.makeText(this, "Starting Client Socket", Toast.LENGTH_SHORT).show()
        thread {
            try {
                socket = Socket(address, SERVER_PORT)
                initializeSocketStreams(socket!!)
            } catch (e: IOException) {
                Log.e(TAG, "Error in client socket: ${e.message}")
            }
        }
    }

    private fun initializeSocketStreams(clientSocket: Socket) {
        Toast.makeText(this, "Initializing Sockets", Toast.LENGTH_SHORT).show()
        socket = clientSocket
        inputReader = clientSocket.getInputStream().bufferedReader()
        outputWriter = clientSocket.getOutputStream().bufferedWriter()
        listenForMessages()
    }

    private fun listenForMessages() {
        Toast.makeText(this, "Listening for messages", Toast.LENGTH_SHORT).show()
        thread {
            try {
                while (true) {
                    val receivedJson = inputReader?.readLine()
                    receivedJson?.let {
                        Log.d(TAG, "Received message: $it")
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error while listening for messages: ${e.message}")
            }
        }
    }

    fun sendObject(gameMessage: Any) {
        thread {
            try {
                val gson = Gson()
                val jsonMessage = gson.toJson(gameMessage)
                outputWriter?.apply {
                    write(jsonMessage)
                    newLine()
                    flush()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error sending message: ${e.message}")
            }
        }
    }

    private fun resetConnection() {
        Log.d(TAG, "Resetting connection")
        try {
            socket?.close()
            serverSocket?.close()
            inputReader = null
            outputWriter = null
            socket = null
            serverSocket = null
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e(TAG, "Error resetting connection: ${e.message}")
        }
    }

    fun updateDeviceInfo(device: WifiP2pDevice?) {

    }
    fun hasRequiredPermissions(): Boolean {
        val locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val nearbyDevicesPermission = checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED
        return locationPermission && nearbyDevicesPermission
    }

    private fun checkAndBroadcastPermissions() {
        if (!hasRequiredPermissions()) {
            val intent = Intent(ACTION_REQUEST_PERMISSIONS)
            sendBroadcast(intent)
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    private fun connectToDevice(device: WifiP2pDevice) {
        if (!hasRequiredPermissions()) {
            checkAndBroadcastPermissions()
            return
        }
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connection initiated")
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Connection failed: $reason")
            }
        })
    }
    fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        info?.let {
            handleConnectionInfo(it)
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    fun requestPeers() {


        val peerListListener = WifiP2pManager.PeerListListener { peerList ->
            val refreshedPeers = peerList.deviceList
            if (refreshedPeers != peers) {
                peers.clear()
                peers.addAll(refreshedPeers)
                var device: WifiP2pDevice = peers[0]
                for(peer in peers) {
                    if (peer.deviceName in "Galaxy-S24") {
                        Log.d(TAG,"Found Aedan")
                        device = peer
                    }
                }
                connectToDevice(device)
            }

            if (peers.isEmpty()) {
                Log.d(TAG, "No devices found")
                return@PeerListListener
            } else {
                Log.d(TAG, "Peers " + peers.toString())
            }
        }
        manager.requestPeers(channel, peerListListener)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Found peers")
            }
            override fun onFailure(reason: Int) {
                Log.d(TAG, "Found peers")
            }
        })
    }

    companion object {
        private const val TAG = "ConnectionService"
        const val ACTION_CONNECTION_INFO_AVAILABLE = "com.bignerdranch.andriod.hivenet.ACTION_CONNECTION_INFO_AVAILABLE"
        const val ACTION_RESET_CONNECTION = "com.bignerdranch.andriod.hivenet.ACTION_RESET_CONNECTION"
        const val EXTRA_INFO = "com.bignerdranch.andriod.hivenet.EXTRA_INFO"
        const val ACTION_REQUEST_PERMISSIONS = "com.bignerdranch.andriod.hivenet.REQUEST_PERMISSIONS"
    }
}
