import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bignerdranch.andriod.hivenet.ConnectionService
import com.bignerdranch.andriod.hivenet.ConnectionService.Companion.EXTRA_INFO
import com.bignerdranch.andriod.hivenet.MainActivity
import com.bignerdranch.andriod.hivenet.MainActivity.Companion.PERMISSION_REQUEST_CODE
import com.bignerdranch.andriod.hivenet.R

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
class MyReceiver(
    private val service: ConnectionService
) : BroadcastReceiver() {
    companion object {
        private const val TAG = "MyReceiver"
    }


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action!!
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "Wi-Fi P2P is enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Wi-Fi P2P is disabled", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device = intent.getParcelableExtra(EXTRA_WIFI_P2P_DEVICE, WifiP2pDevice::class.java)
                // Update device details in the service if needed
                service.updateDeviceInfo(device)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Request available peers
                if (service.hasRequiredPermissions()) {
                    service.requestPeers()
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val info: WifiP2pInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, WifiP2pInfo::class.java)
                if (info != null) {
                    if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    ) {
                        // Device is connected to the internet, notify the service
                        service.onConnectionInfoAvailable(info)
                    } else {
                        // No internet connection, reset the connection in the service
                        Log.d(TAG, "Not connected to a network")
                        service.resetConnection()
                    }
                }
            }
        }
    }
}