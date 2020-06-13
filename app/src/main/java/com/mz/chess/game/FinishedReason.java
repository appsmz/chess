package com.mz.chess.game;

import com.mz.chess.R;

enum FinishedReason {
    CHECKMATE(R.string.checkmate), NO_TIME(R.string.no_time),
    NO_MATERIAL(R.string.draw_no_material), MOVES_50(R.string.draw_moves_50),
    REPEATED_3_TIMES(R.string.draw_repeated_3_times), NO_POSSIBLE_MOVES(R.string.draw_no_moves);

    private int reasonStringId;

    FinishedReason(int reasonStringId) {
        this.reasonStringId = reasonStringId;
    }

    public int getReasonStringId() {
        return reasonStringId;
    }
}
