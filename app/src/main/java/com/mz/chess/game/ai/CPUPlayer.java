package com.mz.chess.game.ai;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;
import com.mz.chess.game.Game;
import com.mz.chess.game.moves.PossibleMovesGenerator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.SneakyThrows;

public class CPUPlayer {

    private final FigureColor cpuColor;
    private WeakReference<Game> game;
    private Difficulty difficulty;
    private Board board;
    private String boardString;

    public CPUPlayer(FigureColor cpuColor, int difficultyLevel, Game game, Board board) {
        this.cpuColor = cpuColor;
        this.difficulty = Difficulty.fromInt(difficultyLevel);
        this.game = new WeakReference<>(game);
        this.board = board;
        this.boardString = board.getBoardString();
    }

    @SneakyThrows
    public void makeMove() {
        Thread.sleep(200);
        int possibleMoves = PossibleMovesGenerator.generatePossibleMoves(board, cpuColor);
        if (possibleMoves == 0) {
            game.get().handleCPUMove(null, FigureType.EMPTY, boardString);
        } else {
            makeMoveForDifficulty(board);
        }
    }

    private void makeMoveForDifficulty(Board board) {
        if (difficulty == Difficulty.DIFF0) {
            float r = (new Random()).nextFloat();
            if (r < 0.5) {
                selectMostProfitableMove(board);
            } else {
                makeRandomMove(board);
            }
        } else if (difficulty == Difficulty.DIFF1) {
            float r = (new Random()).nextFloat();
            if (r < 0.5) {
                findBestMove(board);
            } else if (r < 0.8) {
                selectMostProfitableMove(board);
            } else {
                makeRandomMove(board);
            }
        } else if (difficulty == Difficulty.DIFF2) {
            float r = (new Random()).nextFloat();
            if (r < 0.6) {
                findBestMove(board);
            } else if (r < 0.9) {
                selectMostProfitableMove(board);
            } else {
                makeRandomMove(board);
            }
        } else if (difficulty == Difficulty.DIFF3) {
            float r = (new Random()).nextFloat();
            if (r < 0.9) {
                findBestMove(board);
            } else {
                selectMostProfitableMove(board);
            }
        } else if (difficulty == Difficulty.DIFF4) {
            float r = (new Random()).nextFloat();
            if (r < 0.95) {
                findBestMove(board);
            } else {
                selectMostProfitableMove(board);
            }
        } else {
            findBestMove(board);
        }
    }

    private void makeRandomMove(Board board) {
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves = new ArrayList<>();

        for (Field[] fieldRow : board.getFields()) {
            for (Field field : fieldRow) {
                if (field.getFigure().getColor() == cpuColor) {
                    for (Pair<Integer, Integer> possibleMove : field.getFigure().getPossibleMoves()) {
                        moves.add(new Pair<>(new Pair<>(field.getX(), field.getY()), new Pair<>(possibleMove.first, possibleMove.second)));
                    }
                }
            }
        }

        game.get().handleCPUMove(moves.get((new Random()).nextInt(moves.size())), FigureType.QUEEN, boardString);
    }

    private void findBestMove(Board board) {
        String fen = FenBuilder.buildFenString(board, cpuColor, game.get().gameState, cpuColor == FigureColor.WHITE);

        startEngineWithElo();

        findBestMoveStockfish(fen, difficulty.getDepth(), difficulty.getMoveTime(), (int) game.get().gameState.getWhitePlayerTimeLeft(),
                (int) game.get().gameState.getBlackPlayerTimeLeft(), game.get().gameState.getAdditionalMoveTimeInMilliSeconds());
    }

    private void startEngineWithElo() {
        engineMain(difficulty.getSkillLevel());
    }

    public native String engineMain(int skillLevel);

    public native String findBestMoveStockfish(String fen, int depth, int time, int wtime, int btime, int inc);

    public void handleStockfishBestMove(String stockfishResponse) {
        String bestMoveString = stockfishResponse.split(" ")[1];

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestMove = new Pair<>(new Pair<>(7 - (bestMoveString.charAt(1) - '1'), bestMoveString.charAt(0) - 'a'),
                new Pair<>(7 - (bestMoveString.charAt(3) - '1'), bestMoveString.charAt(2) - 'a'));

        if (cpuColor == FigureColor.WHITE) {
            bestMove = new Pair<>(new Pair<>(7 - bestMove.first.first, 7 - bestMove.first.second),
                    new Pair<>(7 - bestMove.second.first, 7 - bestMove.second.second));
        }

        FigureType figureType = handlePromote(bestMoveString);

        game.get().handleCPUMove(bestMove, figureType, boardString);
    }

    private FigureType handlePromote(String bestMoveString) {
        if (bestMoveString.length() < 5) {
            return FigureType.EMPTY;
        }

        String c = String.valueOf(bestMoveString.charAt(4)).toLowerCase();
        switch (c) {
            case "n": return FigureType.KNIGHT;
            case "b": return FigureType.BISHOP;
            case "r": return FigureType.ROOK;
            default: return FigureType.QUEEN;
        }
    }

    private void selectMostProfitableMove(Board board) {
        int maxMaterialValue = Integer.MIN_VALUE;
        List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> bestMoves = new ArrayList<>();

        for (Field[] fieldRow : board.getFields()) {
            for (Field field : fieldRow) {
                if (field.getFigure().getColor() == cpuColor) {
                    for (Pair<Integer, Integer> possibleMove : field.getFigure().getPossibleMoves()) {
                        Board copy = board.copy(field.getX(), field.getY(), possibleMove.first, possibleMove.second);
                        int materialValue = calculateMaterialValue(copy);

                        if (materialValue > maxMaterialValue) {
                            maxMaterialValue = materialValue;
                            bestMoves.clear();
                            bestMoves.add(new Pair<>(new Pair<>(field.getX(), field.getY()), new Pair<>(possibleMove.first, possibleMove.second)));
                        } else if (materialValue == maxMaterialValue) {
                            bestMoves.add(new Pair<>(new Pair<>(field.getX(), field.getY()), new Pair<>(possibleMove.first, possibleMove.second)));
                        }
                    }
                }
            }
        }

        game.get().handleCPUMove(bestMoves.get((new Random()).nextInt(bestMoves.size())), FigureType.QUEEN, boardString);
    }

    private int calculateMaterialValue(Board board) {
        int value = 0;

        if (board.isCheck(cpuColor.getOppositeColor())) {
            value += FigureType.PAWN.getMaterialValue() - 1;
        }

        for (Field[] fieldRow : board.getFields()) {
            for (Field field : fieldRow) {
                int figureValue = getFigureMaterialValue(field);
                value = field.getFigure().getColor() == cpuColor ? value + figureValue : value - figureValue;
            }
        }

        return value;
    }

    private int getFigureMaterialValue(Field field) {
        if (field.getFigure().getType() == FigureType.PAWN && (field.getX() == 0 || field.getX() == 7)) {
            return FigureType.QUEEN.getMaterialValue();
        }

        return field.getFigure().getType().getMaterialValue();
    }

    public FigureColor getPlayerColor() {
        return cpuColor;
    }
}
