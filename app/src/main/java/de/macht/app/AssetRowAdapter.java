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
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp == null) {
                holder.img.setVisibility(View.GONE);
                return;
            }
            holder.img.setImageBitmap(bmp);
            holder.img.setVisibility(View.VISIBLE);

            // masks (not crop): keep full row visible, hide only one side
            holder.maskLeft.setVisibility(hideGerman ? View.VISIBLE : View.GONE);
            holder.maskRight.setVisibility(hidePersian ? View.VISIBLE : View.GONE);

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
        View maskLeft;
        View maskRight;
        RowHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img_row);
            maskLeft = v.findViewById(R.id.mask_left);
            maskRight = v.findViewById(R.id.mask_right);
        }
    }
}
