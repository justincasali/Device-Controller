package ecen.devicecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textMessage);
    }

}
