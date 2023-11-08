package com.imotorini.sbobinator9000.services
import android.app.Service
import android.content.Intent
import android.os.IBinder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import android.content.Context
import android.net.wifi.WifiManager

class DiscoveryService : Service() {

    private val client = OkHttpClient()
    private val numberOfThreads = 50
    private val executor = Executors.newFixedThreadPool(numberOfThreads)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scanSubnet()
        return START_NOT_STICKY
    }

    private fun getCurrentSubnetBase(): String? {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress

        // Converti l'indirizzo IP in una stringa nel formato a.b.c
        val ip = String.format(
                "%d.%d.%d",
                (ipAddress and 0xff),
                (ipAddress shr 8 and 0xff),
                (ipAddress shr 16 and 0xff)
        )

        return ip
    }

    private fun scanSubnet() {
        val subnetBase = getCurrentSubnetBase()
        if (subnetBase == null) {
            // Gestisci l'errore, ad esempio inviando un broadcast "ERROR"
            sendBroadcast(Intent("ERROR"))
            return
        }

        val latch = CountDownLatch(254)
        val serverFound = AtomicBoolean(false)

        for (i in 1..254) {
            val ip = "$subnetBase.$i"
            executor.execute {
                try {
                    val request = Request.Builder()
                            .url("http://$ip:9999/henlo")
                            .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful && response.body?.string() == "henlo" && serverFound.compareAndSet(false, true)) {
                        val intent = Intent("SERVER_FOUND")
                        intent.putExtra("serverIp", ip)
                        sendBroadcast(intent)
                    }
                } catch (e: Exception) {
                    // Ignora gli errori
                } finally {
                    latch.countDown()
                }
            }
        }

        Thread {
            latch.await()
            if (!serverFound.get()) {
                sendBroadcast(Intent("SERVER_NOT_FOUND"))
            }
            executor.shutdown()
        }.start()
    }
}

