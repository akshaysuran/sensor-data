package be.hcpl.android.sensors.service

import android.app.Service
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Intent
import android.hardware.Sensor
import android.os.IBinder
import android.hardware.SensorEvent
import android.os.PowerManager
import android.util.Log
import java.lang.StringBuilder

/**
 * for a background service not linked to an activity it's important to use the command approach
 * instead of the Binder. For starting use the alarm manager
 */
class SensorBackgroundService : Service(), SensorEventListener {
    /**
     * again we need the sensor manager and sensor reference
     */
    private var mSensorManager: SensorManager? = null

    /**
     * an optional flag for logging
     */
    private var mLogging = false

    /**
     * treshold values
     */
    private var mThresholdMin = 0f
    private var mThresholdMax = 0f
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // get sensor manager on starting the service
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // have a default sensor configured
        var sensorType = Sensor.TYPE_LIGHT
        val args = intent.extras

        // get some properties from the intent
        if (args != null) {

            // set sensortype from bundle
            if (args.containsKey(KEY_SENSOR_TYPE)) sensorType = args.getInt(KEY_SENSOR_TYPE)

            // optional logging
            mLogging = args.getBoolean(KEY_LOGGING)

            // treshold values
            // since we want to take them into account only when configured use min and max
            // values for the type to disable
            mThresholdMin = if (args.containsKey(KEY_THRESHOLD_MIN_VALUE)) args.getFloat(
                KEY_THRESHOLD_MIN_VALUE
            ) else Float.MIN_VALUE
            mThresholdMax = if (args.containsKey(KEY_THRESHOLD_MAX_VALUE)) args.getFloat(
                KEY_THRESHOLD_MAX_VALUE
            ) else Float.MAX_VALUE
        }

        // we need the light sensor
        val sensor = mSensorManager?.getDefaultSensor(sensorType)

        // TODO we could have the sensor reading delay configurable also though that won't do much
        // in this use case since we work with the alarm manager
        mSensorManager?.registerListener(
            this, sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // ignore this since not linked to an activity
        return null
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // do nothing
    }

    override fun onSensorChanged(event: SensorEvent) {

        // for recording of data use an AsyncTask, we just need to compare some values so no
        // background stuff needed for this

        // Log that information for so we can track it in the console (for production code remove
        // this since this will take a lot of resources!!)
        if (mLogging) {

            // grab the values
            val sb = StringBuilder()
            for (value in event.values) sb.append(value.toString()).append(" | ")
            Log.d(
                TAG,
                "received sensor values are: $sb and previousValue was: $previousValue"
            )
        }

        // get the value
        // TODO we could make the value index also configurable, make it simple for now
        val sensorValue = event.values[0]

        // if first value is below min or above max threshold but only when configured
        // we need to enable the screen
        if (previousValue > mThresholdMin && sensorValue < mThresholdMin
            || previousValue < mThresholdMax && sensorValue > mThresholdMax
        ) {

            // and a check in between that there should have been a non triggering value before
            // we can mark a given value as trigger. This is to overcome unneeded wakeups during
            // night for instance where the sensor readings for a light sensor would always be below
            // the threshold needed for day time use.

            // TODO we could even make the actions configurable...

            // wake screen here
            val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                TAG
            )
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            //and release again
            wakeLock.release()

            // optional to release screen lock
            //KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(getApplicationContext().KEYGUARD_SERVICE);
            //KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock(TAG);
            //keyguardLock.disableKeyguard();
        }
        previousValue = sensorValue

        // stop the sensor and service
        mSensorManager?.unregisterListener(this)
        stopSelf()
    }

    companion object {
        /**
         * a tag for logging
         */
        private val TAG = SensorBackgroundService::class.java.simpleName

        /**
         * also keep track of the previous value
         */
        private var previousValue = 0f
        const val KEY_SENSOR_TYPE = "sensor_type"
        const val KEY_THRESHOLD_MIN_VALUE = "threshold_min_value"
        const val KEY_THRESHOLD_MAX_VALUE = "threshold_max_value"
        const val KEY_LOGGING = "logging"
    }
}