package ecen.devicecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// Source: http://codetheory.in/android-sms/

public class MainActivity extends AppCompatActivity {

    private String phoneNumber = null;
    private SmsManager smsManager;

    private TextView textMessage;
    private TextView textAddress;
    private EditText textCustom;

    private IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
    private String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the data (SMS data) bound to intent
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            String str = "";

            if (bundle != null) {
                // Retrieve the SMS Messages received
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];

                // For every SMS message received
                for (int i=0; i < msgs.length; i++) {
                    // Convert Object array
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    // Sender's phone number
                    str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                    // Fetch the text message
                    str += msgs[i].getMessageBody().toString();
                    // Newline <img src="http://codetheory.in/wp-includes/images/smilies/simple-smile.png" alt=":-)" class="wp-smiley" style="height: 1em; max-height: 1em;">
                    str += "\n";
                }

                // Display the entire SMS Message
                Log.d(TAG, str);

                textMessage.setText(msgs[0].getMessageBody());
                textAddress.setText(msgs[0].getOriginatingAddress());
                phoneNumber = msgs[0].getOriginatingAddress();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textAddress = (TextView) findViewById(R.id.textAddress);
        textCustom = (EditText) findViewById(R.id.editTextCustom);

        this.registerReceiver(receiver, filter);
        smsManager = SmsManager.getDefault();
    }

    public void replyYes(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, "Yes", null, null);
    }

    public void replyNo(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, "No", null, null);
    }

    public void replyCustom(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, textCustom.getText().toString(), null, null);
    }

}
