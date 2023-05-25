package com.example.audiorecorder

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE = 111
class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private lateinit var btnRecord: ImageButton
    private lateinit var tvTimer: TextView

    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false

    private lateinit var vibrator: Vibrator

    private lateinit var  timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById<ImageButton>(R.id.btnRecord)
        tvTimer = findViewById<TextView>(R.id.tvTimer)

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)

        btnRecord.setOnClickListener {
            when{
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
        }
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording(){
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        dirPath = "${externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD.hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder = MediaRecorder()
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")

            try {
                prepare()
            }catch (e: IOException){}

            start()
        }

        btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false

        timer.start()
    }


    private fun pauseRecording() {
        recorder.pause()
        isPaused = true
        btnRecord.setImageResource(R.drawable.ic_record)

        timer.pause()
    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused = false
        btnRecord.setImageResource(R.drawable.ic_pause)

        timer.start()
    }

    private fun stopRecording() {
        timer.stop()
    }

    override fun onTimerTick(duration: String) {
        tvTimer.text = duration
    }
}