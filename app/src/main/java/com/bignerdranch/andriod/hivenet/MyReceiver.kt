import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import com.bignerdranch.andriod.hivenet.MainActivity
import com.bignerdranch.andriod.hivenet.R

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
class MyReceiver(
    private val manager: WifiP2pManager?,
    private val channel: WifiP2pManager.Channel?,
    private val activity: MainActivity
) : BroadcastReceiver() {
    private val TAG = "MyReceiver"

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action!!
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(activity, "Wi-Fi P2P is enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Wi-Fi P2P is disabled", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                /*(activity.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
                    .apply {
                        updateThisDevice(
                            intent.getParcelableExtra(
                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice
                        )
                    }*/
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                manager?.requestPeers(channel, activity.peerListListener)
                Log.d(TAG, "P2P peers changed")


            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    manager?.requestConnectionInfo(channel) { info ->
                        if (info.groupFormed && info.isGroupOwner) {
                            activity.startServerSocket()
                        } else if (info.groupFormed) {
                            activity.startClientSocket(info.groupOwnerAddress)
                        }
                    }
                }
            }
        }
    }
}