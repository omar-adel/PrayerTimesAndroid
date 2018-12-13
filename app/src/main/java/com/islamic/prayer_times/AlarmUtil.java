package com.islamic.prayer_times;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import static android.content.Context.ALARM_SERVICE;

public class AlarmUtil {

    public static void scheduleAlarm(Context context, PendingIntent pendingIntent , long date)
    {
         AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        if (null != alarmMgr) {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
            else if (Build.VERSION.SDK_INT >= 19) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP,date, pendingIntent);
            }
            else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
         }
    }

}
