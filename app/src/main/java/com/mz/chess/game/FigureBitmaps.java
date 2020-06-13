package com.mz.chess.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.mz.chess.R;

import java.util.HashMap;
import java.util.Map;

class FigureBitmaps {

    private Map<FigureType, Bitmap> whiteBitmaps = new HashMap<>();
    private Map<FigureType, Bitmap> blackBitmaps = new HashMap<>();

    FigureBitmaps(Context context, boolean isDarkTheme) {
        if (isDarkTheme) {
            loadWhiteDark(context);
        } else {
            loadWhite(context);
        }
        loadBlack(context);
    }

    private void loadWhite(Context context) {
        Bitmap whitePawn = BitmapFactory.decodeResource(context.getResources(), R.drawable.pawn_white);
        whiteBitmaps.put(FigureType.PAWN, whitePawn);

        Bitmap whiteBishop = BitmapFactory.decodeResource(context.getResources(), R.drawable.bishop_white);
        whiteBitmaps.put(FigureType.BISHOP, whiteBishop);

        Bitmap whiteKnight = BitmapFactory.decodeResource(context.getResources(), R.drawable.knight_white);
        whiteBitmaps.put(FigureType.KNIGHT, whiteKnight);

        Bitmap whiteRook = BitmapFactory.decodeResource(context.getResources(), R.drawable.rook_white);
        whiteBitmaps.put(FigureType.ROOK, whiteRook);

        Bitmap whiteQueen = BitmapFactory.decodeResource(context.getResources(), R.drawable.queen_white);
        whiteBitmaps.put(FigureType.QUEEN, whiteQueen);

        Bitmap whiteKing = BitmapFactory.decodeResource(context.getResources(), R.drawable.king_white);
        whiteBitmaps.put(FigureType.KING, whiteKing);
    }

    private void loadBlack(Context context) {
        Bitmap blackPawn = BitmapFactory.decodeResource(context.getResources(), R.drawable.pawn_black);
        blackBitmaps.put(FigureType.PAWN, blackPawn);

        Bitmap blackBishop = BitmapFactory.decodeResource(context.getResources(), R.drawable.bishop_black);
        blackBitmaps.put(FigureType.BISHOP, blackBishop);

        Bitmap blackRook = BitmapFactory.decodeResource(context.getResources(), R.drawable.rook_black);
        blackBitmaps.put(FigureType.ROOK, blackRook);

        Bitmap blackKnight = BitmapFactory.decodeResource(context.getResources(), R.drawable.knight_black);
        blackBitmaps.put(FigureType.KNIGHT, blackKnight);

        Bitmap blackQueen = BitmapFactory.decodeResource(context.getResources(), R.drawable.queen_black);
        blackBitmaps.put(FigureType.QUEEN, blackQueen);

        Bitmap blackKing = BitmapFactory.decodeResource(context.getResources(), R.drawable.king_black);
        blackBitmaps.put(FigureType.KING, blackKing);
    }

    private void loadWhiteDark(Context context) {
        Bitmap whitePawn = BitmapFactory.decodeResource(context.getResources(), R.drawable.pawn_white_dark);
        whiteBitmaps.put(FigureType.PAWN, whitePawn);

        Bitmap whiteBishop = BitmapFactory.decodeResource(context.getResources(), R.drawable.bishop_white_dark);
        whiteBitmaps.put(FigureType.BISHOP, whiteBishop);

        Bitmap whiteKnight = BitmapFactory.decodeResource(context.getResources(), R.drawable.knight_white_dark);
        whiteBitmaps.put(FigureType.KNIGHT, whiteKnight);

        Bitmap whiteRook = BitmapFactory.decodeResource(context.getResources(), R.drawable.rook_white_dark);
        whiteBitmaps.put(FigureType.ROOK, whiteRook);

        Bitmap whiteQueen = BitmapFactory.decodeResource(context.getResources(), R.drawable.queen_white_dark);
        whiteBitmaps.put(FigureType.QUEEN, whiteQueen);

        Bitmap whiteKing = BitmapFactory.decodeResource(context.getResources(), R.drawable.king_white_dark);
        whiteBitmaps.put(FigureType.KING, whiteKing);
    }

    public Bitmap getFigureBitmap(Figure figure) {
        return getFigureBitmap(figure.getColor(), figure.getType());
    }

    public Bitmap getFigureBitmap(FigureColor color, FigureType type) {
        Map<FigureType, Bitmap> map = color == FigureColor.WHITE ? whiteBitmaps : blackBitmaps;
        return map.get(type);
    }

    public Rect getSrcRect() {
        return new Rect(0, 0, whiteBitmaps.get(FigureType.KING).getWidth(), whiteBitmaps.get(FigureType.KING).getHeight());
    }
}
