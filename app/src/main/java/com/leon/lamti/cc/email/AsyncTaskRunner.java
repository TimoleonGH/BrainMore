package com.leon.lamti.cc.email;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String resp = "resp";

    @Override
    protected String doInBackground(String... params) {
        /*publishProgress("Sleeping..."); // Calls onProgressUpdate()
        try {
            // Do your long operations here and return the result
            int time = Integer.parseInt(params[0]);
            // Sleeping for given time period
            Thread.sleep(time);
            resp = "Slept for " + time + " milliseconds";
        } catch (InterruptedException e) {
            e.printStackTrace();
            resp = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
        }*/

        String message = params[0];

        if ( message == null ) {

            message = "message=null";
        }
        
        try {
            GMailSender sender = new GMailSender("timolabrino@gmail.com", "mark1230&&31");
            sender.sendMail("Send email to admin",
                    message,
                    "timolabrino@gmail.com",
                    "tsoumilion@gmail.com");
            Log.e("SendMail", "message send");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

        return message;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        //finalResult.setText(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        // Things to be done before execution of long running operation. For
        // example showing ProgessDialog
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(String... text) {
        //finalResult.setText(text[0]);
        // Things to be done while execution of long running operation is in
        // progress. For example updating ProgessDialog
    }
}