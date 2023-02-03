package com.example.wodasyf

import android.content.SharedPreferences

class SaveDataInterface(savedAlarms:SavedAlarms, sharedPref:SharedPreferences) {
    var savedAlarms = SavedAlarms()
    lateinit var sharedPref : SharedPreferences

    init {
        this.savedAlarms = savedAlarms
        this.sharedPref = sharedPref

    }
    public fun saveAllValues(){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt("alarmCount", this.savedAlarms.getCount() )
        for(x in 0..this.savedAlarms.getCount() -1){

            editor.putInt("currentWaterLevel$x", this.savedAlarms.getAlarm(x).currentWaterLevel )
            editor.putInt("maxWaterLevel$x", this.savedAlarms.getAlarm(x).maxWaterLevel )
            editor.putInt("minWaterLevel$x", this.savedAlarms.getAlarm(x).minWaterLevel )
            editor.putString("stationName$x", this.savedAlarms.getAlarm(x).stationName )
            editor.putString("name$x", this.savedAlarms.getAlarm(x).name )
            editor.putInt("stationNameOrder$x", this.savedAlarms.getAlarm(x).stationNameOrder )
            editor.putBoolean("enable$x", this.savedAlarms.getAlarm(x).enable)

        }
        editor.commit()
    }
    public fun retrieveAllValues(){
        savedAlarms.clearAllAlarms()
        for(x in 0..sharedPref.getInt("alarmCount", 0) -1){
            val tempAlarm = Alarm()
            tempAlarm.stationName = sharedPref.getString("stationName$x", "").toString()
            tempAlarm.name = sharedPref.getString("name$x", "").toString()
            tempAlarm.maxWaterLevel = sharedPref.getInt("maxWaterLevel$x", 0)
            tempAlarm.minWaterLevel = sharedPref.getInt("minWaterLevel$x", 0)
            tempAlarm.currentWaterLevel = sharedPref.getInt("currentWaterLevel$x", 0)
            tempAlarm.stationNameOrder = sharedPref.getInt("stationNameOrder$x", 0)
            tempAlarm.enable = sharedPref.getBoolean("enable$x", false)
            savedAlarms.saveAlarm(tempAlarm)

        }



    }

}