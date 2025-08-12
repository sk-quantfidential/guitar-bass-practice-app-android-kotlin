# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep all model classes for Room and Gson
-keep class com.quantfidential.guitarbasspractice.data.model.** { *; }
-keep class com.quantfidential.guitarbasspractice.data.proto.** { *; }

# Keep all Retrofit interfaces
-keep class com.quantfidential.guitarbasspractice.data.api.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }

# Protocol Buffers
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }