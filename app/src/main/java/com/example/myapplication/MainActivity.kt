package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Time
import java.util.concurrent.TimeUnit
import kotlin.math.min
import androidx.core.os.HandlerCompat.postDelayed



class MainActivity : AppCompatActivity() {
    lateinit var mediaPlayer:MediaPlayer
    val handler = Handler()
    val song = arrayOf(R.raw.piano,R.raw.classic,R.raw.sample)
    var currentSong:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val songTitleArray = arrayOf("Piano Music","Classic Music","Sample Music")

        val adapter = ArrayAdapter(this,R.layout.listitem,songTitleArray)

        listView.adapter = adapter

        mediaPlayer = MediaPlayer.create(this,song[currentSong])

        songName.text = songTitleArray[currentSong]
        val songDuration = duration()
        seekBar.max = mediaPlayer.duration
        timeStart.text = "0:00"
        timeEnd.text = "${songDuration[0]}:${songDuration[1]}"

        playBtn.setOnClickListener {
            if (mediaPlayer.isPlaying){
                mediaPlayer.pause()
                playBtn.text = "PLAY"
            }else{
                UpdateSongTime.run()
                mediaPlayer.start()
                playBtn.text = "PAUSE"
            }
        }

        nextBtn.setOnClickListener {
            if(currentSong==2) {
                currentSong=0
            }else{
                currentSong+=1
            }
            setNewMediaPlayer(currentSong,songTitleArray[currentSong])
        }

        backBtn.setOnClickListener {
            if(currentSong==0) {
                currentSong=2
            }else{
                currentSong-=1
            }
            setNewMediaPlayer(currentSong,songTitleArray[currentSong])
        }

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                mediaPlayer.seekTo(p1)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }
            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

    }

    private fun setNewMediaPlayer (indexOfSong:Int, title:String) {
        mediaPlayer.stop()

        songName.text = title

        currentSong = indexOfSong
        mediaPlayer = MediaPlayer.create(this,song[currentSong])

        seekBar.max = mediaPlayer.duration

        val songDuration = duration()

        timeEnd.text = "${songDuration[0]}:${songDuration[1]}"
        Toast.makeText(this,"$title Playing",Toast.LENGTH_LONG).show()
        playBtn.text = "PAUSE"

        mediaPlayer.start()
    }
    fun duration (): Array<Long> {
        var minute = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.duration.toLong())
        var seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.duration.toLong()) - TimeUnit.MINUTES.toSeconds(minute)
        return arrayOf(minute,seconds)
    }

    fun listClicked(v:View) {
        val textView = v as TextView
        when (textView.text){
            "Piano Music" -> {
                setNewMediaPlayer(0, textView.text as String)
            }
            "Classic Music" -> {
                setNewMediaPlayer(1, textView.text as String)
            }
            "Sample Music" -> {
                setNewMediaPlayer(2, textView.text as String)
            }
        }
    }

    private val UpdateSongTime = object : Runnable {
        override fun run() {
            val startTime = mediaPlayer.currentPosition
            var minute = TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
            var second = TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(minute)
            timeStart.text = "$minute:$second"
            seekBar.progress = startTime
            handler.postDelayed(this, 100)
        }
    }
}
