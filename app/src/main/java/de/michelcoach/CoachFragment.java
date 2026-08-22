package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CoachFragment extends Fragment {
    // Method only — no course content copied. (Play-safe / private build)
    private static final String[] DEFAULTS = {
        "۱۰ دقیقه به جلسهٔ صوتی لکسیون گوش دادم",
        "۵ جملهٔ آلمانی رو بلند گفتم",
        "یک راند خرد کردن جمله بازی کردم",
        "ستون آلمانی جزوه رو پنهان کردم و خودآزمایی دادم",
        "بدون مترجم و دیکشنری جلو رفتم (تقلبسی ممنوع!)"
    };
    private static final String[] LEKTION = {
        "کلمات آشنا", "افعال طلایی", "ضمیرها", "می‌خواهم/می‌توانم",
        "آرتیکل‌ها", "ترتیب طلایی", "پرسیدن", "گذشته", "آینده", "نفی و قیدها"
    };

    private Store store;
    private final List<Boolean> done = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_coach, vg, false);
        store = new Store(getContext());

        TextView hi = root.findViewById(R.id.hero_hi);
        TextView date = root.findViewById(R.id.hero_date);
        int h = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        hi.setText(h < 12 ? "صبحت بخیر رفیق!" : h < 18 ? "آماده‌ای رفیق؟" : "شبِ آرامی برای آلمانی");
        date.setText(new java.text.SimpleDateFormat("EEEE d MMMM", new java.util.Locale("fa"))
            .format(new java.util.Date()));

        setupChecklist(root);
        setupPledge(root);
        setupLessons(root);
        return root;
    }

    private void setupChecklist(View root) {
        for (int i = 0; i < DEFAULTS.length; i++) done.add(false);
        LinearLayout list = root.findViewById(R.id.ck_list);
        ProgressBar bar = root.findViewById(R.id.ck_bar);
        TextView pct = root.findViewById(R.id.ring_pct);

        for (int i = 0; i < DEFAULTS.length; i++) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(DEFAULTS[i]);
            cb.setTextSize(13);
            final int idx = i;
            cb.setOnCheckedChangeListener((v, c) -> {
                done.set(idx, c);
                int d = 0; for (boolean x : done) if (x) d++;
                bar.setProgress(d * 100 / done.size());
                pct.setText(fa(d) + "/" + fa(done.size()));
            });
            list.addView(cb);
        }

        EditText input = root.findViewById(R.id.ck_input);
        Button add = root.findViewById(R.id.ck_add);
        add.setOnClickListener(v -> {
            String t = input.getText().toString().trim();
            if (t.isEmpty()) return;
            CheckBox cb = new CheckBox(getContext());
            cb.setText(t); cb.setTextSize(13);
            final int idx = done.size();
            done.add(false);
            cb.setOnCheckedChangeListener((vv, c) -> {
                done.set(idx, c);
                int d = 0; for (boolean x : done) if (x) d++;
                bar.setProgress(d * 100 / done.size());
                pct.setText(fa(d) + "/" + fa(done.size()));
            });
            list.addView(cb);
            input.setText("");
        });
    }

    private void setupPledge(View root) {
        Button btn = root.findViewById(R.id.pledge_btn);
        TextView stamp = root.findViewById(R.id.stamp);
        boolean pledged = store.getBool("pledge_today", false);
        if (pledged) {
            btn.setVisibility(View.GONE);
            stamp.setVisibility(View.VISIBLE);
        } else {
            btn.setOnClickListener(v -> {
                store.putBool("pledge_today", true);
                btn.setVisibility(View.GONE);
                stamp.setVisibility(View.VISIBLE);
            });
        }
    }

    private void setupLessons(View root) {
        LinearLayout lp = root.findViewById(R.id.lesson_picker);
        int cur = store.getPhase();
        for (int i = 0; i < LEKTION.length; i++) {
            Button b = new Button(getContext());
            b.setText(fa(i + 1) + "\n" + LEKTION[i]);
            b.setTextSize(11);
            b.setPadding(18, 12, 18, 12);
            if (i + 1 == cur) b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF1565C0));
            final int n = i + 1;
            b.setOnClickListener(v -> {
                store.setPhase(n);
                Toast("لکسیون " + fa(n) + " — برو تب جزوه");
            });
            lp.addView(b);
        }
    }

    private void Toast(String m) {
        android.widget.Toast.makeText(getContext(), m, android.widget.Toast.LENGTH_SHORT).show();
    }

    private String fa(int n) {
        return String.valueOf(n).replace('0', '۰').replace('1', '۱')
            .replace('2', '۲').replace('3', '۳').replace('4', '۴')
            .replace('5', '۵').replace('6', '۶').replace('7', '۷')
            .replace('8', '۸').replace('9', '۹');
    }
}
