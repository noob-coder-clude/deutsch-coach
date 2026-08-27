package de.macht.app.ui;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.macht.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PracticeFragment extends Fragment implements TextToSpeech.OnInitListener {

    private TextView tvSentence;
    private TextView tvParts;
    private Button btnSpeak;
    private Button btnNext;
    private Button btnMode;
    private TextToSpeech tts;
    private int mode = 0; // 0=word, 1=half, 2=two-part
    private final String[] MODES = {"کلمه‌به‌کلمه", "نیمه‌نیمه", "دو تکه"};

    // Sample hard-marked sentences (from course). Real data would come from Room.
    private final String[] HARD = {
        "Können Sie mit mir kommen?",
        "Wann wollen Sie hier sein?",
        "Warum haben Sie es noch nicht gekauft?",
        "Ich muss bald gehen.",
        "Ich habe es gesucht, aber ich konnte es nicht finden."
    };
    private int idx = 0;
    private final Random rnd = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_practice, c, false);

        tvSentence = v.findViewById(R.id.tv_sentence);
        tvParts = v.findViewById(R.id.tv_parts);
        btnSpeak = v.findViewById(R.id.btn_speak);
        btnNext = v.findViewById(R.id.btn_next);
        btnMode = v.findViewById(R.id.btn_mode);

        tts = new TextToSpeech(requireContext(), this);

        showCurrent();

        btnSpeak.setOnClickListener(view -> speak(tvSentence.getText().toString()));
        btnMode.setOnClickListener(view -> {
            mode = (mode + 1) % 3;
            btnMode.setText("حالت: " + MODES[mode]);
            showCurrent();
        });
        btnNext.setOnClickListener(view -> {
            idx = rnd.nextInt(HARD.length);
            showCurrent();
        });

        return v;
    }

    private void showCurrent() {
        if (tvSentence == null) return;
        String sentence = HARD[idx];
        tvSentence.setText(sentence);
        tvParts.setText(buildParts(sentence, mode));
    }

    private String buildParts(String sentence, int m) {
        // Split into words, keep punctuation attached to preceding word.
        String[] raw = sentence.trim().split("\\s+");
        List<String> words = new ArrayList<>();
        for (String w : raw) {
            words.add(w);
        }

        if (m == 0) {
            // word by word
            StringBuilder sb = new StringBuilder();
            for (String w : words) sb.append(w).append("\n");
            return sb.toString().trim();
        }

        int n = words.size();
        if (n <= 1) return sentence;

        if (m == 1) {
            // half splits: pair consecutive words
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i += 2) {
                if (i + 1 < n) sb.append(words.get(i)).append(" ").append(words.get(i + 1)).append("\n");
                else sb.append(words.get(i)).append("\n");
            }
            return sb.toString().trim();
        }

        // m == 2: two parts (first half, second half)
        int mid = n / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mid; i++) sb.append(words.get(i)).append(" ");
        sb.append("\n");
        for (int i = mid; i < n; i++) sb.append(words.get(i)).append(" ");
        return sb.toString().trim();
    }

    private void speak(String text) {
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            tts.setLanguage(Locale.GERMAN);
        }
    }

    @Override
    public void onDestroyView() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroyView();
    }
}
