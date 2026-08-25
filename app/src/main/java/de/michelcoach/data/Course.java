package de.michelcoach.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {

    public static class Sentence {
        public final int lesson;
        public final String id;
        public final String fa;
        public final List<String> chunks;

        Sentence(int lesson, String id, String fa, List<String> chunks) {
            this.lesson = lesson;
            this.id = id;
            this.fa = fa;
            this.chunks = chunks;
        }

        public String full() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chunks.size(); i++) {
                if (i > 0) sb.append(' ');
                sb.append(chunks.get(i));
            }
            return sb.toString();
        }
    }

    public static class Lesson {
        public final int num;
        public final List<Sentence> sentences = new ArrayList<>();

        Lesson(int num) { this.num = num; }
    }

    private final List<Lesson> lessons = new ArrayList<>();
    private final Map<String, Sentence> byId = new HashMap<>();
    private final List<Sentence> all = new ArrayList<>();

    public Course(Context c) {
        try {
            String json = read(c.getAssets().open("course.json"));
            JSONArray ls = new JSONObject(json).getJSONArray("lektions");
            for (int i = 0; i < ls.length(); i++) {
                JSONObject lo = ls.getJSONObject(i);
                Lesson lesson = new Lesson(lo.getInt("num"));
                JSONArray items = lo.getJSONArray("items");
                String curFa = null;
                List<String> chunks = new ArrayList<>();
                int start = 0;
                for (int j = 0; j < items.length(); j++) {
                    JSONObject it = items.getJSONObject(j);
                    String fa = it.optString("fa", "").trim();
                    String de = it.optString("de", "").trim();
                    if (!fa.equals(curFa)) {
                        if (curFa != null && !chunks.isEmpty()) add(lesson, curFa, chunks, start);
                        curFa = fa;
                        chunks = new ArrayList<>();
                        start = j;
                    }
                    if (de.length() > 0) chunks.add(de);
                }
                if (curFa != null && !chunks.isEmpty()) add(lesson, curFa, chunks, start);
                if (!lesson.sentences.isEmpty()) lessons.add(lesson);
            }
        } catch (Exception e) {
            throw new IllegalStateException("course.json broken", e);
        }
    }

    private void add(Lesson lesson, String fa, List<String> chunks, int start) {
        Sentence s = new Sentence(lesson.num, lesson.num + "_" + start, fa, chunks);
        lesson.sentences.add(s);
        all.add(s);
        byId.put(s.id, s);
    }

    public List<Lesson> lessons() { return lessons; }

    public Lesson lesson(int num) {
        for (Lesson l : lessons) if (l.num == num) return l;
        return lessons.get(0);
    }

    public List<Sentence> all() { return all; }

    public Sentence byId(String id) { return byId.get(id); }

    public int lessonCount() { return lessons.size(); }

    public static String read(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line).append('\n');
        in.close();
        return sb.toString();
    }
}
