package com.achmadfatkharrofiqi.jobscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val JOB_ID = 0
    }

    private val rdgNetworkOptions: RadioGroup by lazy { network_options }

    private val mSeekBar: SeekBar by lazy { seekbar }
    private val seekBarProgress: TextView by lazy { seekbar_progress }
    private val mDeviceIdleSwitch: Switch by lazy { idle_switch }
    private val mDeviceChargingSwitch: Switch by lazy { charging_switch }
    private val btnCancelJobs: Button by lazy { btn_cancel_jobs }
    private val btnScheduleJob: Button by lazy { btn_schedule_job }

    private var mScheduler: JobScheduler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScheduleJob.setOnClickListener {
            scheduleJob()
        }

        btnCancelJobs.setOnClickListener {
            cancelJobs()
        }

        mSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > 0){
                    seekBarProgress.text = "$progress s"
                }else {
                    seekBarProgress.text = "Not Set"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun scheduleJob() {
        val selectedNetworkId = rdgNetworkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE

        when(selectedNetworkId){
            R.id.no_network -> {
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            }
            R.id.any_network -> {
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            }
            R.id.wifi_network -> {
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
            }
        }

        mScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val serviceName = ComponentName(
            packageName,
            NotificationJobService::class.java.name
        )

        val builder = JobInfo.Builder(JOB_ID, serviceName)
        builder.apply {
            setRequiredNetworkType(selectedNetworkOption)
            setRequiresCharging(mDeviceChargingSwitch.isChecked)
            setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked)
        }

        val seekBarInteger = mSeekBar.progress
        val seekBarSet = seekBarInteger > 0
        if (seekBarSet) {
            builder.setOverrideDeadline((seekBarInteger * 1000).toLong())
        }

        val constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE || mDeviceChargingSwitch.isChecked || mDeviceIdleSwitch.isChecked || seekBarSet
        if (constraintSet) {
            val myJobInfo = builder.build()
            mScheduler!!.schedule(myJobInfo)
            Toast.makeText(this, "Job scheduled, job will run when the constraints are met.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show();
        }

    }

    private fun cancelJobs(){
        if (mScheduler != null) {
            mScheduler!!.cancelAll()
            mScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
