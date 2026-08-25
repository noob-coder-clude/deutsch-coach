package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;
import de.michelcoach.data.AppDb;
import de.michelcoach.data.Card;
import de.michelcoach.data.CardDao;
import de.michelcoach.data.Course;
import de.michelcoach.data.Leitner;

public class CoachFragment extends Fragment {

    private Course.Lesson lesson;
    private int idx;
    private int revealed;
    private boolean rated;

    private TextView lessonTitle, faText, deText, stepLabel, doneTitle;
    private LinearProgressIndicator progress;
    private MaterialCardView card;
    private Button btnReveal, btnEasy, btnHard, btnNextLesson;
    private ImageButton btnPrevLesson, btnNextLessonBtn;
    private View ratingRow, doneBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup p, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_coach, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        lessonTitle = v.findViewById(R.id.lesson_title);
        faText = v.findViewById(R.id.fa_text);
        deText = v.findViewById(R.id.de_text);
        stepLabel = v.findViewById(R.id.step_label);
        progress = v.findViewById(R.id.lesson_progress);
        card = v.findViewById(R.id.coach_card);
        btnReveal = v.findViewById(R.id.btn_reveal);
        btnEasy = v.findViewById(R.id.btn_easy);
        btnHard = v.findViewById(R.id.btn_hard);
        btnNextLesson = v.findViewById(R.id.btn_next_lesson);
        btnPrevLesson = v.findViewById(R.id.btn_prev_lesson);
        btnNextLessonBtn = v.findViewById(R.id.btn_next_lesson_nav);
        ratingRow = v.findViewById(R.id.rating_row);
        doneBox = v.findViewById(R.id.done_box);

        card.setOnClickListener(x -> revealNext());
        btnReveal.setOnClickListener(x -> revealNext());
        btnEasy.setOnClickListener(x -> rate(true));
        btnHard.setOnClickListener(x -> rate(false));
        btnNextLesson.setOnClickListener(x -> gotoLesson(lesson.num + 1));
        btnPrevLesson.setOnClickListener(x -> gotoLesson(lesson.num - 1));
        btnNextLessonBtn.setOnClickListener(x -> gotoLesson(lesson.num + 1));

        onShown();
    }

    public void onShown() {
        if (lesson == null) loadState();
    }

    private void loadState() {
        Store s = Store.get(requireContext());
        int num = s.coachLesson();
        boolean exists = false;
        for (Course.Lesson l : App.course().lessons()) if (l.num == num) exists = true;
        if (!exists) num = App.course().lessons().get(0).num;
        lesson = App.course().lesson(num);
        idx = Math.min(s.coachIndex(), lesson.sentences.size() - 1);
        renderSentence(true);
    }

    private void gotoLesson(int num) {
        List<Course.Lesson> ls = App.course().lessons();
        Course.Lesson target = null;
        for (Course.Lesson l : ls) if (l.num == num) target = l;
        if (target == null) return;
        lesson = target;
        idx = 0;
        persist();
        renderSentence(true);
    }

    private void renderSentence(boolean reset) {
        Store s = Store.get(requireContext());
        s.setCoachLesson(lesson.num);
        s.setCoachIndex(idx);

        doneBox.setVisibility(View.GONE);
        card.setVisibility(View.VISIBLE);
        btnReveal.setVisibility(View.VISIBLE);
        ratingRow.setVisibility(View.GONE);

        Course.Sentence sen = lesson.sentences.get(idx);
        lessonTitle.setText("درس " + lesson.num + " — جمله " + (idx + 1) + " از " + lesson.sentences.size());
        progress.setMax(lesson.sentences.size());
        progress.setProgress(idx);
        faText.setText(sen.fa);
        revealed = reset ? 0 : revealed;
        rated = false;
        deText.setText("");
        stepLabel.setText("");
        updateRevealUi(sen);
    }

    private void updateRevealUi(Course.Sentence sen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < revealed; i++) {
            if (i > 0) sb.append(' ');
            sb.append(sen.chunks.get(i));
        }
        deText.setText(sb.toString());
        if (revealed == 0) {
            stepLabel.setText("اول فکر کن، بعد بساز");
            btnReveal.setText("نمایش گام بعدی");
        } else if (revealed < sen.chunks.size()) {
            stepLabel.setText("گام " + revealed + " از " + sen.chunks.size());
        } else {
            stepLabel.setText("جمله کامل");
            btnReveal.setVisibility(View.GONE);
            ratingRow.setVisibility(View.VISIBLE);
        }
    }

    private void revealNext() {
        if (ratingRow.getVisibility() == View.VISIBLE) return;
        Course.Sentence sen = lesson.sentences.get(idx);
        if (revealed < sen.chunks.size()) {
            revealed++;
            updateRevealUi(sen);
        }
    }

    private void rate(boolean easy) {
        if (rated) return;
        rated = true;
        Store.get(requireContext()).markToday();
        String id = lesson.sentences.get(idx).id;
        App.io().execute(() -> {
            CardDao dao = App.db().cards();
            Card c = dao.get(id);
            if (c == null) c = new Card(id);
            Leitner.answer(c, easy);
            dao.upsert(c);
        });
        idx++;
        if (idx >= lesson.sentences.size()) showDone();
        else renderSentence(true);
    }

    private void showDone() {
        card.setVisibility(View.GONE);
        btnReveal.setVisibility(View.GONE);
        ratingRow.setVisibility(View.GONE);
        progress.setProgress(progress.getMax());
        doneBox.setVisibility(View.VISIBLE);
        boolean hasNext = false;
        for (Course.Lesson l : App.course().lessons()) if (l.num == lesson.num + 1) hasNext = true;
        btnNextLesson.setVisibility(hasNext ? View.VISIBLE : View.GONE);
        doneTitle.setText("درس " + lesson.num + " تمام شد!");
    }

    private void persist() {
        Store s = Store.get(requireContext());
        s.setCoachLesson(lesson.num);
        s.setCoachIndex(idx);
    }
}
