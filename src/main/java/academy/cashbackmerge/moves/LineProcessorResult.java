package academy.cashbackmerge.moves;

import java.util.Arrays;

public final class LineProcessorResult {

    private final int[] newLine;
    private final int movePoints;

    public LineProcessorResult(int[] newLine, int movePoints) {
        this.newLine = Arrays.copyOf(newLine, newLine.length);
        this.movePoints = movePoints;
    }

    public int[] newLine() {
        return Arrays.copyOf(newLine, newLine.length);
    }

    public int movePoints() {
        return movePoints;
    }
}
