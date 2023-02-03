package com.example.wodasyf

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.navigation.NavController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

import java.util.*


class BackgroundAlarmHandler(savedAlarms: SavedAlarms, fetchWaterData:WebScrubber, val context: Context, val alarmManager: AlarmManager, val pendingIntent: PendingIntent, val activity: Activity, val ringtone: Ringtone, val navigationController: NavController, val wakeLock: WakeLock, var vibrator: Vibrator) {
    var savedAlarms = SavedAlarms()
    var webScrubber = WebScrubber()
    var disconnectAlarmCounter = 0
    var alarmMessage = ""
    var triggered = false
    init {
        this.savedAlarms = savedAlarms
        this.webScrubber = fetchWaterData
    }
    fun passVibratorManager(vibrator: Vibrator) {
        this.vibrator = vibrator
    }
    fun setAlarmIfNecessary(trigger: Boolean)
    {
        var anyEnabled = false
    for (x in 0 until savedAlarms.getCount()){
        if(savedAlarms.getAlarm(x).enable) anyEnabled = true
    }
        if (anyEnabled){
            if(trigger){
                if(!executePeriodicAlarm()){
                    setNormalPeriodAlarm()
                }
            }
            else{
                setNormalPeriodAlarm()
            }

        }
    }

    fun setNormalPeriodAlarm()
    {
        //println("11111")

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60 * 5000,
            pendingIntent)


    }
    fun setNoConnectionAlarm()
    {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60 * 1000,
            pendingIntent)
    }
    fun executePeriodicAlarm():Boolean {
        var isAnyTriggered = false
        var isThereConnectionProblem = false
        var isThereNoServerResponse = true;
        var triggeredList = mutableListOf<Alarm>()
        for (x in 0 until savedAlarms.getCount()){
            val maxWater = savedAlarms.getAlarm(x).maxWaterLevel
            val minWater = savedAlarms.getAlarm(x).minWaterLevel
            if(savedAlarms.getAlarm(x).enable){
                val data = webScrubber.fetchWaterData(savedAlarms.getAlarm(x).stationName.split(" ")[1].toInt(), activity)
                val actualWater = data.waterLevel
                val formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM ).withLocale( Locale.GERMANY ).withZone( ZoneId.systemDefault())
//                println(Instant.now().minus(30,ChronoUnit.MINUTES).toString())
//                println(instant.toString())
//                println(instant.compareTo(Instant.now().minus(30,ChronoUnit.MINUTES)) > 0)
                if(data.dateObtained != "null")
                {
                    val instant = Instant.parse(data.dateObtained)
                    isThereNoServerResponse = instant.compareTo(Instant.now().minus(30,ChronoUnit.MINUTES)) > 0

                    if(isThereNoServerResponse){
                        savedAlarms.getAlarm(x).currentWaterLevel = actualWater
                        if((minWater >= actualWater).or(maxWater <= actualWater)){
                            isAnyTriggered = true
                            triggeredList.add(savedAlarms.getAlarm(x))
                        }
                    }
                    else{
                        isThereConnectionProblem = true
                    }
                    //println(actualWater)

                }
                else{
                    isThereConnectionProblem = true
                }

                }
            }
            if(isAnyTriggered.and(!isThereConnectionProblem)){
                triggerAlarm(triggeredList)
                disconnectAlarmCounter = 0
                return true

            }
            else if(!isThereNoServerResponse){
                disconnectAlarmCounter = 0
                executeNoConnectionAlarm("Serwer nie odpowiada od 30 minut")
                return true

            }
            else if(!isThereConnectionProblem){

                setAlarmIfNecessary(false)
                disconnectAlarmCounter = 0
                return false
            }
            else if(disconnectAlarmCounter < 3){
                setNoConnectionAlarm()
                disconnectAlarmCounter++
                return false
            }
            else {
                disconnectAlarmCounter = 0
                executeNoConnectionAlarm("Brak połączenia z internetem")
                return true
            }

    }
    fun executeNoConnectionAlarm(message:String){
        this.wakeLock.acquire();
        ringtone.play()
        val pattern = longArrayOf(0, 200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500)

        vibrator.vibrate(
            VibrationEffect.createWaveform(pattern, -1),
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        ) //vibrator.vibrate(pattern, -1)
        this.alarmMessage = message
        triggered = true
        navigationController.navigate(R.id.action_global_SecondFragment)
    }

    fun triggerAlarm(alarmList : List<Alarm>)
    {
        this.wakeLock.acquire()

        for (alarm in alarmList){
            val name = alarm.name
            val triggerCause = alarm.currentWaterLevel

            //println("przekroczono limity wody w stacji $name, nowy poziom wody: $triggerCause")
            this.alarmMessage+= ("Przekroczono limity w $name, nowy poziom wody: $triggerCause\n")
        }
        triggered = true
        ringtone.play()
        val pattern = longArrayOf(0, 200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500,200, 500)
        vibrator.vibrate(
            VibrationEffect.createWaveform(pattern, -1),
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        navigationController.navigate(R.id.action_global_SecondFragment)



    }


}

