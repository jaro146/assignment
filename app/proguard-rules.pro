# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

############################################################
# 🏠 ROOM (Jetpack Room ORM)
############################################################

# Keep annotated entities, DAOs, and database classes.
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }

# Keep Kotlin data class members used by Room
-keep class * extends androidx.room.RoomDatabase {
    *;
}

# Keep schema information for Room (if you export schema)
-keep class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# Prevent warnings about Room
-dontwarn androidx.room.**

############################################################
# 🌐 RETROFIT (with OkHttp)
############################################################

# Retrofit uses reflection on interfaces to create HTTP calls
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep method parameter names (used for @Query, @Field, etc.)
-keepattributes Signature, Exceptions, *Annotation*

# Keep Retrofit API interfaces (so Retrofit can reflectively call them)
-keep interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp warning suppression
-dontwarn okhttp3.**
-dontwarn okio.**

############################################################
# 🔤 GSON (Google JSON library)
############################################################

# Keep model classes serialized/deserialized by Gson
# (if you use @SerializedName or rely on reflection)
-keep class **.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep type adapters and Gson internal classes
-keep class com.google.gson.stream.** { *; }
-keepattributes Signature
-dontwarn sun.misc.**

############################################################
# ⚙️ GENERAL (optional but recommended)
############################################################

# Keep enums (important if you serialize them)
-keepclassmembers enum * { *; }

# Keep annotations
-keepattributes *Annotation*

# Preserve Kotlin metadata for reflection
-keepclassmembers class kotlin.Metadata { *; }

# Timber (if you use Timber for logging)
-dontwarn timber.log.**

# Optional: if you use kotlinx.coroutines
-dontwarn kotlinx.coroutines.**


############################################################
# 🧹 Remove Logging (Android Log, Timber, println)
############################################################

# Remove all android.util.Log calls (d, v, i, w, e, wtf)
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}

# Remove all System.out and System.err print calls
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# Remove all Timber logs (commonly used logging library)
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
    public static void i(...);
    public static void w(...);
    public static void e(...);
    public static void wtf(...);
    public static void log(...);
    public static void tag(...);
    public static void plant(...);
}

