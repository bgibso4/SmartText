package com.example.ben.smarttext;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateNewText extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_text);



        final ComponentName componentName = new ComponentName(this, SMSJobService.class);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageButton sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String num= "5197028412";

                TextInputEditText m = findViewById(R.id.newMessage);
                String message= m.getText().toString();
                //String time= texts.getString("Time", "InvalidTime");
                boolean x = false;
                if(x){

                }
                //TODO impliment the time feature and then enable this check
//                else if(time.equals("InvalidTime")){
//                    Snackbar.make(view, "Invalid Time set", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                }
                else{
                    //schedule job
                    JSONObject textInfo= new JSONObject();
                    String[] textInf= {num, message};
                    try{
                        textInfo.put("phoneNum", num);
                        textInfo.put("message", message);

                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }

                    //check these
                    PersistableBundle bundle = new PersistableBundle();
                    bundle.putStringArray("TextInfo", textInf);
                    //bundle.putString("TextInfo", textInfo.toString());
                    final JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                            .setMinimumLatency(5000)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                            .setExtras(bundle)
                            .build();
                    JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
                    int resultCode = jobScheduler.schedule(jobInfo);
                    if (resultCode == JobScheduler.RESULT_SUCCESS) {
                        Log.d("Message", "Job scheduled!");
                    } else {
                        Log.d("Message", "Job not scheduled");
                    }

                }
            }
        });
    }
}
