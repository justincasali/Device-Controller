package ecen.devicecontroller;
import java.util.Calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.app.Service;
import android.net.Uri;
import android.database.Cursor;
import android.os.Build;



public class MainActivity extends AppCompatActivity {
    TextView text;
    TextView text2;
    String contact;

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

        return contactName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        text = (TextView) findViewById(R.id.textView2);
        text2 = (TextView) findViewById(R.id.textView);
        text.setText(hour+":"+min);

        TelephonyManager manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber){
            if (state == TelephonyManager.CALL_STATE_RINGING){
                contact = getContactName(incomingNumber);
                text2.setText(contact);
            }
        }
    };

}
