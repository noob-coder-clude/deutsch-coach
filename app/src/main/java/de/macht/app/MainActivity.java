package de.macht.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.macht.app.ui.CoachFragment;
import de.macht.app.ui.PlacementFragment;
import de.macht.app.ui.BookFragment;
import de.macht.app.ui.PracticeFragment;
import de.macht.app.ui.ProgressFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private final String[] TAB_TITLES = {"مربی", "تشخیص", "جزوه", "تمرین", "پیشرفت"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 5;
            }

            @Override
            public androidx.fragment.app.Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new CoachFragment();
                    case 1: return new PlacementFragment();
                    case 2: return new BookFragment();
                    case 3: return new PracticeFragment();
                    default: return new ProgressFragment();
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(TAB_TITLES[position])).attach();
    }
}
