/*
  Stockfish, a UCI chess playing engine derived from Glaurung 2.1
  Copyright (C) 2004-2008 Tord Romstad (Glaurung author)
  Copyright (C) 2008-2015 Marco Costalba, Joona Kiiski, Tord Romstad
  Copyright (C) 2015-2020 Marco Costalba, Joona Kiiski, Gary Linscott, Tord Romstad

  Stockfish is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  Stockfish is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <iostream>

#include "bitboard.h"
#include "position.h"
#include "search.h"
#include "thread.h"
#include "tt.h"
#include "uci.h"
#include "endgame.h"
#include "syzygy/tbprobe.h"
#include "../../../../../../../Android/ndk/20.1.5948944/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/jni.h"

namespace PSQT {
  void init();
}

Search::JavaGameRef g_ctx;

//int main(int argc, char* argv[]) {
extern "C" JNIEXPORT jstring JNICALL
Java_com_mz_chess_game_ai_CPUPlayer_engineMain(JNIEnv* env, jobject thiz, jint skillLevel) {

//  std::cout << engine_info() << std::endl;

  jclass clz = env->GetObjectClass(thiz);
  g_ctx.gameClass = static_cast<jclass>(env->NewGlobalRef(clz));
  g_ctx.gameObject = env->NewGlobalRef(thiz);

  Options["Skill Level"] = (int) skillLevel;

  UCI::init(Options);
  PSQT::init();
  Bitboards::init();
  Position::init();
  Bitbases::init();
  Endgames::init();
  Threads.set(1);
  Search::setJavaGameRef(&g_ctx);
  Search::clear(); // After threads are up

  return env->NewStringUTF("");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mz_chess_game_ai_HintPlayer_engineMainHint(JNIEnv* env, jobject thiz) {
    return Java_com_mz_chess_game_ai_CPUPlayer_engineMain(env, thiz, 20);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    memset(&g_ctx, 0, sizeof(g_ctx));

    g_ctx.javaVM = vm;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    g_ctx.gameClass = NULL;
    g_ctx.gameObject = NULL;
    return  JNI_VERSION_1_6;
}