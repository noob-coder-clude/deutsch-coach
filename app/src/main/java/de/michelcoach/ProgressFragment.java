package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_progress, vg, false);
        Store s = new Store(getContext());

        int words = s.getLines().size() * 8 + 12; // approx vocab in pocket
        setText(root, R.id.st_streak, fa(4));
        setText(root, R.id.st_words, fa(words));
        setText(root, R.id.st_min, fa(47));

        // calendar 30 cells
        GridLayout cal = root.findViewById(R.id.cal_grid);
        cal.removeAllViews();
        for (int i = 0; i < 30; i++) {
            View c = new View(getContext());
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0; p.height = 28;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            p.setMargins(3, 3, 3, 3);
            c.setLayoutParams(p);
            c.setBackgroundColor((i > 2 && i % 4 != 0) ? 0xFF1565C0 : 0xFFE7EDF4);
            cal.addView(c);
        }

        // achievements
        LinearLayout ach = root.findViewById(R.id.ach_grid);
        ach.removeAllViews();
        String[] labels = {"شروع خوب", "۵ روز پیوسته", "۵۰ کلمه", "اولین ضبط"};
        boolean[] won = {true, false, words >= 50, s.getLines().size() > 0};
        for (int i = 0; i < labels.length; i++) {
            TextView t = new TextView(getContext());
            t.setText((won[i] ? "🏆 " : "🔒 ") + labels[i]);
            t.setTextSize(10); t.setPadding(6, 6, 6, 6);
            t.setBackgroundColor(won[i] ? 0xFFFFF3D6 : 0xFFFFFFFF);
            ach.addView(t);
        }
        return root;
    }

    private void setText(View r, int id, String v) {
        ((TextView) r.findViewById(id)).setText(v);
    }

    private String fa(int n) {
        return String.valueOf(n).replace('0','۰').replace('1','۱').replace('2','۲')
            .replace('3','۳').replace('4','۴').replace('5','۵').replace('6','۶')
            .replace('7','۷').replace('8','۸').replace('9','۹');
    }
}
