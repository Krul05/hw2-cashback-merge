package academy.cashbackmerge.moves;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LineProcessor {
    public LineProcessorResult processLine(int[] line) {
        int[] newLine = new int[line.length];
        int movePoints = 0;
        int index = 0;
        boolean wasMerged = false;

        for (int cell : line) {
            if (cell != 0) {
                if (index > 0 && newLine[index - 1] == cell && !wasMerged) {
                    newLine[index - 1] *= 2;
                    movePoints += newLine[index - 1];
                    wasMerged = true;
                } else {
                    newLine[index++] = cell;
                    wasMerged = false;
                }
            }
        }

        return new LineProcessorResult(newLine, movePoints);
    }
}
