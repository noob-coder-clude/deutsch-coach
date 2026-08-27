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

public class ProgressFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_progress, c, false);
        TextView tv = v.findViewById(R.id.tv_progress);
        if (tv != null) tv.setText("آمار پیشرفت، تقویم ۳۰ روزه و نشان‌ها اینجا نمایش داده می‌شوند.");
        return v;
    }
}
