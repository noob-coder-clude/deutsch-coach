package de.macht.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.macht.app.R;

public class PlacementFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_placement, c, false);
        TextView tv = v.findViewById(R.id.tv_placement);
        if (tv != null) tv.setText("آزمون تشخیص برای پیدا کردن نقطهٔ شروع شما در متد میشل توماس.");
        return v;
    }
}
