package dev.nathalie.delayalarm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_delay_alarm.*
import java.net.URL
import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.Klaxon
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.*
import java.util.*
import java.text.DateFormat
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import android.widget.AdapterView.OnItemSelectedListener

typealias TrainStops = List<String>

class AddDelayAlarm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_delay_alarm)

        createDelayAlarm.setOnClickListener { _ ->
            val intent = Intent()
            intent.putExtra("alarmtrainnumber", trainNumberInput.text.toString())
            intent.putExtra("alarmhour", timeAlarmInput.text.toString())
            intent.putExtra("alarmstation", spinner.selectedItem.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        trainNumberInput.setOnFocusChangeListener { _, b ->
            if (!b) {
                RetrieveTrainTask(trainNumberInput.text.toString(), trainNumberInfo, spinner).execute()
            }
        }

        timeAlarmInput.setOnFocusChangeListener { _, b ->
            if (b) {
                val cal = Calendar.getInstance()

                val timePicker = TimePickerDialog(
                    this, timePickerListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
                timePicker.setCancelable(false)
                timePicker.setTitle("Select the hour for the alarm")
                timePicker.show()
            }
        }
    }

    // Get information from train number and display it on screen
    private class RetrieveTrainTask internal constructor(val trainNumber: String,
                                                         val updateField: TextView,
                                                         val spinner: Spinner
    ) : AsyncTask<Void, Void, TrainStops>() {
        override fun doInBackground(vararg params: Void): TrainStops {
            try {
                //TODO change date to today (or recent weekday as P-trains do not run in the weekends)
                val trainJSON: String = URL("https://api.irail.be/vehicle/?id=BE.NMBS.$trainNumber&date=170619&fast=false&format=json&lang=en&alerts=false").readText()
                val result = Klaxon().parse<TrainInfo>(trainJSON)
                val stops = result?.stops?.stop

                //TODO the "!!" is dangerous, put if-statements to verify
                return stops!!.map { it.station }

            } catch (e: Exception) {
                return listOf("unknown", "unknown")
            }
        }

        override fun onPostExecute(result: TrainStops) {
            // Provide general train information
            val departStation = result.first()
            val arriveStation = result.last()
            updateField.text =
                "Train from $departStation to $arriveStation"

            // Show list of stops for train
            val adapter = ArrayAdapter<String>(spinner.context, android.R.layout.simple_spinner_dropdown_item, result)
            spinner.adapter = adapter
            spinner.visibility = View.VISIBLE
        }

        override fun onPreExecute() {
            updateField.text = "Loading..."
        }
    }

    private val timePickerListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val hour1 = hourOfDay.toString()
            val minute1 = minute.toString()
            timeAlarmInput.setText("$hour1:$minute1")
        }

    companion object {
        // TODO: kan meer Java-unlike
        fun newIntent(context: Context): Intent {
            return Intent(context, AddDelayAlarm::class.java)
        }
    }

    data class TrainInfo(
        val stops: Stops,
        val timestamp: String,
        val vehicle: String,
        val vehicleinfo: Vehicleinfo,
        val version: String
    )

    data class Vehicleinfo(
        //@id : String,
        val locationX: String,
        val locationY: String,
        val name: String,
        val shortname: String
    )

    data class Stops(
        val number: String,
        val stop: List<Stop>
    )

    data class Stop(
        val arrivalCanceled: String,
        val arrivalDelay: String,
        val canceled: String,
        val delay: String,
        val departureCanceled: String,
        val departureConnection: String,
        val departureDelay: String,
        val id: String,
        val isExtraStop: String,
        val left: String,
        val occupancy: Occupancy,
        val platform: String,
        val platforminfo: Platforminfo,
        val scheduledArrivalTime: String,
        val scheduledDepartureTime: String,
        val station: String,
        val stationinfo: Stationinfo,
        val time: String
    )

    data class Occupancy(
        //@id : String,
        val name: String
    )

    data class Stationinfo(
        //@id : String,
        val id: String,
        val locationX: String,
        val locationY: String,
        val name: String,
        val standardname: String
    )

    data class Platforminfo(
        val name: String,
        val normal: String
    )

}
