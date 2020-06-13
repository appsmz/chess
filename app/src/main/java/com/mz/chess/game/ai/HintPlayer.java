package com.mz.chess.game.ai;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.Game;

import java.lang.ref.WeakReference;

public class HintPlayer {

    private final FigureColor hintColor;
    private WeakReference<Game> game;
    private Board board;
    private String boardString;

    public HintPlayer(FigureColor hintColor, Game game, Board board) {
        this.hintColor = hintColor;
        this.game = new WeakReference<>(game);
        this.board = board;
        this.boardString = board.getBoardString();
    }

    public void getHint() {
        String fen = FenBuilder.buildFenString(board, hintColor, game.get().gameState, hintColor == FigureColor.BLACK);

        startEngineWithMaxElo();

        findBestMoveStockfishHint(fen);
    }

    private void startEngineWithMaxElo() {
        engineMainHint();
    }

    public native String engineMainHint();

    public native String findBestMoveStockfishHint(String fen);

    public void handleStockfishBestMove(String stockfishResponse) {
        String bestMoveString = stockfishResponse.split(" ")[1];

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestMove = new Pair<>(new Pair<>(7 - (bestMoveString.charAt(1) - '1'), bestMoveString.charAt(0) - 'a'),
                new Pair<>(7 - (bestMoveString.charAt(3) - '1'), bestMoveString.charAt(2) - 'a'));

        if (hintColor == FigureColor.BLACK) {
            bestMove = new Pair<>(new Pair<>(7 - bestMove.first.first, 7 - bestMove.first.second),
                    new Pair<>(7 - bestMove.second.first, 7 - bestMove.second.second));
        }

        game.get().handleHint(bestMove, boardString);
    }
}
