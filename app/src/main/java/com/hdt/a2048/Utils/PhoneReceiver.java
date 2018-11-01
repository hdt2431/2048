package com.hdt.a2048.Utils;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hdt.a2048.Activity.MainActivity;
import com.hdt.a2048.R;


public class PhoneReceiver extends BroadcastReceiver {
    String TAG = "PhoneReceiver";
    private MainActivity activity;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("action" + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "call OUT:" + phoneNumber);
        } else {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    activity = (MainActivity) SysApplication.getInstance().getActivity();
                    activity.setPause(false);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    activity = (MainActivity) SysApplication.getInstance().getActivity();
                    activity.setPause(true);
                    break;
            }
        }
    };
}
