package de.michelcoach;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            show(item.getItemId());
            return true;
        });
        if (savedInstanceState == null) nav.setSelectedItemId(R.id.nav_coach);
    }

    public void show(int id) {
        Fragment f;
        if (id == R.id.nav_placement) f = new PlacementFragment();
        else if (id == R.id.nav_book) f = new BookFragment();
        else if (id == R.id.nav_practice) f = new PracticeFragment();
        else if (id == R.id.nav_progress) f = new ProgressFragment();
        else f = new CoachFragment();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.container, f);
        tx.commit();
    }
}
