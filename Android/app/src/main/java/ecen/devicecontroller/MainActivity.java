package ecen.devicecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.provider.Telephony;

import android.content.BroadcastReceiver;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView text;

    public BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0].getMessageBody();
            text.setText(message);
        }
    };

    public IntentFilter intent_filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(broadcast_receiver, intent_filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcast_receiver);
    }

}
