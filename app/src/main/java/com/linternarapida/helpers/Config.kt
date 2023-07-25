package com.linternarapida.helpers

import android.content.Context
import com.simplemobiletools.commons.helpers.BaseConfig


// puede que falte algun archivo para obtener dichas configuraciones? de donde sale "prefs?"
class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var turnFlashlightOn: Boolean
        get() = prefs.getBoolean(TURN_FLASHLIGHT_ON, false)
        set(turnFlashlightOn) = prefs.edit().putBoolean(TURN_FLASHLIGHT_ON, turnFlashlightOn).apply()


    var forcePortraitMode: Boolean
        get() = prefs.getBoolean(FORCE_PORTRAIT_MODE, true)
        set(forcePortraitMode) = prefs.edit().putBoolean(FORCE_PORTRAIT_MODE, forcePortraitMode).apply()

    var brightnessLevel: Int
        get() = prefs.getInt(BRIGHTNESS_LEVEL, DEFAULT_BRIGHTNESS_LEVEL)
        set(brightnessLevel) = prefs.edit().putInt(BRIGHTNESS_LEVEL, brightnessLevel).apply()
}
