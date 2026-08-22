package de.michelcoach;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 vp = findViewById(R.id.viewpager);
        TabLayout tabs = findViewById(R.id.tabs);
        vp.setAdapter(new SectionsPagerAdapter(this));

        String[] titles = {"مربی", "کجای مسیر", "جزوه", "تمرین", "پیشرفت"};
        new TabLayoutMediator(tabs, vp, (tab, pos) -> tab.setText(titles[pos])).attach();
    }
}
