package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class CoachFragment extends Fragment {
    private static final String[] STEPS = {
        "۱. مواجه اولیه با درس (گوش دادن + رقابت با زبان‌آموزهای فایل)",
        "۲. مرور الف: ستون آلمانی رو بپوشون، فارسی رو ببین، کل جمله رو به آلمانی برگردون",
        "۳. مطلقاً کلمه‌به‌کلمه معنی نکن — کلیت جمله رو بگیر",
        "۴. هدف: از محاسبه ذهنی برس به «جمله از دل» (طبیعی)",
        "۵. مرور ب: ۱۰ جمله سخت رو انتخاب کن، توی ده‌دقیقه‌های مختلف پشت‌سرهم تکرار کن",
        "۶. وقتی روون شد معنی رو اضافه کن و با لحنی بگو که معنی بده",
    };

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_coach, vg, false);
        LinearLayout list = root.findViewById(R.id.steps);
        for (String s : STEPS) {
            TextView t = new TextView(getContext());
            t.setText(s);
            t.setPadding(0, 12, 0, 12);
            t.setTextSize(15);
            list.addView(t);
        }
        CheckBox done = root.findViewById(R.id.cb_done);
        done.setOnCheckedChangeListener((v, c) ->
            getActivity().getSharedPreferences("coach", 0)
                .edit().putBoolean("phase1_day", c).apply());
        done.setChecked(getActivity().getSharedPreferences("coach", 0)
            .getBoolean("phase1_day", false));
        return root;
    }
}
