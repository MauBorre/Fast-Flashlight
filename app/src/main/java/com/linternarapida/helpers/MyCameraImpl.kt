package com.linternarapida.helpers

import android.content.Context
import android.os.Handler
import com.linternarapida.R
import com.linternarapida.extensions.updateWidgets
import com.linternarapida.models.Events
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import org.greenrobot.eventbus.EventBus

class MyCameraImpl private constructor(
    val context: Context,
    private var cameraTorchListener: CameraTorchListener? = null) {

    companion object {
        var isFlashlightOn = false

        //private var u = 200L // length of one dit (time unit)
        //private val SOS = arrayListOf(u, u, u, u, u, u * 3, u * 3, u, u * 3, u, u * 3, u * 3, u, u, u, u, u, u * 7)

        private var shouldEnableFlashlight = false
        //private var shouldEnableStroboscope = false
        //private var shouldEnableSOS = false
        //private var isStroboSOS = false // are we wending sos or casual stroboscope?

        private var cameraFlash: CameraFlash? = null



        fun newInstance(context: Context,
                        cameraTorchListener: CameraTorchListener? = null) = MyCameraImpl(context, cameraTorchListener)
    }

    init {
        handleCameraSetup()
    }

    fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
        checkFlashlight()
    }


    private fun tryInitCamera(): Boolean {
        handleCameraSetup()
        if (cameraFlash == null) {
            context.toast(R.string.camera_error) // faltan recursos
            return false
        }
        return true
    }

    fun handleCameraSetup() {
        try {
            if (cameraFlash == null) {
                cameraFlash = CameraFlash(context, cameraTorchListener)
            }
        } catch (e: Exception) {
            EventBus.getDefault().post(Events.CameraUnavailable())
        }
    }

    private fun checkFlashlight() {
        handleCameraSetup()

        if (isFlashlightOn) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    fun enableFlashlight() {
        shouldEnableFlashlight = true

        try {
            cameraFlash!!.initialize()
            cameraFlash!!.toggleFlashlight(true)
        } catch (e:Exception) {
            context.showErrorToast(e)
            disableFlashlight()
        }

        val mainRunnable = Runnable { stateChanged(isEnabled = true)}
        Handler(context.mainLooper).post(mainRunnable)
    }

    private fun disableFlashlight() {

        try {
            cameraFlash!!.toggleFlashlight(false)
        } catch (e: Exception) {
            context.showErrorToast(e)
            disableFlashlight()
        }
        stateChanged(isEnabled = false)
    }

    private fun stateChanged(isEnabled: Boolean) {
        isFlashlightOn = isEnabled
        EventBus.getDefault().post(Events.StateChanged(isEnabled))
        context.updateWidgets(isEnabled)
    }

    fun releaseCamera() {
        cameraFlash?.unregisterListeners()

        if (isFlashlightOn) {
            disableFlashlight()
        }

        cameraFlash?.release()
        cameraFlash = null
        cameraTorchListener = null

        isFlashlightOn = false
    }

    fun onCameraNotAvailable() {
        disableFlashlight()
    }
}