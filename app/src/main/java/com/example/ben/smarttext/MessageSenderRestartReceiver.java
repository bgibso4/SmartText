package com.example.ben.smarttext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageSenderRestartReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MessageSenderRestartReceiver.class.getSimpleName(), "Service has stopped!");
        context.startForegroundService(new Intent(context, SendingService.class));
    }
}
