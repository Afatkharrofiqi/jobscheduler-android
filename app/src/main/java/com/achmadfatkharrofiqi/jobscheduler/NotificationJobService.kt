package com.achmadfatkharrofiqi.jobscheduler

import android.app.job.JobParameters
import android.app.job.JobService

class NotificationJobService : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        return true
    }
}