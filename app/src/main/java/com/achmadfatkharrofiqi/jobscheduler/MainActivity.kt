package com.achmadfatkharrofiqi.jobscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val JOB_ID = 0
    }

    private val rdgNetworkOptions: RadioGroup by lazy { network_options }

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
        }

        val myJobInfo = builder.build()
        mScheduler!!.schedule(myJobInfo)
        Toast.makeText(this, "Job scheduled, job will run when the constraints are met.", Toast.LENGTH_SHORT).show()
    }

    private fun cancelJobs(){
        if (mScheduler != null) {
            mScheduler!!.cancelAll()
            mScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
