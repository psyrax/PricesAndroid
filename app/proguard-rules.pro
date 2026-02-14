# Add project specific ProGuard rules here.

# ── General ──
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# ── Data models (Room entities, DTOs) ──
-keep class com.psyrax.pokeprices.data.** { *; }

# ── Retrofit ──
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ── OkHttp ──
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── Gson ──
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ── Coroutines ──
-dontwarn kotlinx.coroutines.**

# ── Room ──
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
