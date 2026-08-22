package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    private boolean hideDe = true; // no-cheat: hide German column by default

    private LinearLayout list;
    private TextView status;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_book, vg, false);
        store = new Store(getContext());
        loadCourse();
        hideDe = store.getBool("no_cheat", true);

        Spinner sp = root.findViewById(R.id.book_spin);
        List<String> names = new ArrayList<>();
        JSONArray ls = course.optJSONArray("lektions");
        for (int i = 0; i < ls.length(); i++)
            names.add("لکسیون " + ls.optJSONObject(i).optInt("num"));
        ArrayAdapter<String> ad = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_item, names);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ad);
        sp.setSelection(Math.min(store.getPhase() - 1, names.size() - 1));
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> a, View v, int i, long id) {
                curLekt = i + 1; store.setPhase(curLekt); render();
            }
            public void onNothingSelected(android.widget.AdapterView<?> a) {}
        });

        Button toggle = root.findViewById(R.id.book_toggle);
        toggle.setText(hideDe ? "نشون دادن آلمانی" : "مخفی کردن آلمانی (جلوگیری تقلب)");
        toggle.setOnClickListener(v -> {
            hideDe = !hideDe;
            store.putBool("no_cheat", hideDe);
            toggle.setText(hideDe ? "نشون دادن آلمانی" : "مخفی کردن آلمانی (جلوگیری تقلب)");
            render();
        });

        list = root.findViewById(R.id.book_list);
        status = root.findViewById(R.id.book_status);
        render();
        return root;
    }

    private void loadCourse() {
        try {
            InputStream is = getContext().getAssets().open("course.json");
            int size = is.available();
            byte[] buf = new byte[size];
            is.read(buf); is.close();
            course = new JSONObject(new String(buf, StandardCharsets.UTF_8));
        } catch (Exception e) {
            course = new JSONObject();
        }
    }

    private void render() {
        list.removeAllViews();
        JSONArray ls = course.optJSONArray("lektions");
        if (ls == null) return;
        JSONObject L = null;
        for (int i = 0; i < ls.length(); i++)
            if (ls.optJSONObject(i).optInt("num") == curLekt) { L = ls.optJSONObject(i); break; }
        if (L == null) return;
        JSONArray items = L.optJSONArray("items");
        int hard = 0;
        for (int i = 0; i < items.length(); i++) {
            JSONObject it = items.optJSONObject(i);
            String de = it.optString("de");
            String fa = it.optString("fa");
            String key = Store.key(de);

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 10, 0, 10);

            TextView faTv = new TextView(getContext());
            faTv.setText("فارسی: " + fa);
            faTv.setTextSize(15);
            row.addView(faTv);

            TextView deTv = new TextView(getContext());
            deTv.setText("آلمانی: " + (hideDe ? "•••••• (مخفی)" : de));
            deTv.setTextSize(15);
            deTv.setTextColor(0xFF1565C0);
            row.addView(deTv);

            // split tool
            if (!hideDe) {
                String[] parts = de.split("\\s+");
                if (parts.length > 1) {
                    TextView sp = new TextView(getContext());
                    sp.setText("خرد شده: " + String.join(" | ", parts));
                    sp.setTextSize(13);
                    row.addView(sp);
                }
            }

            CheckBox cb = new CheckBox(getContext());
            cb.setText("برام سخته (بره توی تمرین)");
            boolean marked = store.getLines().contains(de);
            cb.setChecked(marked);
            if (marked) hard++;
            final String fDe = de;
            cb.setOnCheckedChangeListener((v, c) -> {
                if (c) { store.addLine(fDe); }
                else {
                    List<String> lines = store.getLines();
                    lines.remove(fDe);
                    store.setLines(lines);
                }
                render();
            });
            row.addView(cb);
            list.addView(row);
        }
        status.setText("لکسیون " + curLekt + " — " + items.length() + " مورد | سخت‌های انتخابی: " + hard);
    }
}
