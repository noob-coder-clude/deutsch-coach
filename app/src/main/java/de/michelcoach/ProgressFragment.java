package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_progress, vg, false);
        Store s = new Store(getContext());

        TextView days = root.findViewById(R.id.p_days);
        TextView phase = root.findViewById(R.id.p_phase);
        TextView lines = root.findViewById(R.id.p_lines);

        int done = s.getDoneDays().size();
        days.setText("روزهای انجام‌شده: " + done);
        phase.setText("لکسیون فعلی: " + s.getPhase());
        lines.setText("جمله‌های سخت ذخیره‌شده: " + s.getLines().size());

        TextView bar = root.findViewById(R.id.p_bar);
        int weeks = done / 7;
        StringBuilder sb = new StringBuilder("هفته‌ها: ");
        for (int i = 0; i < Math.max(weeks, 1); i++) sb.append("🟦");
        bar.setText(sb.toString());
        return root;
    }
}
