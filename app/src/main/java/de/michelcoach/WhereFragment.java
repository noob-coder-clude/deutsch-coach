package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class WhereFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_where, vg, false);
        TextView tv = root.findViewById(R.id.where_text);
        tv.setText(
            "کجای مسیری؟\n\n" +
            "فاز ۱ — آشنایی و پایه (رویکرد به ساختار کلی تا ~B2)\n" +
            "  • قدم ۱: مواجه اولیه با درس\n" +
            "  • قدم ۲: مرور الف (ستون‌پوش + برگردوندن فارسی→آلمانی کلّی)\n" +
            "  • قدم ۳: مرور ب (۱۰ جمله سخت ← تکرار در ده‌دقیقه‌های پراکنده)\n\n" +
            "فاز ۲ — A1 تا اوایل B1\n" +
            "فاز ۳ — B1 تا B2\n" +
            "فاز ۴ — پیشرفته / آمادگی آزمون\n\n" +
            "گیج شدی؟ برگرد به قدمی که روی اونی. تردید یعنی بازگشت به تمرین، نه تغییر مسیر.\n" +
            "تعهد + استمرار. «چه کنم چه کنم» رو بذار کنار — قاطعانه ادامه بده."
        );
        return root;
    }
}
