package crixec.app.howmuchdb;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable {
    private MediaRecorder mRecorder = null;
    private TextView view;
    private boolean stop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new TextView(this);
        view.setText("0  dB");
        view.setTextSize(40);
        view.setGravity(Gravity.CENTER);
        setContentView(view);
        new Thread(this).start();
    }

    @Override
    protected void onDestroy() {
        // TODO: Implement this method
        super.onDestroy();
        stop = true;
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
        }
    }

    @Override
    public void run() {
        stop = false;
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(1);
            mRecorder.setOutputFormat(1);
            mRecorder.setAudioEncoder(1);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
            while (true) {
                if (stop) break;
                final int size = (int) (20 * Math.log10(mRecorder.getMaxAmplitude()));
                Thread.sleep(200);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (size > 0)
                            view.setText(size + "  dB");
                    }
                });
            }
        } catch (Exception e) {
        }
    }
}
