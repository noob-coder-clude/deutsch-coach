package de.macht.app.ui;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.materialswitch.MaterialSwitch;
import de.macht.app.R;
import de.macht.app.AssetRowAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {

    private RecyclerView recycler;
    private MaterialSwitch swHideGerman;
    private MaterialSwitch swHidePersian;
    private AssetRowAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container, false);

        recycler = v.findViewById(R.id.recycler_rows);
        swHideGerman = v.findViewById(R.id.sw_hide_german);
        swHidePersian = v.findViewById(R.id.sw_hide_persian);

        List<String> rows = new ArrayList<>();
        AssetManager am = requireContext().getAssets();
        try {
            String[] files = am.list("pages/rows");
            if (files != null) {
                for (String f : files) {
                    if (f.endsWith(".png")) rows.add("pages/rows/" + f);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // sort so p01..p67, r01..rNN
        rows.sort((a, b) -> a.compareTo(b));

        adapter = new AssetRowAdapter(rows, requireContext());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        swHideGerman.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setHideGerman(isChecked));
        swHidePersian.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setHidePersian(isChecked));

        return v;
    }
}
