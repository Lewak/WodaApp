package com.example.wodasyf

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.wodasyf.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val mainAlarmList: SavedAlarms by viewModels<SavedAlarms>()
    lateinit var sharedPref : SharedPreferences
    lateinit var saveDataInterface : SaveDataInterface
    lateinit var  alarmHandler: BackgroundAlarmHandler
    lateinit var mainWebScrubber: WebScrubber
    lateinit var myReceiver: AlarmTest
    lateinit var ringTone: Ringtone
    lateinit var vibrator: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)


        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val mWakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "myapp:tagforclassxyz"
        )


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        this.sharedPref = this.getSharedPreferences(
            "test_data", Context.MODE_PRIVATE)
        this.mainWebScrubber = WebScrubber()
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent("com.example.wodasyf.AlarmTest")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE)
        val filter = IntentFilter()
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            this.ringTone = RingtoneManager.getRingtone(
                this,
                notification
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        this.alarmHandler = BackgroundAlarmHandler(mainAlarmList, mainWebScrubber, this, alarmManager, pendingIntent, this,  ringTone, navController, mWakeLock, vibrator)

        filter.addAction("com.example.wodasyf.AlarmTest")
        this.myReceiver = AlarmTest(alarmHandler)
        registerReceiver(myReceiver, filter)

        this.saveDataInterface = SaveDataInterface(mainAlarmList, sharedPref)

        saveDataInterface.retrieveAllValues()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.newAlarm -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)

                navController.navigate(R.id.action_global_createNewAlarm)
                true
            }
            R.id.clear_alarms ->{
                mainAlarmList.clearAllAlarms()
                finish();
                startActivity(getIntent());

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    override fun onResume(){
        if(alarmHandler.wakeLock.isHeld()){
            alarmHandler.wakeLock.release();
        }
        if (alarmHandler.triggered){
            alarmHandler.triggered = false
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_global_SecondFragment)
        }
        super.onResume()
    }

    override fun onPause(){
        saveDataInterface.saveAllValues()
        super.onPause()
    }
    override  fun onStop(){
        saveDataInterface.saveAllValues()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        saveDataInterface.saveAllValues()

    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        newOverride.densityDpi = 410
        newOverride.colorMode = 1
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}

