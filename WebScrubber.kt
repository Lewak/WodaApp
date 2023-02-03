package com.example.wodasyf

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class ReturnData(){
    public var waterLevel = 0
    public var dateObtained = "null"

}

class WebScrubber() {

    var returnData = ReturnData()

    public fun fetchWaterData(station_number:Int, activity: Activity) :ReturnData {
        if(verifyAvailableNetwork(activity)){
            val url = URL("https://hydro.imgw.pl/api/station/hydro/?id=$station_number")
            //println("https://hydro.imgw.pl/api/station/hydro/?id=$station_number")
            val connect = url.openConnection() as HttpsURLConnection
            connect.setRequestProperty("content-type", "application/json")

            if (connect.responseCode == 200){
                val inputStream = connect.inputStream
                val token = JSONTokener(inputStream.bufferedReader().use { it.readText() }).nextValue() as JSONObject
                val jsonArray = token.getJSONObject("status")
                if (jsonArray.getString("currentState") != "null"){
                    var secondArray = jsonArray.getJSONObject("currentState")
                    connect.disconnect()
                    returnData.waterLevel = (secondArray.getString("value").toFloatOrNull() ?: 0.0).toInt()
                    if((returnData.waterLevel != 0)){
                        returnData.dateObtained = secondArray.getString("date")
                    }
                    else returnData.dateObtained = "null"
                }
                else{
                    returnData.dateObtained = "null"
                }

                return returnData

            }
            else{
                connect.disconnect()
                returnData.waterLevel = 0
                returnData.dateObtained = "null"
                return returnData
            }
        }
        else{
            returnData.waterLevel = 0
            returnData.dateObtained = "null"
            return returnData
        }


    }
    fun verifyAvailableNetwork(activity: Activity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }
}

