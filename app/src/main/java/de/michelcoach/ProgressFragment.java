package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.Set;
import de.michelcoach.data.CardDao;

public class ProgressFragment extends Fragment {

    private TextView statSeen, statMastered, statReviews, statStreak;
    private LinearLayout lessonBars;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup p, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_progress, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        statSeen = v.findViewById(R.id.stat_seen);
        statMastered = v.findViewById(R.id.stat_mastered);
        statReviews = v.findViewById(R.id.stat_reviews);
        statStreak = v.findViewById(R.id.stat_streak);
        lessonBars = v.findViewById(R.id.lesson_bars);
        onShown();
    }

    public void onShown() {
        if (!isAdded()) return;
        Set<String> days = Store.get(requireContext()).doneDays();
        statStreak.setText(String.valueOf(Dates.streak(days)) + " روز");

        App.io().execute(() -> {
            final int seen = App.db().cards().seenCount();
            final int mastered = App.db().cards().masteredCount();
            final int reviews = App.db().cards().totalReviews();

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                statSeen.setText(String.valueOf(seen));
                statMastered.setText(String.valueOf(mastered));
                statReviews.setText(String.valueOf(reviews));

                lessonBars.removeAllViews();
                for (de.michelcoach.data.Course.Lesson l : App.course().lessons()) {
                    addLessonBar(l.num, l.sentences.size());
                }
            });
        });
    }

    private void addLessonBar(int num, int sentenceCount) {
        App.io().execute(() -> {
            CardDao dao = App.db().cards();
            final int started = dao.startedCount(String.valueOf(num) + "_");
            final int masteredInL = dao.masteredInLesson(String.valueOf(num) + "_");
            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                View row = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_lesson_bar, lessonBars, false);
                TextView title = row.findViewById(R.id.lb_title);
                LinearProgressIndicator bar = row.findViewById(R.id.lb_progress);
                TextView pct = row.findViewById(R.id.lb_pct);
                title.setText("درس " + num + " (" + sentenceCount + " جمله)");
                int percent = sentenceCount == 0 ? 0 : (started * 100) / sentenceCount;
                bar.setMax(100);
                bar.setProgress(percent);
                pct.setText(masteredInL > 0 ? percent + "% • " + masteredInL + " مسلط" : percent + "%");
                lessonBars.addView(row);
            });
        });
    }
}
