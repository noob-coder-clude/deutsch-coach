package de.michelcoach;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookFragment extends Fragment {

    private final List<String> rows = new ArrayList<>();
    private BookAdapter adapter;
    private MaterialButton toggleDe, toggleFa;
    private TextView posLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup p, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_book, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        loadRowNames();
        RecyclerView rv = v.findViewById(R.id.book_list);
        LinearLayoutManager lm = new LinearLayoutManager(requireContext());
        rv.setLayoutManager(lm);
        adapter = new BookAdapter();
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView r, int dx, int dy) {
                int p = lm.findFirstVisibleItemPosition();
                if (p >= 0 && p < rows.size()) labelFor(rows.get(p));
            }
        });

        toggleDe = v.findViewById(R.id.toggle_hide_de);
        toggleFa = v.findViewById(R.id.toggle_hide_fa);
        posLabel = v.findViewById(R.id.pos_label);
        MaterialButton prev = v.findViewById(R.id.btn_prev_row);
        MaterialButton next = v.findViewById(R.id.btn_next_row);
        TextView total = v.findViewById(R.id.total_label);
        total.setText(rows.size() + " ردیف");
        if (!rows.isEmpty()) labelFor(rows.get(0));

        Store s = Store.get(requireContext());
        toggleDe.setChecked(s.hideDe());
        toggleFa.setChecked(s.hideFa());
        toggleDe.setOnClickListener(x -> {
            s.setHideDe(toggleDe.isChecked());
            adapter.resetOverrides();
        });
        toggleFa.setOnClickListener(x -> {
            s.setHideFa(toggleFa.isChecked());
            adapter.resetOverrides();
        });
        prev.setOnClickListener(x -> scroll(lm, -1));
        next.setOnClickListener(x -> scroll(lm, 1));
    }

    private void scroll(LinearLayoutManager lm, int delta) {
        int first = lm.findFirstVisibleItemPosition();
        int target = Math.max(0, Math.min(rows.size() - 1, first + delta));
        lm.scrollToPositionWithOffset(target, 0);
    }

    private void labelFor(String n) {
        String page = n.substring(1, n.indexOf('_'));
        String row = n.substring(n.indexOf("_r") + 2, n.length() - 4);
        posLabel.setText("صفحه " + Integer.parseInt(page) + " — ردیف " + Integer.parseInt(row));
    }

    private void loadRowNames() {
        try {
            for (String f : requireContext().getAssets().list("pages/rows")) {
                if (f.endsWith(".png") && f.startsWith("p")) rows.add(f);
            }
            Collections.sort(rows, (a, b2) -> {
                int pa = Integer.parseInt(a.substring(1, a.indexOf('_')));
                int pb = Integer.parseInt(b2.substring(1, b2.indexOf('_')));
                if (pa != pb) return Integer.compare(pa, pb);
                int ra = Integer.parseInt(a.substring(a.indexOf("_r") + 2, a.length() - 4));
                int rb = Integer.parseInt(b2.substring(b2.indexOf("_r") + 2, b2.length() - 4));
                return Integer.compare(ra, rb);
            });
        } catch (Exception e) {
            rows.clear();
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.Holder> {

        private final Map<String, Bitmap> mem = Collections.synchronizedMap(new HashMap<>());
        private final Map<Integer, Boolean> revealDe = new HashMap<>();
        private final Map<Integer, Boolean> revealFa = new HashMap<>();

        void resetOverrides() {
            revealDe.clear();
            revealFa.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_book_row, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int position) {
            h.bind(rows.get(position), position);
        }

        @Override
        public int getItemCount() { return rows.size(); }

        private Bitmap decode(android.content.Context ctx, String name) {
            Bitmap hit = mem.get(name);
            if (hit != null) return hit;
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                InputStream in1 = ctx.getAssets().open("pages/rows/" + name);
                BitmapFactory.decodeStream(in1, null, o);
                in1.close();
                int sample = 1;
                while (o.outWidth / sample > 1600) sample *= 2;
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = sample;
                InputStream in2 = ctx.getAssets().open("pages/rows/" + name);
                Bitmap bmp = BitmapFactory.decodeStream(in2, null, o2);
                in2.close();
                if (bmp != null) mem.put(name, bmp);
                return bmp;
            } catch (Exception e) {
                return null;
            }
        }

        class Holder extends RecyclerView.ViewHolder {
            final ImageView img;
            final View ovDe, ovFa;
            final TextView posTag;

            Holder(@NonNull View v) {
                super(v);
                img = v.findViewById(R.id.row_img);
                ovDe = v.findViewById(R.id.overlay_de);
                ovFa = v.findViewById(R.id.overlay_fa);
                posTag = v.findViewById(R.id.row_pos_tag);
            }

            void bind(String name, int position) {
                img.setTag(name);
                Bitmap hit = mem.get(name);
                if (hit != null) {
                    img.setImageBitmap(hit);
                } else {
                    final android.content.Context ctx = img.getContext();
                    img.setImageDrawable(null);
                    App.io().execute(() -> {
                        Bitmap bmp = decode(ctx, name);
                        if (bmp == null) return;
                        img.post(() -> {
                            if (name.equals(img.getTag())) img.setImageBitmap(bmp);
                        });
                    });
                }

                boolean hideDe = Store.get(requireContext()).hideDe()
                        && !Boolean.TRUE.equals(revealDe.get(position));
                boolean hideFa = Store.get(requireContext()).hideFa()
                        && !Boolean.TRUE.equals(revealFa.get(position));
                ovDe.setVisibility(hideDe ? View.VISIBLE : View.GONE);
                ovFa.setVisibility(hideFa ? View.VISIBLE : View.GONE);

                ovDe.setOnClickListener(x -> {
                    revealDe.put(position, true);
                    ovDe.setVisibility(View.GONE);
                });
                ovFa.setOnClickListener(x -> {
                    revealFa.put(position, true);
                    ovFa.setVisibility(View.GONE);
                });

                posTag.setText(labelText(name));
            }

            private String labelText(String n) {
                String page = n.substring(1, n.indexOf('_'));
                String row = n.substring(n.indexOf("_r") + 2, n.length() - 4);
                return "ص" + Integer.parseInt(page) + " / ر" + Integer.parseInt(row);
            }
        }
    }
}
