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
# This should point to the directory where AlexaClientLib.aar is stored.
-libraryjars ../AlexaClientLib
-libraryjars libs

# Keep the LWA and Kenobi Client Library classes
-dontwarn com.amazon.identity.auth.device.**
-dontwarn com.amazon.alexa.vsk.clientlib.**
-dontwarn com.amazon.device.messaging.**

-keep class com.amazon.identity.auth.device.** { *; }
-keep class com.amazon.alexa.vsk.clientlib.** { *; }
-keep class com.amazon.device.messaging.** {*;}
-keep public class * extends com.amazon.device.messaging.ADMMessageReceiver
-keep public class * extends com.amazon.device.messaging.ADMMessageHandlerBase
-keep public class * extends com.amazon.device.messaging.ADMMessageHandlerJobBase
