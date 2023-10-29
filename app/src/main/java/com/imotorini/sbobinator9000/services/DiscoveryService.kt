package com.imotorini.sbobinator9000.services
import android.app.Service
import android.content.Intent
import android.os.IBinder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

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

    private fun scanSubnet() {
        val latch = CountDownLatch(254)
        val serverFound = AtomicBoolean(false)

        for (i in 1..254) {
            val ip = "192.168.1.$i"
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
