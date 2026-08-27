package de.macht.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.RowHolder> {

    private final List<String> rows;
    private boolean hideGerman = false;
    private boolean hidePersian = false;

    public RowAdapter(List<String> rows) {
        this.rows = rows;
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
        Context ctx = holder.img.getContext();
        int resId = ctx.getResources().getIdentifier(rows.get(position), "drawable", ctx.getPackageName());
        if (resId != 0) {
            holder.img.setImageResource(resId);
            holder.img.setVisibility(View.VISIBLE);
        } else {
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
