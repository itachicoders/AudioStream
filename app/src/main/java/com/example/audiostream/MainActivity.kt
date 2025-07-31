
package com.example.audiostream

import android.media.*
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private val serverIP = "YOUR_SERVER_IP" // <-- ЗАМЕНИ НА VPS IP
    private val serverPort = 50005
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendButton = findViewById<Button>(R.id.buttonSend)
        val receiveButton = findViewById<Button>(R.id.buttonReceive)
        
        var isSending = false
        var isReceiving = false

        sendButton.setOnClickListener {
            if (isReceiving) return@setOnClickListener
            isSending = true
            Thread { startSending() }.start()
        }
        
        receiveButton.setOnClickListener {
            if (isSending) return@setOnClickListener
            isReceiving = true
            Thread { startReceiving() }.start()
        }
    }

    private fun startSending() {
        try {
            val socket = Socket(serverIP, serverPort)
            val output: OutputStream = socket.getOutputStream()

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val buffer = ByteArray(bufferSize)
            audioRecord.startRecording()

            while (!Thread.interrupted()) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    output.write(buffer, 0, read)
                }
            }

            audioRecord.stop()
            audioRecord.release()
            socket.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startReceiving() {
        try {
            val socket = Socket(serverIP, serverPort)
            val input: InputStream = socket.getInputStream()

            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )

            val buffer = ByteArray(bufferSize)
            audioTrack.play()

            while (!Thread.interrupted()) {
                val read = input.read(buffer)
                if (read > 0) {
                    audioTrack.write(buffer, 0, read)
                }
            }

            audioTrack.stop()
            audioTrack.release()
            socket.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
