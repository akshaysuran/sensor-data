package be.hcpl.android.sensors

import android.content.Context
import android.hardware.*
import be.hcpl.android.sensors.core.BaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.databinding.DataBindingUtil
import be.hcpl.android.sensors.databinding.FragmentSensorDataBinding
import java.lang.StringBuilder

/**
 * Example of listening for sensor data
 */
class SensorDataFragment : BaseFragment(), SensorEventListener {
    // again the sensor manager
    private var mSensorManager: SensorManager? = null

    // fixed single sensor reference
    // TODO get this one from the sensor listing fragment instead
    private var mCurrentSensor: Sensor? = null
    private lateinit var mBinding: FragmentSensorDataBinding
    // TODO make speed selectable from UI, same for moving average window and more
    // the textview to show all the received sensor data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // always need a sensor manager
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // retrieve optional information from bundle
        val arguments = arguments
        mCurrentSensor =
            if (arguments != null && arguments.containsKey(KEY_SENSOR_TYPE)) {
                val sensorType = arguments.getInt(KEY_SENSOR_TYPE)
                mSensorManager?.getDefaultSensor(sensorType)
            } else {
                mSensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_data, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // show some information about the selected sensor
        mBinding.textSelectedSensorValue.text = StringBuilder("Selected sensor is: ").append(mCurrentSensor)

        // the clear button
        mBinding.buttonClear.setOnClickListener {
            mBinding.textDataValue.text = ""
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        //float lux = event.values[0];
        // Do something with this sensor value.

        // append all available values on one line
        val sb = StringBuilder()
        for (value in event.values) sb.append(value.toString()).append(" ")

        // and print these
        mBinding.textDataValue.append(sb.append("\r\n"))

        // make sure to never block this method, this will be called on every value update
    }

    override fun onResume() {
        super.onResume()
        // start listening for new values here
        mSensorManager?.registerListener(this, mCurrentSensor, SensorManager.SENSOR_DELAY_NORMAL)
        // we can use faster data delays like: SENSOR_DELAY_GAME (20,000 microsecond delay),
        // SENSOR_DELAY_UI (60,000 microsecond delay), or SENSOR_DELAY_FASTEST
    }

    override fun onPause() {
        super.onPause()

        // never forget to unregister
        mSensorManager?.unregisterListener(this)
    }

    companion object {
        const val KEY_SENSOR_TYPE = "selected_sensor_type"

        /**
         * helper to create an instance of this fragment with the given arguments bundle
         * @param bundle
         * @return
         */
        fun getInstance(bundle: Bundle?): SensorDataFragment {
            val instance = SensorDataFragment()
            instance.arguments = bundle
            return instance
        }
    }
}