package de.michelcoach;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {
    private JSONObject course;
    private Store store;
    private int curLekt = 1;
    private boolean hideDe = true;
    private LinearLayout list;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_book, vg, false);
        store = new Store(getContext());
        loadCourse();
        hideDe = store.getBool("no_cheat", true);

        // mask switch
        View sw = root.findViewById(R.id.mask_sw);
        sw.setBackgroundResource(0);
        sw.setBackgroundColor(hideDe ? 0xFF1565C0 : 0xFFD6DEE8);
        sw.setOnClickListener(v -> {
            hideDe = !hideDe;
            store.putBool("no_cheat", hideDe);
            sw.setBackgroundColor(hideDe ? 0xFF1565C0 : 0xFFD6DEE8);
            render(root);
        });

        list = root.findViewById(R.id.word_list);
        root.findViewById(R.id.l_prev).setOnClickListener(v -> {
            if (curLekt > 1) { curLekt--; store.setPhase(curLekt); render(root); }
        });
        root.findViewById(R.id.l_next).setOnClickListener(v -> {
            int max = course.optJSONArray("lektions").length();
            if (curLekt < max) { curLekt++; store.setPhase(curLekt); render(root); }
        });

        render(root);
        return root;
    }

    private void loadCourse() {
        try {
            InputStream is = getContext().getAssets().open("course.json");
            int size = is.available();
            byte[] buf = new byte[size];
            is.read(buf); is.close();
            course = new JSONObject(new String(buf, StandardCharsets.UTF_8));
        } catch (Exception e) { course = new JSONObject(); }
    }

    private void render(View root) {
        // pills
        LinearLayout pills = root.findViewById(R.id.lpills);
        pills.removeAllViews();
        JSONArray ls = course.optJSONArray("lektions");
        for (int i = 0; i < ls.length(); i++) {
            int num = ls.optJSONObject(i).optInt("num");
            Button p = new Button(getContext());
            p.setText(fa(num));
            p.setTextSize(12.5f); p.setTypeface(null, android.graphics.Typeface.BOLD);
            if (num == curLekt) p.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
            else { p.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF)); ((Button)p).setTextColor(0xFF5A6B7D); }
            final int n = num;
            p.setOnClickListener(v -> { curLekt = n; store.setPhase(n); render(root); });
            pills.addView(p);
        }

        // head
        JSONObject L = null;
        for (int i = 0; i < ls.length(); i++)
            if (ls.optJSONObject(i).optInt("num") == curLekt) { L = ls.optJSONObject(i); break; }
        if (L == null) return;
        ((TextView) root.findViewById(R.id.lhead_num)).setText(fa(curLekt));
        ((TextView) root.findViewById(R.id.lhead_title)).setText("لکسیون " + fa(curLekt));
        ((TextView) root.findViewById(R.id.lhead_desc)).setText(L.optInt("items") + " مورد");

        // list
        list.removeAllViews();
        JSONArray items = L.optJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject it = items.optJSONObject(i);
            String de = it.optString("de");
            String fa = it.optString("fa");

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 12, 0, 12);

            TextView fa_tv = new TextView(getContext());
            fa_tv.setText("فارسی: " + fa); fa_tv.setTextSize(15);
            row.addView(fa_tv);

            TextView de_tv = new TextView(getContext());
            de_tv.setText("آلمانی: " + (hideDe ? "•••••• (مخفی)" : de));
            de_tv.setTextSize(15); de_tv.setTextColor(0xFF1565C0);
            row.addView(de_tv);

            String ex = it.optString("ex");
            if (!ex.isEmpty()) {
                TextView ex_tv = new TextView(getContext());
                ex_tv.setText("مثال: " + (hideDe ? "•••••• (مخفی)" : ex));
                ex_tv.setTextSize(13); ex_tv.setTextColor(Color.GRAY);
                row.addView(ex_tv);
            }

            if (!hideDe) {
                String[] parts = de.split("\\s+");
                if (parts.length > 1) {
                    TextView sp = new TextView(getContext());
                    sp.setText("خرد شده: " + String.join(" | ", parts));
                    sp.setTextSize(13); sp.setTextColor(Color.GRAY);
                    row.addView(sp);
                }
            }

            CheckBox cb = new CheckBox(getContext());
            cb.setText("برام سخته (بره توی تمرین)");
            boolean marked = store.getLines().contains(de);
            cb.setChecked(marked);
            final String fDe = de;
            cb.setOnCheckedChangeListener((v, c) -> {
                if (c) store.addLine(fDe);
                else { List<String> lines = store.getLines(); lines.remove(fDe); store.setLines(lines); }
                render(root);
            });
            row.addView(cb);
            list.addView(row);
        }
    }

    private String fa(int n) {
        return String.valueOf(n).replace('0', '۰').replace('1', '۱')
            .replace('2', '۲').replace('3', '۳').replace('4', '۴')
            .replace('5', '۵').replace('6', '۶').replace('7', '۷')
            .replace('8', '۸').replace('9', '۹');
    }
}
