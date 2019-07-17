package dev.nathalie.delayalarm

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        addAlarmButton.setOnClickListener { _ ->
            val intent = AddDelayAlarm.newIntent(this)
            startActivityForResult(intent, GET_TRAIN_ALARM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_TRAIN_ALARM) {
            if (resultCode == Activity.RESULT_OK) {
                //TODO process data
                Log.d("nathalielog", data.toString())
            }
        }
    }

    companion object {
        // TODO: kan meer Java-unlike
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        const val GET_TRAIN_ALARM = 1;
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
