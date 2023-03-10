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

-keep class com.google.firebase.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.google.firebase.quickstart.database.java.viewholder.** {
    *;
}

-keepclassmembers class com.google.firebase.quickstart.database.java.models.** {
    *;
}

-keepclassmembers class com.google.firebase.quickstart.database.kotlin.models.** {
    *;
}

-keep class com.droidekamobile.models.** { *; }

-obfuscationdictionary "C:\Users\ASUS\Documents\GitHub\2207-QR-Code-Scanner\gistfile1.txt"
-classobfuscationdictionary "C:\Users\ASUS\Documents\GitHub\2207-QR-Code-Scanner\gistfile1.txt"
-packageobfuscationdictionary "C:\Users\ASUS\Documents\GitHub\2207-QR-Code-Scanner\gistfile1.txt"

-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses 'com.droidekamobile'