# Retrofit / OkHttp — keep annotations and generic signatures used at runtime
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Moshi codegen — keep generated *JsonAdapter classes and the models they serialize
-keep class com.banaoreel.app.data.model.** { *; }
-keep class com.banaoreel.app.data.model.**JsonAdapter { *; }
-keepclassmembers class com.banaoreel.app.data.model.** {
    <init>(...);
}

# Razorpay checkout SDK (their AAR ships consumer rules too, this is a safety net)
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

# Hilt-generated components
-keep class dagger.hilt.internal.** { *; }
