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
# Retrofit reads Kotlin suspend fun return types via getGenericParameterTypes() on the
# Continuation<T> parameter. R8 full mode removes the generic Signature attribute from
# interfaces that have no concrete implementation (they use Proxy). These -if rules keep
# the interface and its full generic signatures so parseAnnotations() can cast correctly.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
# R8 full mode strips generic signatures from Call/Response unless kept explicitly.
-keep,allowobfuscation,allowshrinking class retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# Method-level annotations needed so Retrofit finds @GET, @POST, etc.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-dontwarn kotlin.Unit
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
