package com.leon.lamti.cc.email;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

public class IntentEmailService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";

    public IntentEmailService() {
        super("IntentEmailService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String message = intent.getStringExtra("message");

        try {
            GMailSender sender = new GMailSender("timolabrino@gmail.com", "mark1230&&31");
            sender.sendMail("Send email to admin",
                    message,
                    "timolabrino@gmail.com",
                    "timolabrino@gmail.com");
            Log.e("SendMail", "message send");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }



        /*String msg = intent.getStringExtra(PARAM_IN_MSG);
        SystemClock.sleep(30000); // 30 seconds
        String resultTxt = msg + " "
                + DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis());*/
    }
}
