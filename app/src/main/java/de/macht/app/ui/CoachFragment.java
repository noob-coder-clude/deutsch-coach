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

public class CoachFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_coach, c, false);
        TextView pledge = v.findViewById(R.id.tv_pledge);
        if (pledge != null) pledge.setText(R.string.pledge_text);
        return v;
    }
}
