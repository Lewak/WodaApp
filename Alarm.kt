package com.example.wodasyf

class Alarm()  {
    var currentWaterLevel = 0
    var maxWaterLevel = 0
    var minWaterLevel = 0
    var stationName = "Null"
    var stationNameOrder = 0
    var name = "Null"
    var enable = false
    public var triggered = false
    public fun init(currentWaterLevel:Int, maxWaterLevel:Int, minWaterLevel:Int, stationName:String,stationNameOrder:Int, name:String,enable:Boolean)
    {
        this.currentWaterLevel = currentWaterLevel
        this.maxWaterLevel = maxWaterLevel
        this.minWaterLevel = minWaterLevel
        this.stationName = stationName
        this.stationNameOrder = stationNameOrder
        this.name = name
        this.enable = enable
    }
    public fun obtainWaterValue(){

    }
    public fun saveData(){

    }
    public fun checkBoundaries(){

    }
}