package de.macht.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private boolean hideGerman = false;
    private boolean hidePersian = false;

    public AssetRowAdapter(List<String> rows, Context ctx) {
        this.rows = rows;
        this.ctx = ctx;
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
                holder.img.setVisibility(View.GONE);
                return;
            }

            Bitmap toShow;
            int w = full.getWidth();
            int h = full.getHeight();

            if (hideGerman && hidePersian) {
                toShow = null;
            } else if (hideGerman) {
                // show only Persian (right half)
                toShow = Bitmap.createBitmap(full, w / 2, 0, w - w / 2, h);
            } else if (hidePersian) {
                // show only German (left half)
                toShow = Bitmap.createBitmap(full, 0, 0, w / 2, h);
            } else {
                // show both
                toShow = full;
            }

            if (toShow != null) {
                holder.img.setImageBitmap(toShow);
                holder.img.setVisibility(View.VISIBLE);
            } else {
                holder.img.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ImageView img;
        RowHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img_row);
        }
    }
}