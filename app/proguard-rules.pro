# Add project specific ProGuard rules here.

# ── General ──
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
# Required for Gson TypeToken and Retrofit coroutines to resolve generic types at runtime
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ── Data models (Room entities, DTOs) ──
-keep class com.psyrax.pokeprices.data.** { *; }

# ── Retrofit ──
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Retrofit Kotlin coroutines adapter uses anonymous TypeToken to capture suspend return types
-keep class retrofit2.KotlinExtensions { *; }
-keep class retrofit2.KotlinExtensions$* { *; }

# ── OkHttp ──
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── Gson ──
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# TypeToken anonymous subclasses capture generic type info; must survive R8 shrinking
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# ── Coroutines ──
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# ── Room ──
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
