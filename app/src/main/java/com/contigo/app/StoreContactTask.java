package com.contigo.app;

import android.os.AsyncTask;

/**
 * Created by Trumpets on 30/11/16.
 */
public class StoreContactTask extends AsyncTask<Void, Integer, Void> {


    @Override
    protected void onPreExecute() {
//Show progress Dialog here

    }


    @Override
    protected void onPostExecute(Void result) {
        //Update UI here if needed
//Dismiss Progress Dialog here
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Do you heavy task here

        return null;
    }

}