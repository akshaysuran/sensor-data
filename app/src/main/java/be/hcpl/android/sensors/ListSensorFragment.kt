package be.hcpl.android.sensors

import android.content.Context
import android.hardware.Sensor
import be.hcpl.android.sensors.core.BaseFragment
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import be.hcpl.android.sensors.databinding.FragmentListSensorsBinding

/**
 * A fragment that checks all available sensors for this device
 */
class ListSensorFragment : BaseFragment() {
    // you'll always need a reference to the sensorManager
    private var mSensorManager: SensorManager? = null
    private lateinit var mBinding: FragmentListSensorsBinding
    // and we will populate this list
    private var deviceSensors: List<Sensor>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // we need to retrieve the system service on the parent activity
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // this is how to list all sensors
        deviceSensors = mSensorManager?.getSensorList(Sensor.TYPE_ALL)

        // check for a specific type of sensor
        //if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
        // Success! There's a magnetometer.
        //}
        //else {
        // Failure! No magnetometer.
        //}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_sensors, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO list the information here
        // get the listview from our layout
        val listView = view.findViewById<View>(R.id.list_sensors) as ListView

        // and populate it with the most basic view available for listviews, a single text view
        // only made final so we can refer to it in our anonymuous innerclass for clickListener impl
        val listAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, deviceSensors!!.toTypedArray())
        // set it to our listview
        listView.adapter = listAdapter

        // on click we want to open the sensor data fragment with information about the selected
        // sensor
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->

            // and pass it on to the data fragment in a bundle
            val bundle = Bundle()
            bundle.putInt(SensorDataFragment.KEY_SENSOR_TYPE, listAdapter.getItem(i)!!.type)
            parentActivity?.switchFragment(SensorDataFragment.getInstance(bundle))
        }
    }
}