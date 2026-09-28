#include <jni.h>
#include <android/api-level.h>
#include <cstdlib>
#include <cstdint>

static JavaVM* g_vm = nullptr;
static jclass g_vibrator_class = nullptr;
static jobject g_vibrator = nullptr;
static jmethodID g_get_system_service = nullptr;
static jmethodID g_vibrate_long = nullptr;
static jmethodID g_vibrate_effect = nullptr;
static jmethodID g_create_one_shot = nullptr;

static constexpr int VIBRATOR_SERVICE = 14;
static constexpr int VIBRATE_EFFECT_DEFAULT_AMPLITUDE = -1;

static bool parse_app(jobject& app) {
    const char* raw = std::getenv("DALVIK_APPLICATION");
    if (!raw || !*raw) return false;
    char* end = nullptr;
    uintptr_t ptr = static_cast<uintptr_t>(std::strtoull(raw, &end, 10));
    if (end == raw || ptr == 0) return false;
    app = reinterpret_cast<jobject>(ptr);
    return true;
}

static bool init_vibrator(JNIEnv* env) {
    if (g_vibrator) return true;
    jobject app = nullptr;
    if (!parse_app(app)) return false;

    jclass context_class = env->FindClass("android/content/Context");
    if (!context_class) return false;
    jfieldID vibrator_service = env->GetStaticFieldID(context_class, "VIBRATOR_SERVICE", "Ljava/lang/String;");
    jstring service_name = (jstring)env->GetStaticObjectField(context_class, vibrator_service);

    jclass app_class = env->GetObjectClass(app);
    g_get_system_service = env->GetMethodID(app_class, "getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");
    if (!g_get_system_service) return false;

    jobject vibrator_obj = env->CallObjectMethod(app, g_get_system_service, service_name);
    if (env->ExceptionCheck() || !vibrator_obj) {
        env->ExceptionClear();
        return false;
    }

    jclass vibrator_class_local = env->GetObjectClass(vibrator_obj);
    g_vibrator_class = (jclass)env->NewGlobalRef(vibrator_class_local);
    g_vibrator = env->NewGlobalRef(vibrator_obj);

    g_vibrate_long = env->GetMethodID(g_vibrator_class, "vibrate", "(J)V");
    if (env->GetMethodID(g_vibrator_class, "vibrate", "(Landroid/os/VibrationEffect;)V")) {
        g_vibrate_effect = env->GetMethodID(g_vibrator_class, "vibrate", "(Landroid/os/VibrationEffect;)V");
        jclass effect_class = env->FindClass("android/os/VibrationEffect");
        if (effect_class) {
            g_create_one_shot = env->GetStaticMethodID(effect_class, "createOneShot", "(JI)Landroid/os/VibrationEffect;");
        }
    }

    return g_vibrate_long || (g_vibrate_effect && g_create_one_shot);
}

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

static bool do_vibrate(int duration_ms, int amplitude) {
    if (!g_vm) return false;
    JNIEnv* env = nullptr;
    if (g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK || !env) {
        if (g_vm->AttachCurrentThread(&env, nullptr) != JNI_OK) return false;
    }
    if (!init_vibrator(env)) return false;

    if (g_vibrate_effect && g_create_one_shot && amplitude >= 1 && amplitude <= 255) {
        jclass effect_class = env->FindClass("android/os/VibrationEffect");
        jobject effect = env->CallStaticObjectMethod(effect_class, g_create_one_shot,
                                                       (jlong)duration_ms, (jint)amplitude);
        if (!env->ExceptionCheck() && effect) {
            env->CallVoidMethod(g_vibrator, g_vibrate_effect, effect);
            if (!env->ExceptionCheck()) return true;
            env->ExceptionClear();
        } else {
            env->ExceptionClear();
        }
    }

    if (g_vibrate_long) {
        env->CallVoidMethod(g_vibrator, g_vibrate_long, (jlong)duration_ms);
        if (!env->ExceptionCheck()) return true;
        env->ExceptionClear();
    }
    return false;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_ru_mobilehaptics_NativeVibrator_nativeVibrate(JNIEnv*, jclass, jint durationMs, jint amplitude) {
    if (durationMs < 1) durationMs = 1;
    if (durationMs > 5000) durationMs = 5000;
    if (amplitude < 1) amplitude = 1;
    if (amplitude > 255) amplitude = 255;
    return do_vibrate(durationMs, amplitude) ? JNI_TRUE : JNI_FALSE;
}
