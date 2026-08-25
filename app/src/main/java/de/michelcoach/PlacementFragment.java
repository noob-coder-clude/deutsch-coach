package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.michelcoach.data.Course;

public class PlacementFragment extends Fragment {

    private static class Q {
        final Course.Sentence sen;
        Q(Course.Sentence s) { sen = s; }
    }

    private List<Q> quiz;
    private int pos;
    private final Set<Integer> knownLessons = new HashSet<>();
    private final Set<String> known = new HashSet<>();

    private TextView counter, faText, resultText;
    private MaterialCardView questionCard, resultCard;
    private Button btnKnow, btnDontKnow, btnFinish, btnApply;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup p, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_placement, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        counter = v.findViewById(R.id.pl_counter);
        faText = v.findViewById(R.id.pl_fa);
        resultText = v.findViewById(R.id.pl_result_text);
        questionCard = v.findViewById(R.id.pl_card);
        resultCard = v.findViewById(R.id.pl_result_card);
        btnKnow = v.findViewById(R.id.btn_know);
        btnDontKnow = v.findViewById(R.id.btn_dont_know);
        btnFinish = v.findViewById(R.id.btn_finish_early);
        btnApply = v.findViewById(R.id.btn_apply);

        btnKnow.setOnClickListener(x -> answer(true));
        btnDontKnow.setOnClickListener(x -> answer(false));
        btnFinish.setOnClickListener(x -> finish());
        btnApply.setOnClickListener(x -> {
            Store.get(requireContext()).setStartLesson(recommended());
            Store.get(requireContext()).setPlacementDone(true);
            ((MainActivity) requireActivity()).show(R.id.nav_coach);
        });

        buildQuiz();
        showQuestion();
    }

    private void buildQuiz() {
        quiz = new ArrayList<>();
        for (Course.Lesson l : App.course().lessons()) {
            List<Course.Sentence> ss = l.sentences;
            int[] picks = {0, ss.size() / 2, ss.size() - 1};
            Set<Integer> used = new HashSet<>();
            for (int p : picks) {
                if (p >= 0 && p < ss.size() && used.add(p)) quiz.add(new Q(ss.get(p)));
            }
        }
        pos = 0;
        known.clear();
        knownLessons.clear();
    }

    private void showQuestion() {
        if (pos >= quiz.size()) { finish(); return; }
        Course.Sentence s = quiz.get(pos).sen;
        counter.setText("سوال " + (pos + 1) + " از " + quiz.size() + " — درس " + s.lesson);
        faText.setText(s.fa);
    }

    private void answer(boolean know) {
        if (pos >= quiz.size()) return;
        Course.Sentence s = quiz.get(pos).sen;
        if (know) {
            known.add(s.id);
            knownLessons.add(s.lesson);
        }
        pos++;
        showQuestion();
    }

    private int recommended() {
        for (Course.Lesson l : App.course().lessons()) {
            int total = 0, ok = 0;
            for (Q q : quiz) {
                if (q.sen.lesson != l.num) continue;
                total++;
                if (known.contains(q.sen.id)) ok++;
            }
            if (total > 0 && (double) ok / total < 0.6) return l.num;
        }
        return App.course().lessons().get(App.course().lessons().size() - 1).num;
    }

    private void finish() {
        questionCard.setVisibility(View.GONE);
        resultCard.setVisibility(View.VISIBLE);
        int rec = recommended();
        String level = rec == 1 ? "از پایه شروع می‌کنیم" : "سطحت بالاست!";
        resultText.setText(level + "\nپیشنهاد ما: شروع از درس " + rec
                + "\n(" + known.size() + " جمله از " + quiz.size() + " رو بلد بودی)");
    }
}
