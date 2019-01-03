package raivio.kaappo.equalizer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Equalizer equalizer;

    public List<VerticalSeekBar> sliders;
    public static MainActivity context;

    private int[] vals = new int[8];
    private final int[] bands = {
        125,
        250,
        500,
        1000,
        2000,
        4000,
        8000,
        16000
    };

    private int audioSessionID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sliders = Arrays.asList(
            (VerticalSeekBar) findViewById(R.id.slider_1),
            (VerticalSeekBar) findViewById(R.id.slider_2),
            (VerticalSeekBar) findViewById(R.id.slider_3),
            (VerticalSeekBar) findViewById(R.id.slider_4),
            (VerticalSeekBar) findViewById(R.id.slider_5),
            (VerticalSeekBar) findViewById(R.id.slider_6),
            (VerticalSeekBar) findViewById(R.id.slider_7),
            (VerticalSeekBar) findViewById(R.id.slider_8)

        );

        audioSessionID = new MediaPlayer().getAudioSessionId();

        context = this;
        equalizer = new Equalizer(10, audioSessionID);
        unbindSystemEqualizer();
        equalizer.setEnabled(true);



    }



    private  void unbindSystemEqualizer() {
        Intent intent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionID);
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);
    }

    public void equalizeOneFrequency (int freq, int value) {
        System.out.println("Equalizing now: " + freq + ", " + (short) value);
        short band = equalizer.getBand(freq);
//        equalizer.setBandLevel(band, (short) 0);
        equalizer.usePreset((short)2);

        short[] asd = equalizer.getBandLevelRange();
        for (short a : asd) {
            System.out.print(a + " ");
        }

        for (int i = 0; i < asd.length; i++) {
//            System.out.print(i + " ");
            sliders.get(i).setProgress(asd[i]);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        equalizer.release();
    }

    public static void equalize (VerticalSeekBar changed) {
        StringBuilder builder = new StringBuilder();

        int index = Integer.parseInt((String) changed.getTag());

        context.vals[index] = changed.getProgress() - 50;



        for (int i = 0; i < context.vals.length; i++) {
            builder.append(i).append(": ").append(context.vals[i]).append("\n");
        }

        System.out.println(builder.toString());

        context.equalizeOneFrequency(context.bands[index], context.vals[index]);
    }


}
