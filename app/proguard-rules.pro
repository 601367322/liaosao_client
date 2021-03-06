# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class !android.support.v7.internal.view.menu.**,android.support.v7.** {*;}
-keep class android.support.v7.** {*;}

-keep class c.b.BP
-keep class c.b.PListener
-keep class c.b.QListener
-keepclasseswithmembers class c.b.BP{ *; }
-keepclasseswithmembers class * implements c.b.PListener{ *; }
-keepclasseswithmembers class * implements c.b.QListener{ *; }