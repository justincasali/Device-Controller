package ecen.devicecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.media.session.MediaSessionManager;
import android.media.session.MediaController;
import android.media.MediaMetadata;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView title;

    MediaSessionManager mediaManager;
    MediaController controller;
    MediaController.TransportControls transport;
    MediaMetadata metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.textTitle);

        mediaManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        controller = mediaManager.getActiveSessions(null).get(0); // Get first media controller?
        transport = controller.getTransportControls();
        metadata = controller.getMetadata();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlay: transport.play();
            case R.id.buttonPause: transport.pause();
            case R.id.buttonNext: transport.skipToNext();
            case R.id.buttonPrevious: transport.skipToPrevious();
            break;
        }
        title.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
    }
}
