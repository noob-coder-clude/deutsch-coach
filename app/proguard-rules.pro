# Keep TTS and Room working
-keep class androidx.room.** { *; }
-keepclassmembers class * implements androidx.room.Entity { *; }
