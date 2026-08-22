package de.michelcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class WhereFragment extends Fragment {
    // Diagnostic: where are you stuck? -> redirect to the right step.
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {
        View root = inf.inflate(R.layout.fragment_where, vg, false);
        TextView tv = root.findViewById(R.id.where_text);
        tv.setText(
            "کجای مسیری؟ (تشخیص گیر)\n\n" +
            "فاز ۱ — آشنایی و پایه (تا ~B2، ۸۰٪ ساختار با متد مادرزبان)\n" +
            "  • قدم ۱: مواجه اولیه با درس\n" +
            "  • قدم ۲: تقلب ممنوع (ستون آلمانی رو نبین)\n" +
            "  • قدم ۳: مرور الف (ستون‌پوش + فارسی→آلمانی کلّی)\n" +
            "  • قدم ۴: مرور ب (خرد کردن + تکرار تا روونی)\n\n" +
            "گیج شدی؟ چک‌لیست:\n" +
            "  □ جمله رو می‌فهمی؟ → نه: برگرد قدم ۱\n" +
            "  □ روون می‌گی؟ → نه: تکنیک خرد کردن (تب تمرین)\n" +
            "  □ معنی می‌دونی؟ → نه: بعد روونی برو سراغ معنی\n" +
            "  □ حوصله داری؟ → نه: ۱۰ دقیقه کوتاه بهتر از هیچی\n\n" +
            "فاز ۲ — A1 تا اوایل B1  |  فاز ۳ — B1 تا B2  |  فاز ۴ — آمادگی آزمون\n\n" +
            "تردید یعنی بازگشت به تمرین، نه تغییر مسیر. تعهد + استمرار."
        );
        return root;
    }
}
