package be.hcpl.android.sensors

import be.hcpl.android.sensors.core.BaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.CheckBox
import android.app.AlarmManager
import android.content.Intent
import be.hcpl.android.sensors.service.SensorBackgroundService
import android.app.PendingIntent
import android.content.Context
import android.view.View
import java.lang.Exception

/**
 * A Fragment for managing the background service
 */
class ScheduleServiceFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO add current state of service

        // add configuration options for service (interval, treshold, sensor, precision, ...)
        val editMin = view.findViewById<View>(R.id.editMin) as EditText
        val editMax = view.findViewById<View>(R.id.editMax) as EditText
        val editInterval = view.findViewById<View>(R.id.editInterval) as EditText
        val chkLogging = view.findViewById<View>(R.id.chkLogging) as CheckBox
        view.findViewById<View>(R.id.btnStart)
            .setOnClickListener { // get scheduler and prepare intent
                val scheduler = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent =
                    Intent(activity?.applicationContext, SensorBackgroundService::class.java)

                // add some extras for config
                val args = Bundle()
                try {
                    val value = editMin.text.toString().toFloat()
                    args.putFloat(SensorBackgroundService.KEY_THRESHOLD_MIN_VALUE, value)
                } catch (e: Exception) {
                    // ignore
                }
                try {
                    val value = editMax.text.toString().toFloat()
                    args.putFloat(SensorBackgroundService.KEY_THRESHOLD_MAX_VALUE, value)
                } catch (e: Exception) {
                    // ignore
                }
                args.putBoolean(SensorBackgroundService.KEY_LOGGING, chkLogging.isChecked)
                intent.putExtras(args)

                // try getting interval option
                val interval: Long
                interval = try {
                    editInterval.text.toString().toLong()
                } catch (e: Exception) {
                    // use the default in that case
                    1000L
                }
                val scheduledIntent = PendingIntent.getService(
                    activity?.applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                // start the service
                scheduler.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    interval,
                    scheduledIntent
                )
            }
        view.findViewById<View>(R.id.btnStop).setOnClickListener {
            val scheduler = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(activity, SensorBackgroundService::class.java)
            val scheduledIntent = PendingIntent.getService(
                activity?.applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            scheduler.cancel(scheduledIntent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ScheduleServiceFragment.
         */
        fun newInstance(): ScheduleServiceFragment {
            //Bundle args = new Bundle();
            //fragment.setArguments(args);
            return ScheduleServiceFragment()
        }
    }
}