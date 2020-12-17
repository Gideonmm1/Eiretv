package com.eiretv.setup.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Calendar rebootTime = Calendar.getInstance();
            rebootTime.set(Calendar.HOUR_OF_DAY, 4);
            rebootTime.set(Calendar.MINUTE, 0);
            rebootTime.set(Calendar.SECOND, 0);
            rebootTime.set(Calendar.MILLISECOND, 0);

            // If reboot time is in the past then add 1 day.
            if (rebootTime.before(Calendar.getInstance())) {
                rebootTime.add(Calendar.DATE, 1);
            }

            // Schedule the nightly reboot
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent rebootIntent = new Intent(context, NightlyReboot.class);
            PendingIntent pendingRebootIntent = PendingIntent.getBroadcast(context, 0, rebootIntent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, rebootTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingRebootIntent);
        }
        //Starts main activity when device completes boot
        Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
        context.startActivity(i);
    }
}
