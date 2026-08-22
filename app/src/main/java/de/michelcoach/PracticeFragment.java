package de.michelcoach;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PracticeFragment extends Fragment {
    private static final int REQ_REC = 101;
    private Store store;
    private MediaRecorder rec;
    private File recFile;
    private boolean recording = false;
    private String currentLine = "";
    private Handler ui = new Handler(Looper.getMainLooper());
    private TextToSpeech tts;

    // split game
    private static final String[] GAME = {
        "Ich möchte Deutsch lernen", "Das ist nicht gut",
        "Wo ist das Restaurant", "Heute lerne ich viel", "Ich trinke nur Wasser"
    };
    private int sbIdx = 0;
    private final List<String> sbAns = new ArrayList<>();

    // mic practice
    private int pIdx = 0;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_practice, vg, false);
        store = new Store(getContext());
        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.GERMAN);
        });
        setupMic(root);
        setupSplit(root);
        setupSR(root);
        return root;
    }

    /* ===== MIC ===== */
    private void setupMic(View root) {
        TextView fa = root.findViewById(R.id.ps_fa);
        TextView de = root.findViewById(R.id.ps_de);
        ProgressBar lvl = root.findViewById(R.id.level_bar);
        TextView stat = root.findViewById(R.id.rec_stat);
        Button tts = root.findViewById(R.id.tts_btn);
        Button recBtn = root.findViewById(R.id.rec_btn);
        Button play = root.findViewById(R.id.play_btn);
        Button next = root.findViewById(R.id.next_sent);

        fa.setText("می‌خواهم آلمانی یاد بگیرم.");
        de.setText(GAME[0]);

        tts.setOnClickListener(v -> {
            if (this.tts != null) this.tts.speak(GAME[pIdx], TextToSpeech.QUEUE_FLUSH, null, "de");
            else Toast.makeText(getContext(), "TTS در دسترس نیست", Toast.LENGTH_SHORT).show();
        });
        recBtn.setOnClickListener(v -> {
            if (!recording) startRec(root); else stopRec(root);
        });
        play.setOnClickListener(v -> playLast());
        next.setOnClickListener(v -> {
            pIdx = (pIdx + 1) % GAME.length;
            de.setText(GAME[pIdx]);
            stopRec(root);
        });
    }

    private void startRec(View root) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO}, REQ_REC);
            return;
        }
        try {
            recFile = new File(getContext().getCacheDir(), "dc_rec.mp3");
            rec = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ? new MediaRecorder(getContext()) : new MediaRecorder();
            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            rec.setAudioEncodingBitRate(192000);
            rec.setAudioSamplingRate(44100);
            rec.setOutputFile(recFile.getAbsolutePath());
            rec.prepare(); rec.start();
            recording = true; currentLine = "practice";
            ((Button) root.findViewById(R.id.rec_btn)).setText("توقف");
            Toast.makeText(getContext(), "ضبط شروع شد", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ضبط", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRec(View root) {
        try {
            if (rec != null) { rec.stop(); rec.release(); rec = null; }
            recording = false;
            ((Button) root.findViewById(R.id.rec_btn)).setText("ضبط");
            ((Button) root.findViewById(R.id.play_btn)).setEnabled(true);
            if (!currentLine.isEmpty())
                store.setNextReview(Store.key(currentLine), System.currentTimeMillis() + 24L * 3600 * 1000);
            Toast.makeText(getContext(), "ضبط شد — با متن مقایسه کن", Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {}
    }

    private void playLast() {
        if (recFile == null || !recFile.exists()) {
            Toast.makeText(getContext(), "اول ضبط کن", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            android.media.MediaPlayer mp = new android.media.MediaPlayer();
            mp.setDataSource(recFile.getAbsolutePath());
            mp.prepare(); mp.start();
            mp.setOnCompletionListener(p -> p.release());
        } catch (Exception e) { Toast.makeText(getContext(), "خطا در پخش", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] res) {
        if (req == REQ_REC && res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED)
            startRec(getView());
    }

    /* ===== SPLIT GAME ===== */
    private void setupSplit(View root) {
        renderSplit(root);
        root.findViewById(R.id.sb_reset).setOnClickListener(v -> renderSplit(root));
        root.findViewById(R.id.sb_next).setOnClickListener(v -> {
            sbIdx = (sbIdx + 1) % GAME.length; renderSplit(root);
        });
    }

    private void renderSplit(View root) {
        TextView fa = root.findViewById(R.id.sb_fa);
        LinearLayout slots = root.findViewById(R.id.sb_slots);
        LinearLayout chips = root.findViewById(R.id.sb_chips);
        Button next = root.findViewById(R.id.sb_next);
        sbAns.clear();
        fa.setText("این جملهٔ آلمانی رو بساز: " + GAME[sbIdx]);
        next.setEnabled(false);

        String[] words = GAME[sbIdx].split(" ");
        List<String> sh = new ArrayList<>(java.util.Arrays.asList(words));
        java.util.Collections.shuffle(sh);
        slots.removeAllViews();
        chips.removeAllViews();
        for (String w : sh) {
            Button c = new Button(getContext());
            c.setText(w); c.setTextSize(14);
            c.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBBDEFB));
            c.setTextColor(0xFF0D47A1);
            c.setOnClickListener(v -> {
                sbAns.add(w); c.setVisibility(View.INVISIBLE);
                TextView s = new TextView(getContext());
                s.setText(w); s.setPadding(6, 8, 6, 8); s.setTextSize(14); s.setTextColor(0xFF0D47A1);
                slots.addView(s);
                if (sbAns.size() == words.length) {
                    if (String.join(" ", sbAns).equals(GAME[sbIdx])) {
                        next.setEnabled(true);
                        Toast.makeText(getContext(), "آفرین! درسته", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "نزدیکی! فعل همیشه دومه", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            chips.addView(c);
        }
    }

    /* ===== SPACED REPETITION ===== */
    private void setupSR(View root) {
        LinearLayout today = root.findViewById(R.id.srs_today);
        LinearLayout next = root.findViewById(R.id.srs_next);
        List<String> lines = store.getLines();
        long now = System.currentTimeMillis();
        int due = 0;
        for (String l : lines) {
            long nr = store.getNextReview(Store.key(l));
            if (nr == 0 || nr <= now) {
                due++;
                TextView t = new TextView(getContext());
                t.setText("• " + l); t.setTextSize(14); t.setTextColor(0xFF0D47A1);
                today.addView(t);
            } else {
                TextView t = new TextView(getContext());
                t.setText("• " + l + " (فردا)"); t.setTextSize(13); t.setTextColor(0xFF5A6B7D);
                next.addView(t);
            }
        }
        if (due == 0) {
            TextView t = new TextView(getContext());
            t.setText("(همه به‌روزه)"); t.setTextSize(13); t.setTextColor(0xFF5A6B7D);
            today.addView(t);
        }
    }
}
