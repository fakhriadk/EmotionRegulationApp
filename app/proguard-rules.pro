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

# Keep CredentialManager to avoid obfuscation issues
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
    *;
}


# Keep WebView and its necessary components from being stripped by R8/ProGuard
-keepclassmembers class android.webkit.WebView {
   public *;
}

-keep public class android.webkit.WebView
-keep public class android.webkit.WebSettings
-keep public class android.webkit.WebChromeClient
-keep public class android.webkit.WebViewClient