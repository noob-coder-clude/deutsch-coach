package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class CoachFragment extends Fragment {
    // Method only — no course content copied. (Play-safe)
    private static final String[] STEPS = {
        "۱. مواجه اولیه با درس (گوش دادن + رقابت با زبان‌آموزهای فایل)",
        "۲. تقلب ممنوع: ستون آلمانی رو پیش از ساختن نگاه نکن",
        "۳. مطلقاً کلمه‌به‌کلمه معنی نکن — کلیت جمله رو بگیر",
        "۴. مرور الف: ستون آلمانی رو بپوشون، فارسی رو ببین، کل جمله رو بساز",
        "۵. مرور ب: جمله‌های سخت رو علامت بزن، خرد کن، تکرار تا روونی، بعد معنی",
        "۶. استمرار + برنامه‌ریزی مو‌به‌مو؛ «چه کنم چه کنم» رو بذار کنار",
    };

    private static final String[] LEKTION = {
        "لکسیون ۱ — مقدمات / خواستن / آمدن",
        "لکسیون ۲ — فعل‌های بیشتر / نفی",
        "لکسیون ۳ — ضمایر / صفت‌ها",
        "لکسیون ۴ — گذشته (Perfekt)",
        "لکسیون ۵ — ترتیب کلمه / اتصالات",
        "لکسیون ۶ — جمع‌بندی / جمله‌های ترکیبی",
    };

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_coach, vg, false);
        Store s = new Store(getContext());

        LinearLayout list = root.findViewById(R.id.steps);
        for (String t : STEPS) {
            TextView tv = new TextView(getContext());
            tv.setText(t);
            tv.setPadding(0, 12, 0, 12);
            tv.setTextSize(15);
            list.addView(tv);
        }

        // No-cheat rule as a hard zero/one toggle
        CheckBox noCheat = root.findViewById(R.id.cb_nocheat);
        noCheat.setChecked(s.getBool("no_cheat", false));
        noCheat.setOnCheckedChangeListener((v, c) -> s.putBool("no_cheat", c));

        // Phase / Lektion selector (smart reminder basis)
        Spinner sp = root.findViewById(R.id.spin_phase);
        ArrayAdapter<String> ad = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_item, LEKTION);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ad);
        sp.setSelection(Math.min(s.getPhase() - 1, LEKTION.length - 1));
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> a, View v, int i, long id) {
                s.setPhase(i + 1);
            }
            public void onNothingSelected(android.widget.AdapterView<?> a) {}
        });

        // Daily done
        CheckBox done = root.findViewById(R.id.cb_done);
        String today = java.time.LocalDate.now().toString();
        done.setChecked(s.getDoneDays().contains(today));
        done.setOnCheckedChangeListener((v, c) -> {
            if (c) s.markDay(today);
            s.putBool("phase1_day", c);
        });

        TextView tip = root.findViewById(R.id.coach_tip);
        tip.setText("یادآوری هوشمند: روی «" + LEKTION[s.getPhase() - 1] +
            "» هستی. جمله‌های سختت رو توی تب تمرین وارد کن و با میکروفون تمرین کن.");
        return root;
    }
}
