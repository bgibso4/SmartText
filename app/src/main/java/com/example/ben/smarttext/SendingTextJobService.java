package com.example.ben.smarttext;

import android.app.job.JobParameters;
import android.app.job.JobService;


public class SendingTextJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
