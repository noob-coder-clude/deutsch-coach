package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class WhereFragment extends Fragment {
    // Diagnostic: which axis are you stuck on? -> redirect to the right step/lektion.
    private static final String[][] QUIZ = {
        {"وقتی می‌خواهی جمله بسازی، بیشتر کدام حالتت می‌شود؟",
            "کلمه‌ها را بلدم ولی نمی‌دانم کجا بگذارمشان|order",
            "روی der/die/das و صرف فعل گیر می‌کنم|grammar",
            "کلمهٔ کافی بلد نیستم|vocab"},
        {"موقع حرف زدن آلمانی…",
            "از اشتباه می‌ترسم و ساکت می‌شوم|speak",
            "فعل‌ها را درست صرف نمی‌کنم|grammar",
            "جمله‌ام وسط راه می‌ماند چون کلمه یادم نمی‌آید|vocab"},
        {"وقتی آلمانی گوش می‌دهی…",
            "کلمه‌ها را می‌شنوم ولی ساختار جمله را نمی‌گیرم|order",
            "ü و ö و ch قاطی می‌شوند|pron",
            "لهجه و سرعت، حرفم را می‌زند|speak"},
        {"موقع مرور جزوه…",
            "کلمه را دیده‌ام ولی یادم نمی‌آید|vocab",
            "مثال‌ها را می‌فهمم ولی خودم نمی‌توانم بسازم|speak",
            "زمان و شکل فعل‌ها فراموش می‌شود|grammar"}
    };
    private static final String[] AXES = {"vocab", "grammar", "order", "speak", "pron"};
    private static final String[] AX_LABEL = {"واژگان", "آرتیکل و فعل", "ترتیب کلمات", "جسارتِ گفتن", "تلفظ"};
    private static final int[] AX_GO = {1, 5, 6, -1, -1}; // lektion or -1=go practice

    private int qIdx = 0;
    private final List<String> ans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_where, vg, false);
        buildAccordion(root);
        renderQuiz(root);
        return root;
    }

    private void renderQuiz(View root) {
        LinearLayout box = root.findViewById(R.id.quiz_box);
        box.removeAllViews();
        if (qIdx < QUIZ.length) {
            TextView q = new TextView(getContext());
            q.setText(fa(qIdx + 1) + " از " + fa(QUIZ.length) + " — " + QUIZ[qIdx][0]);
            q.setTextSize(15.5f); q.setTypeface(null, android.graphics.Typeface.BOLD);
            q.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            box.addView(q);
            for (int i = 1; i < 4; i++) {
                String[] parts = QUIZ[qIdx][i].split("\\|");
                Button o = new Button(getContext());
                o.setText(parts[0]);
                o.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF6F9FC));
                o.setTextColor(0xFF17222E);
                o.setGravity(android.view.Gravity.RIGHT);
                final String axis = parts[1];
                o.setOnClickListener(v -> {
                    ans.add(axis); qIdx++;
                    if (qIdx >= QUIZ.length) showResult(root);
                    else renderQuiz(root);
                });
                box.addView(o);
            }
        }
    }

    private void showResult(View root) {
        int[] sc = new int[AXES.length];
        for (String a : ans) for (int i = 0; i < AXES.length; i++) if (AXES[i].equals(a)) sc[i]++;
        int max = 0; for (int i = 1; i < AXES.length; i++) if (sc[i] > sc[max]) max = i;

        LinearLayout box = root.findViewById(R.id.quiz_box);
        box.removeAllViews();
        TextView r = new TextView(getContext());
        r.setText("نتیجه: گیر اصلی‌ات «" + AX_LABEL[max] + "» است");
        r.setTextSize(15.5f); r.setTypeface(null, android.graphics.Typeface.BOLD);
        box.addView(r);

        TextView d = new TextView(getContext());
        d.setText(advice(max));
        d.setTextSize(12.5f); d.setTextColor(0xFF5A6B7D); d.setLineSpacing(2, 1.9f);
        box.addView(d);

        Button go = new Button(getContext());
        final int goMax = max;
        if (AX_GO[goMax] > 0) {
            go.setText("برو به لکسیون " + fa(AX_GO[goMax]));
            go.setOnClickListener(v -> {
                new Store(getContext()).setPhase(AX_GO[goMax]);
                Toast("لکسیون " + fa(AX_GO[goMax]) + " — تب جزوه");
            });
        } else {
            go.setText("برو به تمرین");
            go.setOnClickListener(v -> Toast("تب تمرین: ضبط و مقایسه کن"));
        }
        go.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
        go.setTextColor(0xFFFFFFFF);
        box.addView(go);
    }

    private String advice(int i) {
        switch (i) {
            case 0: return "مشکلت کمبود آجره، نه دیوار. لکسیون ۱ و ۲ رو برو — کلمات هم‌ریشه سریع می‌چسبن.";
            case 1: return "آرتیکل و فعل رو جدا از اسم یاد نگیر. لکسیون ۲ و ۵ الگو رو اتومات می‌کنن.";
            case 2: return "فقط یک قانون: فعل همیشه نفر دومه. لکسیون ۶ همین رو با تمرین تو دستت می‌ذاره.";
            case 3: return "غلط گفتن بخشی از متده. توی تب تمرین ضبط کن و صدات رو بشنو.";
            default: return "ü و ö و ch با گوش یاد گرفته می‌شن. مسیر: شنیدن ← تکرار ← ضبط ← مقایسه.";
        }
    }

    private void buildAccordion(View root) {
        String[][] items = {
            {"🏷 آرتیکل‌ها (der/die/das)",
                "آرتیکل رو هیچ‌وقت جدا از اسم یاد نگیر — «der Tee» یعنی «چای»."},
            {"🔁 صرف فعل‌ها",
                "فعل‌های -en یک الگو دارن: ich lerne، du lernst. هر فعل جدید رو با سه ضمیر بلند بگو."},
            {"🔢 ترتیب کلمات",
                "فعل همیشه نفر دوم جمله‌ست — حتی وقتی با Heute شروع می‌شه."},
            {"🔊 تلفظ",
                "ü رو با گفتن «ای» و لب‌های گرد «او» بساز. مسیر: شنیدن ← تکرار ← ضبط ← مقایسه."}
        };
        LinearLayout box = root.findViewById(R.id.acc_box);
        for (String[] it : items) {
            LinearLayout card = new LinearLayout(getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(android.R.drawable.btn_default);
            card.setBackgroundColor(0xFFFFFFFF);
            card.setPadding(15, 13, 15, 13);

            TextView h = new TextView(getContext());
            h.setText(it[0]); h.setTextSize(13); h.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(h);

            TextView b = new TextView(getContext());
            b.setText(it[1]); b.setTextSize(12.5f); b.setTextColor(0xFF5A6B7D);
            b.setVisibility(View.GONE);
            b.setPadding(0, 8, 0, 0);
            card.addView(b);

            card.setOnClickListener(v -> b.setVisibility(b.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
            box.addView(card);

            View sep = new View(getContext());
            sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            sep.setBackgroundColor(0xFFE2E9F1);
            box.addView(sep);
        }
    }

    private void Toast(String m) {
        android.widget.Toast.makeText(getContext(), m, android.widget.Toast.LENGTH_SHORT).show();
    }

    private String fa(int n) {
        return String.valueOf(n).replace('0', '۰').replace('1', '۱')
            .replace('2', '۲').replace('3', '۳').replace('4', '۴')
            .replace('5', '۵').replace('6', '۶').replace('7', '۷')
            .replace('8', '۸').replace('9', '۹');
    }
}
