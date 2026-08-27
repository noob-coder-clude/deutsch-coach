package de.macht.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.materialswitch.MaterialSwitch;
import de.macht.app.R;
import de.macht.app.RowAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {

    private RecyclerView recycler;
    private MaterialSwitch swHideGerman;
    private MaterialSwitch swHidePersian;
    private RowAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container, false);

        recycler = v.findViewById(R.id.recycler_rows);
        swHideGerman = v.findViewById(R.id.sw_hide_german);
        swHidePersian = v.findViewById(R.id.sw_hide_persian);

        List<String> rows = new ArrayList<>();
        for (int p = 1; p <= 67; p++) {
            for (int r = 1; ; r++) {
                String name = String.format("p%02d_r%02d", p, r);
                if (getResources().getIdentifier(name, "drawable", requireContext().getPackageName()) == 0) break;
                rows.add(name);
            }
        }

        adapter = new RowAdapter(rows);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        swHideGerman.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setHideGerman(isChecked));
        swHidePersian.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setHidePersian(isChecked));

        return v;
    }
}
