package com.linternarapida.activities


import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import com.linternarapida.databinding.ActivityMainBinding
import com.linternarapida.helpers.MyCameraImpl


import com.linternarapida.extensions.config
import com.linternarapida.helpers.CameraTorchListener
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.getProperBackgroundColor
import com.simplemobiletools.commons.extensions.getProperPrimaryColor

class MainActivity : Activity() {

    //viewBindings
    private lateinit var binding: ActivityMainBinding


    private val FLASHLIGHT_STATE = "flashlight_state"

    private var mCameraImpl: MyCameraImpl? = null
    private var mIsFlashlightOn = false
    private var reTurnFlashlightOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        config.turnFlashlightOn
        binding.flashlightBtn.setOnClickListener {
            mCameraImpl!!.toggleFlashlight()
        }

    }

    override fun onResume() {
        super.onResume()
        mCameraImpl!!.handleCameraSetup()
        checkState(MyCameraImpl.isFlashlightOn)

        setupTurnFlashlightOn()

        requestedOrientation = if (config.forcePortraitMode) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR

        if (config.turnFlashlightOn && reTurnFlashlightOn) {
            mCameraImpl!!.enableFlashlight()
        }

        reTurnFlashlightOn = true

    }

    override fun onStart() {
        super.onStart()

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FLASHLIGHT_STATE, mIsFlashlightOn)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val isFlashlightOn = savedInstanceState.getBoolean(FLASHLIGHT_STATE, false)
        if (isFlashlightOn) {
            mCameraImpl!!.toggleFlashlight()
        }
    }

    private fun setupCameraImpl() {
        mCameraImpl = MyCameraImpl.newInstance(this, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {

            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        })
        if (config.turnFlashlightOn) {
            mCameraImpl!!.enableFlashlight()
        }
    }

    private fun setupTurnFlashlightOn() {
        binding.settingsTurnFlashlightOn.isChecked = config.turnFlashlightOn
        binding.settingsTurnFlashlightOnHolder.setOnClickListener {
            binding.settingsTurnFlashlightOn.toggle()
            config.turnFlashlightOn = binding.settingsTurnFlashlightOn.isChecked
        }
    }

    private fun getContrastColor() = getProperBackgroundColor().getContrastColor()

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }


    private fun checkState(isEnabled: Boolean) {
        if (isEnabled) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    private fun enableFlashlight() {
        changeIconColor(getProperPrimaryColor(), binding.flashlightBtn)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = true
    }

    private fun disableFlashlight() {
        changeIconColor(getContrastColor(), binding.flashlightBtn)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = false
    }

    private fun changeIconColor(color: Int, imageView: ImageView?) {
        imageView!!.background.applyColorFilter(color)
    }


}

