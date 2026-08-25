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
import java.util.List;
import de.michelcoach.data.AppDb;
import de.michelcoach.data.Card;
import de.michelcoach.data.CardDao;
import de.michelcoach.data.Course;
import de.michelcoach.data.Leitner;

public class PracticeFragment extends Fragment {

    private final List<Course.Sentence> queue = new ArrayList<>();
    private int idx;

    private TextView counter, faText, deText, stepLabel, emptyText, doneText;
    private MaterialCardView card, emptyCard, doneCard;
    private Button btnStart, btnReveal, btnEasy, btnHard, btnAgain;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup p, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_practice, p, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        counter = v.findViewById(R.id.pr_counter);
        faText = v.findViewById(R.id.pr_fa);
        deText = v.findViewById(R.id.pr_de);
        stepLabel = v.findViewById(R.id.pr_step);
        emptyText = v.findViewById(R.id.pr_empty_text);
        card = v.findViewById(R.id.pr_card);
        emptyCard = v.findViewById(R.id.pr_empty_card);
        doneCard = v.findViewById(R.id.pr_done_card);
        btnStart = v.findViewById(R.id.btn_start_review);
        btnReveal = v.findViewById(R.id.btn_reveal);
        btnEasy = v.findViewById(R.id.btn_easy);
        btnHard = v.findViewById(R.id.btn_hard);
        btnAgain = v.findViewById(R.id.btn_again);

        btnStart.setOnClickListener(x -> loadQueue());
        btnReveal.setOnClickListener(x -> reveal());
        card.setOnClickListener(x -> reveal());
        btnEasy.setOnClickListener(x -> rate(true));
        btnHard.setOnClickListener(x -> rate(false));
        btnAgain.setOnClickListener(x -> {
            doneCard.setVisibility(View.GONE);
            emptyCard.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        });
        onShown();
    }

    public void onShown() {
        if (queue.isEmpty()) {
            card.setVisibility(View.GONE);
            doneCard.setVisibility(View.GONE);
            btnReveal.setVisibility(View.GONE);
            App.io().execute(() -> {
                List<Card> due = App.db().cards().due(System.currentTimeMillis(), 40);
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    queue.clear();
                    for (Card c : due) {
                        Course.Sentence s = App.course().byId(c.id);
                        if (s != null) queue.add(s);
                    }
                    if (queue.isEmpty()) {
                        emptyCard.setVisibility(View.VISIBLE);
                        emptyText.setText("مروری برای امروز نیست. تو مربی جلو برو تا جمله‌های جدید به مرور اضافه بشن.");
                    } else {
                        emptyCard.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                    }
                });
            });
        }
    }

    private void loadQueue() {
        if (queue.isEmpty()) { onShown(); return; }
        idx = 0;
        emptyCard.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        showSentence();
    }

    private void showSentence() {
        if (idx >= queue.size()) {
            card.setVisibility(View.GONE);
            doneCard.setVisibility(View.VISIBLE);
            doneText.setText("همه‌ی " + queue.size() + " مرور امروز تموم شد!");
            return;
        }
        card.setVisibility(View.VISIBLE);
        btnReveal.setVisibility(View.VISIBLE);
        counter.setText("مرور " + (idx + 1) + " از " + queue.size());
        Course.Sentence s = queue.get(idx);
        faText.setText(s.fa);
        deText.setText("");
        stepLabel.setText("اول فکر کن، بعد جواب رو نشون بده");
    }

    private void reveal() {
        if (card.getVisibility() != View.VISIBLE) return;
        Course.Sentence s = queue.get(idx);
        if (deText.getText().length() == 0) {
            deText.setText(s.full());
            stepLabel.setText("درست ساختی؟ خودت صادقانه امتیاز بده");
            btnReveal.setVisibility(View.GONE);
        }
    }

    private void rate(boolean easy) {
        Store.get(requireContext()).markToday();
        String id = queue.get(idx).id;
        App.io().execute(() -> {
            CardDao dao = App.db().cards();
            Card c = dao.get(id);
            if (c == null) c = new Card(id);
            Leitner.answer(c, easy);
            dao.upsert(c);
        });
        idx++;
        showSentence();
    }
}
