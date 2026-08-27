package de.macht.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AssetRowAdapter extends RecyclerView.Adapter<AssetRowAdapter.RowHolder> {

    private final List<String> rows;
    private final Context ctx;
    private final int maxHpx;
    private boolean hideGerman = false;
    private boolean hidePersian = false;

    public AssetRowAdapter(List<String> rows, Context ctx) {
        this.rows = rows;
        this.ctx = ctx;
        this.maxHpx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 36, ctx.getResources().getDisplayMetrics());
    }

    public void setHideGerman(boolean v) {
        hideGerman = v;
        notifyDataSetChanged();
    }

    public void setHidePersian(boolean v) {
        hidePersian = v;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        try {
            InputStream is = ctx.getAssets().open(rows.get(position));
            Bitmap full = BitmapFactory.decodeStream(is);
            is.close();
            if (full == null) {
                holder.itemView.setVisibility(View.GONE);
                return;
            }
            holder.itemView.setVisibility(View.VISIBLE);

            int split = findSplitX(full);
            Bitmap de = Bitmap.createBitmap(full, 0, 0, Math.max(1, split), full.getHeight());
            int faW = full.getWidth() - split;
            Bitmap fa = faW > 8
                    ? Bitmap.createBitmap(full, split, 0, faW, full.getHeight())
                    : null;

            apply(holder.imgDe, de);
            if (fa != null) {
                holder.imgFa.setVisibility(View.VISIBLE);
                apply(holder.imgFa, fa);
            } else {
                holder.imgFa.setVisibility(View.GONE);
            }

            holder.maskDe.setVisibility(hideGerman ? View.VISIBLE : View.GONE);
            holder.maskFa.setVisibility(hidePersian ? View.VISIBLE : View.GONE);
        } catch (IOException e) {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    private void apply(ImageView img, Bitmap bmp) {
        float scale = Math.min(1f, maxHpx / (float) Math.max(1, bmp.getHeight()));
        int w = Math.max(1, Math.round(bmp.getWidth() * scale));
        int h = Math.max(1, Math.round(bmp.getHeight() * scale));
        ViewGroup.LayoutParams lp = img.getLayoutParams();
        lp.width = w;
        lp.height = h;
        img.setLayoutParams(lp);
        img.setImageBitmap(bmp);
        img.setVisibility(View.VISIBLE);
    }

    /** Lowest-ink column in the middle = gap between German (left) and Persian (right). */
    private static int findSplitX(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w < 80) return w;
        int start = w / 5;
        int end = (w * 4) / 5;
        int win = Math.max(6, w / 60);
        long[] col = new long[w];
        for (int x = start; x < end; x++) {
            long s = 0;
            for (int y = 0; y < h; y += 2) {
                int c = bmp.getPixel(x, y);
                s += 765 - (((c >> 16) & 0xff) + ((c >> 8) & 0xff) + (c & 0xff));
            }
            col[x] = s;
        }
        long best = Long.MAX_VALUE;
        int bestX = w / 2;
        for (int x = start; x < end - win; x++) {
            long s = 0;
            for (int i = 0; i < win; i++) s += col[x + i];
            if (s < best) {
                best = s;
                bestX = x + win / 2;
            }
        }
        return Math.max(8, Math.min(w - 8, bestX));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ImageView imgDe;
        ImageView imgFa;
        View maskDe;
        View maskFa;

        RowHolder(@NonNull View v) {
            super(v);
            imgDe = v.findViewById(R.id.img_de);
            imgFa = v.findViewById(R.id.img_fa);
            maskDe = v.findViewById(R.id.mask_de);
            maskFa = v.findViewById(R.id.mask_fa);
        }
    }
}
