package de.macht.app;

import android.content.Context;
import android.content.res.AssetManager;
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
import java.util.ArrayList;
import java.util.List;

public class AssetRowAdapter extends RecyclerView.Adapter<AssetRowAdapter.RowHolder> {

    private final List<String> rows;   // asset path under pages/rows
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
        String name = rows.get(position).replace("pages/rows/", "");
        load(holder.imgDe, "pages/de/" + name);
        load(holder.imgFa, "pages/fa/" + name);
        holder.imgDe.setVisibility(hideGerman ? View.GONE : View.VISIBLE);
        holder.imgFa.setVisibility(hidePersian ? View.GONE : View.VISIBLE);
    }

    private void load(ImageView iv, String path) {
        try {
            AssetManager am = ctx.getAssets();
            InputStream is = am.open(path);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                iv.setImageBitmap(bmp);
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ImageView imgDe;
        ImageView imgFa;

        RowHolder(@NonNull View v) {
            super(v);
            imgDe = v.findViewById(R.id.img_de);
            imgFa = v.findViewById(R.id.img_fa);
        }
    }
}
