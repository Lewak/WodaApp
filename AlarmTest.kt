package com.example.wodasyf

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Vibrator
import java.util.*

class AlarmTest(var alarmHandler: BackgroundAlarmHandler) : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent

    ) {
        alarmHandler.passVibratorManager(context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        val currentTime: Date = Calendar.getInstance().getTime()

        println("NO SPRAWDZAM CHUJu")
        println(currentTime)

        alarmHandler.executePeriodicAlarm()
        //executePeriodicAlarm()
    }
}