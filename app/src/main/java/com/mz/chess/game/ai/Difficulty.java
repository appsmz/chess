package com.mz.chess.game.ai;

public enum Difficulty {
    DIFF0(1, 1, 0),
    DIFF1(1, 1, 0),
    DIFF2(1, 1, 0),
    DIFF3(1, 1, 0),
    DIFF4(1, 1, 0),
    DIFF5(1, 1, 50),
    DIFF6(3, 1, 50),
    DIFF7(5, 2, 90),
    DIFF8(8, 3, 120),
    DIFF9(11, 4, 150),
    DIFF10(13, 6, 200),
    DIFF11(15, 8, 250),
    DIFF12(18, 10, 300),
    DIFF13(20, 12, 350);

    private int skillLevel;
    private int depth;
    private int moveTime;

    Difficulty(int skillLevel, int depth, int moveTime) {
        this.skillLevel = skillLevel;
        this.depth = depth;
        this.moveTime = moveTime;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getDepth() {
        return depth;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public static Difficulty fromInt(int difficultyLevel) {
        for (int i = 0; i < values().length; i++) {
            if (difficultyLevel == i) {
                return values()[i];
            }
        }

        return DIFF2;
    }
}
