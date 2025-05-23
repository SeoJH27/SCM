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

# Hidden API 관련 경고 무시
-dontwarn java.lang.invoke.MethodHandles$Lookup

# Reflection을 사용하는 라이브러리 보호
-keep class java.lang.invoke.** { *; }
-keep class sun.misc.** { *; }

# Moshi 관련 클래스 최적화 방지
-keepclassmembers class * {
    @com.squareup.moshi.JsonClass *;
}

-keep class com.scm.sch_cafeteria_manager.data.weekAdmin { *; }
