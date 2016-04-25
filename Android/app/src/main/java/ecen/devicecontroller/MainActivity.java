package ecen.devicecontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.*;

// Source: http://codetheory.in/android-sms/

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bluetooth;

    private String phoneNumber = null;
    private SmsManager smsManager;

    private TextView textMessage, textAddress, textStatus;
    private EditText text1, text2, text3;

    // Bool used to check if bluetooth is connected before data is sent
    boolean isConnected = false;

    private IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the data (SMS data) bound to intent
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            if (bundle != null) {
                // Retrieve the SMS Messages received
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];

                // For every SMS message received
                for (int i=0; i < msgs.length; i++) {
                    // Convert Object array
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                // Index of last message
                int lastIndex = msgs.length - 1;

                // First message in array
                textMessage.setText(msgs[lastIndex].getMessageBody());
                textAddress.setText(msgs[lastIndex].getOriginatingAddress());
                phoneNumber = msgs[lastIndex].getOriginatingAddress();

                if (isConnected) {
                    byte[] data = {0x74};
                    bluetooth.send(data, false);
                    bluetooth.send(msgs[lastIndex].getOriginatingAddress(), false);
                    bluetooth.send(msgs[lastIndex].getMessageBody(), false);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMessage = (TextView) findViewById(R.id.textMessage);
        textAddress = (TextView) findViewById(R.id.textAddress);
        textStatus = (TextView) findViewById(R.id.textStatus);
        text1 = (EditText) findViewById(R.id.editText1);
        text2 = (EditText) findViewById(R.id.editText2);
        text3 = (EditText) findViewById(R.id.editText3);

        this.registerReceiver(receiver, filter);
        smsManager = SmsManager.getDefault();

        bluetooth = new BluetoothSPP(getApplicationContext());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
                bluetooth.connect(data);
            }
        }
    }

    public void connect(View view) {
        if (!bluetooth.isBluetoothEnabled() || !bluetooth.isBluetoothAvailable()) return;
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Connected");
                isConnected = true;
            }

            @Override
            public void onDeviceDisconnected() {
                textStatus.setText("Disconnected");
                isConnected = false;
            }

            @Override
            public void onDeviceConnectionFailed() {
                textStatus.setText("Failed");
                isConnected = false;
            }
        });
    }

    public void reply1(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, text1.getText().toString(), null, null);
    }

    public void reply2(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, text2.getText().toString(), null, null);
    }

    public void reply3(View view) {
        if (phoneNumber != null) smsManager.sendTextMessage(phoneNumber, null, text3.getText().toString(), null, null);
    }

}
