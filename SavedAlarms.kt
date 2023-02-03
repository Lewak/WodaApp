package com.example.wodasyf

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SavedAlarms() : ViewModel() {

     var savedAlarms = mutableListOf<Alarm>()
     var lastEditedAlarm = 0

    fun getAlarm(number:Int): Alarm {
        return savedAlarms[number]
    }
    fun saveAlarm(alarm: Alarm) {
        savedAlarms.add(alarm)
    }
    fun getCount(): Int{
        return savedAlarms.size
    }
    fun clearAllAlarms(){
        savedAlarms.clear()
    }
    fun editAlarm(alarm:Alarm, id:Int){
        savedAlarms[id] = alarm
    }
    fun deleteAlarm(id:Int){
        savedAlarms.removeAt(id)
    }

}