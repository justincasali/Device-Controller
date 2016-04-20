package ecen.devicecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.provider.Telephony;
import android.telephony.SmsMessage;

import android.widget.Toast;

/**
 * Created by justincasali on 4/19/16.
 */
public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String text = messages[0].getDisplayMessageBody();
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

}
