package de.michelcoach;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PracticeFragment extends Fragment {
    private static final int REQ_REC = 101;
    private Store store;
    private MediaRecorder rec;
    private File recFile;
    private boolean recording = false;
    private String currentLine = "";

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private final List<String> lines = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_practice, vg, false);
        store = new Store(getContext());

        EditText input = root.findViewById(R.id.edit_practice);
        Button add = root.findViewById(R.id.btn_add);
        Button recBtn = root.findViewById(R.id.btn_rec);
        Button playBtn = root.findViewById(R.id.btn_play);
        Button splitBtn = root.findViewById(R.id.btn_split);
        TextView due = root.findViewById(R.id.due_text);

        lines.clear();
        lines.addAll(store.getLines());
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lines);
        listView = root.findViewById(R.id.practice_list);
        listView.setAdapter(adapter);

        add.setOnClickListener(v -> {
            String line = input.getText().toString().trim();
            if (line.isEmpty()) return;
            lines.add(line);
            store.addLine(line);
            adapter.notifyDataSetChanged();
            input.setText("");
        });

        recBtn.setOnClickListener(v -> {
            if (!recording) startRec(input.getText().toString().trim());
            else stopRec();
        });

        playBtn.setOnClickListener(v -> playLast());

        splitBtn.setOnClickListener(v -> {
            String line = input.getText().toString().trim();
            if (line.isEmpty()) return;
            String[] parts = line.split("\\s+");
            StringBuilder sb = new StringBuilder("خرد شده:\n");
            for (String p : parts) sb.append("• ").append(p).append("\n");
            due.setText(sb.toString());
        });

        // spaced-repetition: show what's due
        refreshDue(due);
        return root;
    }

    private void refreshDue(TextView due) {
        StringBuilder sb = new StringBuilder("یادآوری فاصله‌دار (آماده تمرین):\n");
        long now = System.currentTimeMillis();
        int n = 0;
        for (String l : lines) {
            long next = store.getNextReview(Store.key(l));
            if (next == 0 || next <= now) { sb.append("• ").append(l).append("\n"); n++; }
        }
        if (n == 0) sb.append("(همه به‌روزه)");
        due.setText(sb.toString());
    }

    private void startRec(String line) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO}, REQ_REC);
            currentLine = line;
            return;
        }
        try {
            recFile = new File(getContext().getCacheDir(), "dc_rec.mp3");
            rec = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ? new MediaRecorder(getContext())
                : new MediaRecorder();
            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            rec.setOutputFile(recFile.getAbsolutePath());
            rec.prepare();
            rec.start();
            recording = true;
            currentLine = line;
            Toast.makeText(getContext(), "ضبط شروع شد — بگو و دوباره بزن تا بایسته", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ضبط: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRec() {
        try {
            if (rec != null) { rec.stop(); rec.release(); rec = null; }
            recording = false;
            // schedule next review ~1 day (simple SR)
            if (!currentLine.isEmpty()) {
                store.setNextReview(Store.key(currentLine),
                    System.currentTimeMillis() + 24L * 3600 * 1000);
            }
            Toast.makeText(getContext(), "ضبط شد. با متن مقایسه کن. (پخش: دکمهٔ پخش)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { /* ignore */ }
    }

    private void playLast() {
        if (recFile == null || !recFile.exists()) {
            Toast.makeText(getContext(), "اول ضبط کن", Toast.LENGTH_SHORT).show();
            return;
        }
        android.media.MediaPlayer mp = new android.media.MediaPlayer();
        try {
            mp.setDataSource(recFile.getAbsolutePath());
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(p -> p.release());
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در پخش", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms, @NonNull int[] res) {
        if (req == REQ_REC && res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
            startRec(currentLine);
        }
    }
}
