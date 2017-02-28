package com.udacity.stockhawk.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Mahendran on 18-02-2017.
 */

public class StockNotFoundBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Stock Not Found",Toast.LENGTH_SHORT).show();
    }
}
