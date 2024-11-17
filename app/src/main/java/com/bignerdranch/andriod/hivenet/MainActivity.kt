package com.bignerdranch.andriod.hivenet

import MyReceiver
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var service: ConnectionService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder is ConnectionService.LocalBinder) {
                service = binder.getService()
                isBound = true
                receiver = MyReceiver(service)
                registerReceiver(
                    receiver,
                    IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            unregisterReceiver(receiver)
        }
    }
    private lateinit var receiver: BroadcastReceiver

    private var isBound = false
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            playGame()
        }
        val filter = IntentFilter(ConnectionService.ACTION_REQUEST_PERMISSIONS)
        registerReceiver(permissionReceiver, filter, RECEIVER_NOT_EXPORTED)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        binding.inviteButton.setOnClickListener {
            discoverPeers()
        }

    }
    fun sendObject(gameMessage: Any) {
        service.sendObject(gameMessage)
    }


    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ConnectionService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /* register the broadcast receiver with the intent values to be matched */
    override fun onResume() {
        super.onResume()
        permissionReceiver.also { permissionReceiver -> this.registerReceiver(permissionReceiver, intentFilter,
            RECEIVER_NOT_EXPORTED
        ) }
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        unregisterReceiver(permissionReceiver)
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun playGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun discoverPeers() {
        // Start the peer discovery process by requesting peers from WifiP2pManager
        service.requestPeers()

        // Show a toast to indicate that discovery has started
        Toast.makeText(this, "Discovering peers...", Toast.LENGTH_SHORT).show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions are required to connect", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectionService.ACTION_REQUEST_PERMISSIONS) {
                requestPermissions()
            }
        }
    }
    private fun requestPermissions() {
        // Request permissions if they are missing
        if (!hasRequiredPermissions()) {
            requestPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES)
            )
        }
    }
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] != true ||
                permissions[Manifest.permission.NEARBY_WIFI_DEVICES] != true)
            {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
            }
        }
    private fun hasRequiredPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.NEARBY_WIFI_DEVICES
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
        const val SERVER_PORT = 8888
    }



}