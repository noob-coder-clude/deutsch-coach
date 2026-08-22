package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class PracticeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_practice, vg, false);
        EditText input = root.findViewById(R.id.edit_practice);
        Button add = root.findViewById(R.id.btn_add);
        TextView list = root.findViewById(R.id.practice_list);

        add.setOnClickListener(v -> {
            String line = input.getText().toString().trim();
            if (line.isEmpty()) return;
            String cur = list.getText().toString();
            list.setText(cur.isEmpty() ? line : cur + "\n• " + line);
            input.setText("");
        });
        return root;
    }
}
