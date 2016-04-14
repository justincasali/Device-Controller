package ecen.devicecontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import android.media.session.MediaSessionManager;
import android.media.session.MediaController;
import android.media.MediaMetadata;

// Needs
// android.Manifest.permission.MEDIA_CONTENT_CONTROL



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

        // Sets up music title text
        title = (TextView) findViewById(R.id.textTitle);

        // Sets up media manager
        mediaManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);

        // Sets up media controller from the 0th active session
        controller = mediaManager.getActiveSessions(null).get(1);

        // Sets up metadata
        metadata = controller.getMetadata();

    }
    /*
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
    */
}
