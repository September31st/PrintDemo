//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.asynncTask;

import android.os.AsyncTask;

import com.print.posprinterface.BackgroundInit;
import com.print.posprinterface.UiExecute;


public class PosAsyncTask extends AsyncTask<Void, Void, Boolean> {
    UiExecute execute;
    BackgroundInit init;

    public PosAsyncTask(UiExecute execute, BackgroundInit init) {
        this.execute = execute;
        this.init = init;
    }

    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        result = this.init.doInBackground();
        return result;
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            this.execute.onsucess();
        } else {
            this.execute.onfailed();
        }

    }
}
