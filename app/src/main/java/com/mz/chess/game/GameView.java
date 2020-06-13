package com.mz.chess.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.mz.chess.R;
import com.mz.chess.menu.GameMode;

import java.util.List;

public class GameView extends View {

    private GameDimensions gameDimensions;
    private FigureBitmaps figureBitmaps;
    private Bitmap boardWhite;
    private Bitmap boardBlack;
    private Bitmap possibleMoveBitmap;
    private Bitmap lastMoveBitmap;
    private Bitmap hintMoveBitmap;
    private Bitmap undoBitmap;
    private Bitmap redoBitmap;
    private Bitmap hintBitmap;
    private Rect undoRedoSrcRect;
    private Rect boardSrcRect;
    private Rect figureSrcRect;
    private boolean dimensionsInitialized = false;
    private Game game;
    private Bitmap whitePlayerIcon;
    private Bitmap blackPlayerIcon;
    private RectF undoRect;
    private RectF redoRect;
    private RectF hintRect;

    private Rect mainMessageRect;
    private Rect reasonRect;
    private RectF newGameRect;
    private RectF menuRect;

    private Rect whitePlayerIconDstRect;
    private Rect blackPlayerIconDstRect;
    private Rect boardBorderRect;
    private Paint boardBorder = new Paint();

    private Paint gameFinishedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint gameFinishedMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint buttonPressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint buttonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint timeText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF selectedButton = null;
    private Rect finishedNewGameSize = new Rect();
    private Rect finishedMenuSize = new Rect();
    private Rect finishedMainSize = new Rect();
    private Rect finishedSize = new Rect();

    private Rect panelTopBorderRect;
    private Rect panelTopRect;
    private Rect panelBottomBorderRect;
    private Rect panelBottomRect;
    private Paint panelBorderPaint;
    private Paint panelBorderSelectedPaint;
    private Paint panelPaint;

    private Paint playerNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect playerNameSize = new Rect();
    private Rect playerNameRect;
    private Rect playerTopNameRect;
    private String playerNameString;
    private String playerTopNameString;
    private int beatenSize;
    private int beatenMargin;

    private Rect panelSrcRect;
    private Bitmap panelBitmap;
    private Rect bgSrcRect;
    private Bitmap bgBitmap;
    private Rect bgTopDstRect;
    private Rect bgBottomDstRect;
    private Path activePlayerPathTop;
    private Path activePlayerPathBottom;
    private Paint undoRedoDisabledPaint;
    private Paint undoRedoPaint;
    private Paint undoRedoDisabledBitmapPaint;

    private Bitmap thinkingBitmap;
    private Rect thinkingSrcRect;
    private Rect thinkingDstRect;
    private Rect thinkingMainPlayerDstRect;

    private Bitmap bgFilterBitmap;
    private Rect bgFilterSrcRect;
    private Rect bgFilterDstRect;

    public boolean drawLoading = false;

    public GameView(Context context) {
        super(context);

        this.game = (Game) context;

        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        dimensionsInitialized = false;
    }

    private void init(Context context) {

        panelBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.light_panel);
        panelSrcRect = new Rect(0, 0, panelBitmap.getWidth(), panelBitmap.getHeight());

        panelBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        panelBorderPaint.setColor(getResources().getColor(R.color.panelBorder));
        panelBorderPaint.setAntiAlias(true);
        panelBorderSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        panelBorderSelectedPaint.setAntiAlias(true);

        initTheme();

        figureBitmaps = new FigureBitmaps(context, game.isDarkTheme);
        whitePlayerIcon = figureBitmaps.getFigureBitmap(FigureColor.WHITE, FigureType.PAWN);
        blackPlayerIcon = figureBitmaps.getFigureBitmap(FigureColor.BLACK, FigureType.PAWN);

        loadBoardBitmaps();

        initSrcRects();

        timeText.setStyle(Paint.Style.FILL);
        timeText.setTextAlign(Paint.Align.LEFT);
        timeText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        gameFinishedPaint.setTypeface(game.application.getTypeface());
        gameFinishedPaint.setLetterSpacing(0.04f);
        gameFinishedMainPaint.setTypeface(game.application.getTypeface());
        gameFinishedMainPaint.setLetterSpacing(0.04f);
        buttonTextPaint.setTypeface(game.application.getTypeface());
        buttonTextPaint.setLetterSpacing(0.05f);

        playerNamePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        playerNamePaint.setLetterSpacing(0.04f);
        playerNamePaint.setTextAlign(Paint.Align.LEFT);

        undoRedoPaint = new Paint();
        undoRedoPaint.setColor(getResources().getColor(R.color.gameButtonNormal));
        undoRedoDisabledPaint = new Paint();
        undoRedoDisabledPaint.setColor(getResources().getColor(R.color.gameButtonDisabled));

        undoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.undo);
        redoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redo);
        hintBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hint);
        undoRedoSrcRect = new Rect(0, 0, undoBitmap.getWidth(), undoBitmap.getHeight());
        undoRedoDisabledBitmapPaint = new Paint();
        undoRedoDisabledBitmapPaint.setAlpha(42);

        thinkingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hourglass);
        thinkingSrcRect = new Rect(0, 0, thinkingBitmap.getWidth(), thinkingBitmap.getHeight());

        bgFilterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.backgroud_filter);
        bgFilterSrcRect = new Rect(0, 0, bgFilterBitmap.getWidth(), bgFilterBitmap.getHeight());
    }

    private void loadBoardBitmaps() {
        this.possibleMoveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.possible_move);
        this.lastMoveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.last_move);
        this.hintMoveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hint_move);
    }

    private void initSrcRects() {
        this.boardSrcRect = new Rect(0, 0, boardWhite.getWidth(), boardWhite.getHeight());
        this.figureSrcRect = figureBitmaps.getSrcRect();
    }

    private void initTheme() {
        boolean isDarkTheme = game.isDarkTheme;
        if (isDarkTheme) {
            setDarkTheme();
        } else {
            setThemeDefault();
        }
    }

    private void setThemeDefault() {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.light_bg);
        bgSrcRect = new Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());

        boardBorder.setColor(Color.parseColor("#707070"));
        boardWhite = BitmapFactory.decodeResource(getResources(), R.drawable.light);
        boardBlack = BitmapFactory.decodeResource(getResources(), R.drawable.dark);

        gameFinishedPaint.setColor(Color.BLACK);
        gameFinishedMainPaint.setColor(Color.BLACK);
        buttonTextPaint.setColor(Color.WHITE);
        buttonPaint.setColor(getResources().getColor(R.color.colorAccent));
        buttonPressedPaint.setColor(getResources().getColor(R.color.colorAccentPressed));
        panelBorderSelectedPaint.setColor(getResources().getColor(R.color.panelBorderSelected));

        panelPaint = new Paint();
        panelPaint.setColor(getResources().getColor(R.color.panelColor));

        playerNamePaint.setColor(Color.BLACK);
        timeText.setColor(Color.BLACK);
    }

    private void setDarkTheme() {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dark_bg);
        bgSrcRect = new Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());

        boardBorder.setColor(Color.parseColor("#cccccc"));
        boardWhite = BitmapFactory.decodeResource(getResources(), R.drawable.light_dark);
        boardBlack = BitmapFactory.decodeResource(getResources(), R.drawable.dark_dark);

        gameFinishedPaint.setColor(getResources().getColor(R.color.darkTextColor));
        gameFinishedMainPaint.setColor(getResources().getColor(R.color.darkTextColor));
        buttonTextPaint.setColor(getResources().getColor(R.color.darkTextColor));
        buttonPaint.setColor(getResources().getColor(R.color.colorAccentDark));
        buttonPressedPaint.setColor(getResources().getColor(R.color.colorAccentDarkPressed));
        panelBorderSelectedPaint.setColor(getResources().getColor(R.color.panelBorderSelectedDark));

        panelPaint = new Paint();
        panelPaint.setColor(getResources().getColor(R.color.panelColorDark));

        playerNamePaint.setColor(Color.BLACK);
        timeText.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!dimensionsInitialized) {
            initDimensions();
        }

        canvas.drawBitmap(bgBitmap, bgSrcRect, bgTopDstRect, null);
        canvas.drawBitmap(bgBitmap, bgSrcRect, bgBottomDstRect, null);

        if (game.gameFinished) {
            drawGameFinished(canvas);
        } else {
            drawGameStatus(canvas);
        }

        drawChessboard(canvas);

        if (drawLoading) {
            canvas.drawBitmap(bgFilterBitmap, bgFilterSrcRect, bgFilterDstRect, null);
        }
    }

    private void drawGameFinished(Canvas canvas) {
        String mainMessage;
        String reason;

        if (game.winner == FigureColor.NONE) {
            mainMessage = game.getString(R.string.draw_title);
            reason = game.getString(game.finishedReason.getReasonStringId());

            gameFinishedPaint.getTextBounds(reason, 0, reason.length(), finishedSize);
            canvas.drawText(reason, getWidth() / 2 - finishedSize.width() / 2, reasonRect.top, gameFinishedPaint);
        } else {
            mainMessage = game.getString(game.winner == FigureColor.WHITE ? R.string.white : R.string.black) + " " + game.getString(R.string.wins_message);
        }

        gameFinishedMainPaint.getTextBounds(mainMessage, 0, mainMessage.length(), finishedMainSize);
        canvas.drawText(mainMessage, getWidth() / 2 - finishedMainSize.width() / 2, mainMessageRect.centerY(), gameFinishedMainPaint);

        canvas.drawRoundRect(newGameRect, 5, 5, selectedButton == newGameRect ? buttonPressedPaint : buttonPaint);
        canvas.drawText(game.getString(R.string.new_game_label_finished), newGameRect.centerX() - finishedNewGameSize.width() / 2, newGameRect.centerY() + finishedMenuSize.height() / 2, buttonTextPaint);
        canvas.drawRoundRect(menuRect, 5, 5, selectedButton == menuRect ? buttonPressedPaint : buttonPaint);
        canvas.drawText(game.getString(R.string.main_menu_label), menuRect.centerX() - finishedMenuSize.width() / 2, menuRect.centerY() + finishedMenuSize.height() / 2, buttonTextPaint);

        int iconSize = (int) (finishedMainSize.height() * 1.5);
        if (game.winner == FigureColor.NONE || game.winner == FigureColor.WHITE) {
            int left = getWidth() / 2 - finishedMainSize.width() / 2 - iconSize - iconSize / 4;
            int top = mainMessageRect.centerY() - iconSize + finishedMainSize.height() / 5;
            Rect mainMessageKingWhite = new Rect(left, top, left + iconSize, top + iconSize);
            canvas.drawBitmap(figureBitmaps.getFigureBitmap(FigureColor.WHITE, FigureType.KING), figureSrcRect, mainMessageKingWhite, null);
        }
        if (game.winner == FigureColor.NONE || game.winner == FigureColor.BLACK) {
            int left = getWidth() / 2 + finishedMainSize.width() / 2 + iconSize / 4;
            int top = mainMessageRect.centerY() - iconSize + finishedMainSize.height() / 5;
            Rect mainMessageKingBlack = new Rect(left, top, left + iconSize, top + iconSize);
            canvas.drawBitmap(figureBitmaps.getFigureBitmap(FigureColor.BLACK, FigureType.KING), figureSrcRect, mainMessageKingBlack, null);
        }
    }

    private void drawGameStatus(Canvas canvas) {

        if (game.gameState.getCurrentPlayer() == FigureColor.BLACK && game.board.isBlackOnTop() || game.gameState.getCurrentPlayer() == FigureColor.WHITE && !game.board.isBlackOnTop() ) {
            canvas.drawRect(panelTopBorderRect, panelBorderSelectedPaint);
            canvas.drawRect(panelBottomBorderRect, panelBorderPaint);

            canvas.drawBitmap(panelBitmap, panelSrcRect, panelTopRect, null);
            canvas.drawBitmap(panelBitmap, panelSrcRect, panelBottomRect, null);

            canvas.drawPath(activePlayerPathTop, panelBorderSelectedPaint);
        } else {
            canvas.drawRect(panelTopBorderRect, panelBorderPaint);
            canvas.drawRect(panelBottomBorderRect, panelBorderSelectedPaint);

            canvas.drawBitmap(panelBitmap, panelSrcRect, panelTopRect, null);
            canvas.drawBitmap(panelBitmap, panelSrcRect, panelBottomRect, null);

            canvas.drawPath(activePlayerPathBottom, panelBorderSelectedPaint);
        }


        canvas.drawBitmap(whitePlayerIcon, figureSrcRect, whitePlayerIconDstRect, null);
        canvas.drawBitmap(blackPlayerIcon, figureSrcRect, blackPlayerIconDstRect, null);

        if (game.getGameOptions().getGameTime() > 0) {
            canvas.drawText(game.getWhitePlayerTime(), (float) (getWidth() * 0.25), (float) ((game.board.isBlackOnTop() ? playerNameRect.centerY() : playerTopNameRect.centerY()) + playerNameRect.height() * 1.2), timeText);
            canvas.drawText(game.getBlackPlayerTime(), (float) (getWidth() * 0.25), (float) ((game.board.isBlackOnTop() ? playerTopNameRect.centerY() : playerNameRect.centerY()) + playerNameRect.height() * 1.2), timeText);
        }

        canvas.drawText(playerNameString, playerNameRect.left, playerNameRect.centerY(), playerNamePaint);
        canvas.drawText(playerTopNameString, playerTopNameRect.left, playerTopNameRect.centerY(), playerNamePaint);

        drawBeatenFigures(canvas);

        drawUndoRedoButtons(canvas);

        drawThinkingIcons(canvas);
    }

    private void drawThinkingIcons(Canvas canvas) {
        if (game.gameOptions.getGameMode() == GameMode.SINGLE && (game.gameState.getCurrentPlayer() == FigureColor.BLACK && game.board.isBlackOnTop() || game.gameState.getCurrentPlayer() == FigureColor.WHITE && !game.board.isBlackOnTop())) {
            canvas.drawBitmap(thinkingBitmap, thinkingSrcRect, thinkingDstRect, null);
        }

        if (game.hintInProgress) {
            canvas.drawBitmap(thinkingBitmap, thinkingSrcRect, thinkingMainPlayerDstRect, null);
        }
    }

    private void drawUndoRedoButtons(Canvas canvas) {
        canvas.drawRoundRect(undoRect, 5, 5, game.stateHistory.isUndoPossible() ? undoRedoPaint : undoRedoDisabledPaint);
        canvas.drawBitmap(undoBitmap, undoRedoSrcRect, undoRect, game.stateHistory.isUndoPossible() ? null : undoRedoDisabledBitmapPaint);
        canvas.drawRoundRect(redoRect, 5, 5, game.stateHistory.isRedoPossible() ? undoRedoPaint : undoRedoDisabledPaint);
        canvas.drawBitmap(redoBitmap, undoRedoSrcRect, redoRect, game.stateHistory.isRedoPossible() ? null : undoRedoDisabledBitmapPaint);

        if (game.gameOptions.getGameMode() == GameMode.SINGLE) {
            canvas.drawRoundRect(hintRect, 5, 5, game.isHintEnabled() ? undoRedoPaint : undoRedoDisabledPaint);
            canvas.drawBitmap(hintBitmap, undoRedoSrcRect, hintRect, game.isHintEnabled() ? null : undoRedoDisabledBitmapPaint);
        }
    }

    private void drawBeatenFigures(Canvas canvas) {
        drawBeaten(canvas, game.board.isBlackOnTop() ? playerTopNameRect : playerNameRect, game.board.getBeatenByBlack());
        drawBeaten(canvas, game.board.isBlackOnTop() ? playerNameRect : playerTopNameRect, game.board.getBeatenByWhite());
    }

    private void drawBeaten(Canvas canvas, Rect nameRect, List<Figure> beaten) {
        Rect beatenRect = new Rect((int) (nameRect.left - beatenSize * 0.25), nameRect.bottom + nameRect.height(), (int) (nameRect.left + beatenSize * 0.75), nameRect.bottom + nameRect.height() + beatenSize);
        for (int i = 0; i < beaten.size(); i++) {
            canvas.drawBitmap(figureBitmaps.getFigureBitmap(beaten.get(i)), figureSrcRect, beatenRect, null);

            if (i == 8) {
                beatenRect = new Rect((int) (nameRect.left - beatenSize * 0.75), (int) (nameRect.bottom + nameRect.height() + 0.75 * beatenSize), (int) (nameRect.left + beatenSize * 0.25), (int) (nameRect.bottom + nameRect.height() + 1.75 * beatenSize));
            }

            beatenRect.left = beatenRect.left + beatenSize + beatenMargin;
            beatenRect.right = beatenRect.right + beatenSize + beatenMargin;
        }
    }

    private void drawChessboard(Canvas canvas) {
        canvas.drawRect(boardBorderRect, boardBorder);

        int fieldCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Bitmap fieldBitmap = fieldCounter++ % 2 == 0 ? boardWhite : boardBlack;
                canvas.drawBitmap(fieldBitmap, boardSrcRect, gameDimensions.getFieldDstRect(i, j), null);

                Figure figure = game.board.getFields()[i][j].getFigure();
                if (figure.getType() != FigureType.EMPTY) {
                    canvas.drawBitmap(figureBitmaps.getFigureBitmap(figure), figureSrcRect, gameDimensions.getSmallerFieldDstRect(i, j), null);
                }
            }
            fieldCounter++;
        }

        if (game.getSelectedField() != null) {
            for (Pair<Integer, Integer> fieldCoord : game.getSelectedField().getFigure().getPossibleMoves()) {
                canvas.drawBitmap(possibleMoveBitmap, boardSrcRect, gameDimensions.getFieldDstRect(fieldCoord.first, fieldCoord.second), null);
            }
        }

        if (game.cpuLastMove != null) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = game.cpuLastMove;
            canvas.drawBitmap(lastMoveBitmap, boardSrcRect, gameDimensions.getFieldDstRect(move.first.first, move.first.second), null);
            canvas.drawBitmap(lastMoveBitmap, boardSrcRect, gameDimensions.getFieldDstRect(move.second.first, move.second.second), null);
        }

        if (game.hintMove != null) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = game.hintMove;
            canvas.drawBitmap(hintMoveBitmap, boardSrcRect, gameDimensions.getFieldDstRect(move.first.first, move.first.second), null);
            canvas.drawBitmap(hintMoveBitmap, boardSrcRect, gameDimensions.getFieldDstRect(move.second.first, move.second.second), null);
        }
    }

    private void initDimensions() {

        int boardTopMargin = (int) ((getHeight() - getWidth() * 0.99) / 2);

        gameDimensions = GameDimensions.builder()
                .fieldWidth((int) (getWidth() * 0.99) / 8)
                .figureWidth((int) (getWidth() * 0.99) / 8)
                .sideMargin((int) (getWidth() * 0.005))
                .topMargin(boardTopMargin)
                .build();

        int boardBorderWidth = (int) (getWidth() * 0.005);

        boardBorderRect = new Rect(0, boardTopMargin - boardBorderWidth, getWidth(), (int) (boardTopMargin + getWidth() * 0.99 + boardBorderWidth));

        int playerRectSize = (getHeight() - getWidth()) / 2;
        bgTopDstRect = new Rect(0, 0, getWidth(), playerRectSize);
        bgBottomDstRect = new Rect(0, playerRectSize + getWidth(), getWidth(), getHeight());

        int panelMargin = (int) ((((getHeight() - getWidth()) / 2) - getHeight() * 0.15) / 2);
        panelMargin = panelMargin < 20 ? 20 : panelMargin;
        int panelHeight = (getHeight() - getWidth() - 4 * panelMargin) / 2;
        panelHeight = panelHeight > getHeight() * 0.15 ? (int) (getHeight() * 0.15) : panelHeight;
        panelTopBorderRect = new Rect(0, panelMargin, getWidth(), panelMargin  + panelHeight);
        panelTopRect = new Rect(0, (int) (panelTopBorderRect.top + getHeight() * 0.003), getWidth(), (int) (panelTopBorderRect.bottom - getHeight() * 0.013));
        panelBottomBorderRect = new Rect(0, getHeight() - panelMargin - panelHeight, getWidth(), getHeight() - panelMargin);
        panelBottomRect = new Rect(0, (int) (panelBottomBorderRect.top + getHeight() * 0.003), getWidth(), (int) (panelBottomBorderRect.bottom - getHeight() * 0.013));

        mainMessageRect = new Rect((int) (getWidth() * 0.1), (int) (panelMargin + panelHeight * 0.37), (int) (getWidth() * 0.9), (int) (panelMargin + panelHeight * 0.68));
        reasonRect = new Rect((int) (getWidth() * 0.1), (int) (panelMargin + panelHeight * 0.8), (int) (getWidth() * 0.9), (int) (panelMargin + panelHeight * 0.97));

        newGameRect = new RectF((int) (getWidth() * 0.08), (int) (getHeight() - boardTopMargin * 0.5 - getWidth() * 0.07), (int) (getWidth() * 0.42), (int) (getHeight() - boardTopMargin * 0.5 + getWidth() * 0.07));
        menuRect = new RectF((int) (getWidth() * 0.58), (int) (getHeight() - boardTopMargin * 0.5 - getWidth() * 0.07), (int) (getWidth() * 0.92), (int) (getHeight() - boardTopMargin * 0.5 + getWidth() * 0.07));

        buttonTextPaint.setTextSize(newGameRect.height() * 0.3f);
        gameFinishedPaint.setTextSize(reasonRect.height());
        gameFinishedMainPaint.setTextSize(mainMessageRect.height());

        buttonTextPaint.getTextBounds(game.getString(R.string.new_game_label_finished), 0, game.getString(R.string.new_game_label_finished).length(), finishedNewGameSize);
        buttonTextPaint.getTextBounds(game.getString(R.string.main_menu_label), 0, game.getString(R.string.main_menu_label).length(), finishedMenuSize);

        beatenSize = getWidth() / 22;
        beatenMargin = beatenSize / 13;

        int playerIconMargin = (int) (getWidth() * 0.08);
        int playerIconWidth = panelTopRect.height() * 3 / 5;

        if (game.board.isBlackOnTop()) {
            blackPlayerIconDstRect = new Rect(playerIconMargin, panelTopRect.top + panelTopRect.height() / 5, playerIconMargin + playerIconWidth, panelTopRect.top + panelTopRect.height() * 4 / 5);
            whitePlayerIconDstRect = new Rect(playerIconMargin, panelBottomRect.top + panelBottomRect.height() / 5, playerIconMargin + playerIconWidth, panelBottomRect.top + panelBottomRect.height() * 4 / 5);
        } else {
            whitePlayerIconDstRect = new Rect(playerIconMargin, panelTopRect.top + panelTopRect.height() / 5, playerIconMargin + playerIconWidth, panelTopRect.top + panelTopRect.height() * 4 / 5);
            blackPlayerIconDstRect = new Rect(playerIconMargin, panelBottomRect.top + panelBottomRect.height() / 5, playerIconMargin + playerIconWidth, panelBottomRect.top + panelBottomRect.height() * 4 / 5);
        }

        Point activePlayerTopPoint1 = new Point(panelTopRect.left, (int) (panelTopRect.top + panelTopRect.height() * 0.4));
        Point activePlayerTopPoint2 = new Point((int) (panelTopRect.left + getWidth() * 0.06), (int) (panelTopRect.top + panelTopRect.height() * 0.5));
        Point activePlayerTopPoint3 = new Point(panelTopRect.left, (int) (panelTopRect.top + panelTopRect.height() * 0.6));

        activePlayerPathTop = new Path();
        activePlayerPathTop.setFillType(Path.FillType.EVEN_ODD);
        activePlayerPathTop.moveTo(activePlayerTopPoint1.x, activePlayerTopPoint1.y);
        activePlayerPathTop.lineTo(activePlayerTopPoint2.x, activePlayerTopPoint2.y);
        activePlayerPathTop.lineTo(activePlayerTopPoint3.x, activePlayerTopPoint3.y);
        activePlayerPathTop.lineTo(activePlayerTopPoint1.x, activePlayerTopPoint1.y);
        activePlayerPathTop.close();

        Point activePlayerBottomPoint1 = new Point(panelBottomRect.left, (int) (panelBottomRect.top + panelBottomRect.height() * 0.4));
        Point activePlayerBottomPoint2 = new Point((int) (panelBottomRect.left + getWidth() * 0.06), (int) (panelBottomRect.top + panelBottomRect.height() * 0.5));
        Point activePlayerBottomPoint3 = new Point(panelBottomRect.left, (int) (panelBottomRect.top + panelBottomRect.height() * 0.6));

        activePlayerPathBottom = new Path();
        activePlayerPathBottom.setFillType(Path.FillType.EVEN_ODD);
        activePlayerPathBottom.moveTo(activePlayerBottomPoint1.x, activePlayerBottomPoint1.y);
        activePlayerPathBottom.lineTo(activePlayerBottomPoint2.x, activePlayerBottomPoint2.y);
        activePlayerPathBottom.lineTo(activePlayerBottomPoint3.x, activePlayerBottomPoint3.y);
        activePlayerPathBottom.lineTo(activePlayerBottomPoint1.x, activePlayerBottomPoint1.y);
        activePlayerPathBottom.close();

        timeText.setTextSize(panelHeight / 5);
        playerNamePaint.setTextSize(panelHeight * 0.23f);
        playerNameString = getResources().getString(R.string.player_name);
        playerNamePaint.getTextBounds(playerNameString, 0, playerNameString.length(), playerNameSize);
        playerTopNameString = game.gameOptions.getGameMode() == GameMode.SINGLE ? "CPU " + game.gameOptions.getDifficulty() : getResources().getString(R.string.player_name) + " 2";

        playerTopNameRect = new Rect((int)(getWidth() * 0.25), (int)(panelTopRect.top + panelTopRect.height() * 0.15), (int)(getWidth() * 0.25 + playerNameSize.width()), (int)(panelTopRect.top + panelTopRect.height() * 0.15 + playerNameSize.height()));
        playerNameRect = new Rect((int)(getWidth() * 0.25), (int)(panelBottomRect.top + panelBottomRect.height() * 0.15), (int)(getWidth() * 0.25 + playerNameSize.width()), (int)(panelBottomRect.top + panelBottomRect.height() * 0.15 + playerNameSize.height()));

        undoRect = new RectF((int)(getWidth() - panelBottomRect.height() * 1.15), (int)(panelBottomRect.top + panelBottomRect.height() * 0.05), (int)(getWidth() - panelBottomRect.height() * 0.65), (int)(panelBottomRect.top + panelBottomRect.height() * 0.48));
        redoRect = new RectF((int)(getWidth() - panelBottomRect.height() * 0.6), (int)(panelBottomRect.top + panelBottomRect.height() * 0.05), (int)(getWidth() - panelBottomRect.height() * 0.1), (int)(panelBottomRect.top + panelBottomRect.height() * 0.48));
        hintRect = new RectF((int)(getWidth() - panelBottomRect.height() * 0.6), (int)(panelBottomRect.top + panelBottomRect.height() * 0.52), (int)(getWidth() - panelBottomRect.height() * 0.1), (int)(panelBottomRect.top + panelBottomRect.height() * 0.95));

        thinkingDstRect = new Rect((int) (playerIconMargin + playerIconWidth * 0.6), panelTopRect.top + panelTopRect.height() / 7, (int) (playerIconMargin + playerIconWidth * 0.9), (int) (panelTopRect.top + panelTopRect.height() / 7 + playerIconWidth * 0.3));
        thinkingMainPlayerDstRect = new Rect((int)(getWidth() - panelBottomRect.height() * 0.62), (int)(panelBottomRect.top + panelBottomRect.height() * 0.77), (int)(getWidth() - panelBottomRect.height() * 0.62 + playerIconWidth * 0.3), (int)(panelBottomRect.top + panelBottomRect.height() * 0.77 + playerIconWidth * 0.3));

        bgFilterDstRect = new Rect(0, 0, getWidth(), getHeight());

        dimensionsInitialized = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!dimensionsInitialized || game.hintInProgress) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX(), y = (int) event.getY();
            if (!game.gameFinished && undoRect.contains(x, y)) {
                selectedButton = undoRect;
            } else if (!game.gameFinished && redoRect.contains(x, y)) {
                selectedButton = redoRect;
            } else if (!game.gameFinished && hintRect.contains(x, y)) {
                selectedButton = hintRect;
            } else if (game.gameFinished && newGameRect.contains(x, y)) {
                selectedButton = newGameRect;
            } else if (game.gameFinished && menuRect.contains(x, y)) {
                selectedButton = menuRect;
            } else {
                selectedButton = null;
                game.handleTouch(getFieldForCoord(x, y));
            }
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            int x = (int) event.getX(), y = (int) event.getY();
            if (!game.gameFinished && undoRect.contains(x, y) && selectedButton == undoRect) {
                game.undo();
            } else if (!game.gameFinished && redoRect.contains(x, y) && selectedButton == redoRect) {
                game.redo();
            } else if (!game.gameFinished && hintRect.contains(x, y) && selectedButton == hintRect) {
                game.hint();
            } else if (game.gameFinished && newGameRect.contains(x, y) && selectedButton == newGameRect) {
                game.startNewGame();
            } else if (game.gameFinished && menuRect.contains(x, y) && selectedButton == menuRect) {
                game.returnToMainMenu();
            }
            selectedButton = null;
            invalidate();
        } else {
            return super.onTouchEvent(event);
        }

        return true;
    }

    private Field getFieldForCoord(int eventX, int eventY) {
        int fieldX = (eventY - gameDimensions.getTopMargin()) / gameDimensions.getFieldWidth();
        int fieldY = (eventX - gameDimensions.getSideMargin()) / gameDimensions.getFieldWidth();

        if (BoardUtils.isFieldInBoard(fieldX, fieldY)) {
            return game.board.getFields()[fieldX][fieldY];
        } else {
            return null;
        }
    }

    public void reset() {
        dimensionsInitialized = false;
    }
}
