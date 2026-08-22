package de.michelcoach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private View welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 vp = findViewById(R.id.viewpager);
        TabLayout tabs = findViewById(R.id.tabs);
        vp.setAdapter(new SectionsPagerAdapter(this));
        new TabLayoutMediator(tabs, vp, (tab, pos) -> {
            tab.setText(new String[]{"مربی","کجای مسیر","جزوه","تمرین","پیشرفت"}[pos]);
        }).attach();

        welcome = findViewById(R.id.welcome);
        Button start = findViewById(R.id.start_btn);
        start.setOnClickListener(v -> {
            welcome.setVisibility(View.GONE);
        });
    }
}
