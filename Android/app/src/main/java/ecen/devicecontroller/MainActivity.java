package ecen.devicecontroller;
import java.util.Calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.app.Service;



public class MainActivity extends AppCompatActivity {
    TextView text;
    TextView text2;

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
                text2.setText(incomingNumber);
            }
        }
    };

}
