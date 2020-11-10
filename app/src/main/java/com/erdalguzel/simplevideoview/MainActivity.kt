package com.erdalguzel.simplevideoview

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val VIDEO_SAMPLE: String = "tacoma_narrows"
    private val PLAYBACK_TIME: String = "play_time"

    private lateinit var mVideoView: VideoView
    private lateinit var mMediaController: MediaController
    private lateinit var mBufferingTextView: TextView

    private var mCurrentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mVideoView = findViewById(R.id.videoview)
        mBufferingTextView = findViewById(R.id.buffering_textview)

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME)
        }

        mMediaController = MediaController(this)
        mMediaController.setMediaPlayer(mVideoView)
        mVideoView.setMediaController(mMediaController)

        initializePlayer()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_TIME, mVideoView.currentPosition)
    }

    private fun initializePlayer() {
        val uri: Uri? = getMedia(VIDEO_SAMPLE)
        mBufferingTextView.visibility = VideoView.VISIBLE
        mVideoView.setVideoURI(uri)

        mVideoView.setOnPreparedListener {
            mBufferingTextView.visibility = VideoView.INVISIBLE
            if (mCurrentPosition > 0) {
                mVideoView.seekTo(mCurrentPosition)
            } else {
                // Setting this to 1 plays the video instead of giving black screen
                mVideoView.seekTo(1)
            }
            //mVideoView.start()
        }

        mVideoView.setOnCompletionListener {
            Toast
                .makeText(this@MainActivity, "Playback is complete", Toast.LENGTH_SHORT)
                .show()
            mVideoView.seekTo(1)
        }
    }

    private fun releasePlayer() {
        mVideoView.stopPlayback()
    }

    private fun getMedia(mediaName: String): Uri? {
        if (URLUtil.isValidUrl(mediaName)) {
            return Uri.parse(mediaName)
        } else {
            return Uri.parse("android.resource://$packageName/raw/$mediaName")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_forward -> {
                mVideoView.seekTo(mVideoView.currentPosition + 10 * 1000)
                return true
            }
            R.id.item_saveAs -> {
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}