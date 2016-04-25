package ecen.devicecontroller;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.os.Build;
import android.database.Cursor;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.*;

// Source: http://codetheory.in/android-sms/

public class MainActivity extends AppCompatActivity {

    int lastIndex;
    SmsMessage[] msgs = null;
    String contact = null;

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

            //SmsMessage[] msgs = null;

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
                lastIndex = msgs.length - 1;

                // First message in array
                textMessage.setText(msgs[lastIndex].getMessageBody());
                textAddress.setText(msgs[lastIndex].getOriginatingAddress());
                phoneNumber = msgs[lastIndex].getOriginatingAddress();

                if (isConnected) {
                    byte[] data = {0x74};
                    bluetooth.send(data, false);
                    new CountDownTimer(500, 500) {
                        @Override
                        public void onFinish() {
                            bluetooth.send(getContactName(msgs[lastIndex].getOriginatingAddress()), false);
                            new CountDownTimer(500, 500) {
                                @Override
                                public void onFinish() {
                                    bluetooth.send(msgs[lastIndex].getMessageBody(), false);
                                }

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }
                            }.start();
                        }

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                    }.start();
                }

            }
        }
    };

    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                contact = getContactName(incomingNumber);
                textMessage.setText("Incoming Call");
                textAddress.setText(contact);

                if (isConnected) {
                    byte[] data = {0x70};
                    bluetooth.send(data, false);
                    new CountDownTimer(500, 500) {
                        @Override
                        public void onFinish() {
                            bluetooth.send(contact, false);
                        }

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                    }.start();
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

        // Text Messages
        this.registerReceiver(receiver, filter);
        smsManager = SmsManager.getDefault();

        // Phone Calls
        TelephonyManager manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        // Bluetooth
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

        // Reading data
        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                //
            }
        });

        // Send initial time data
        if (isConnected) {
            // Send special character and time info, device control one.
            //byte[] data = {0x11}
            //bluetooth.send();
        }

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

    // Phone number to name converter
    public String getContactName(final String phoneNumber)
    {
        Uri uri;
        String[] projection;

        if (Build.VERSION.SDK_INT >= 5)
        {
            uri = Uri.parse("content://com.android.contacts/phone_lookup");
            projection = new String[] { "display_name" };
        }
        else
        {
            uri = Uri.parse("content://contacts/phones/filter");
            projection = new String[] { "name" };
        }

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);
        }

        cursor.close();
        cursor = null;

        if (contactName.equals("")) return phoneNumber;

        return contactName;
    }

}
