#include "../../../../../../../Android/ndk/20.1.5948944/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/jni.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_mz_chess_menu_MainMenu_stringFromJNI( JNIEnv* env,
                                                  jobject thiz, jstring str )
{
    const char *nativeString = env->GetStringUTFChars(str, 0);
    return env->NewStringUTF("abcddupa");
}