package academy.cashbackmerge.moves;

public enum Move {
    DOWN,
    UP,
    LEFT,
    RIGHT;

    private static final Move[] MOVES = Move.values();

    public static Move getByOrdinal(int index) {
        return MOVES[index];
    }

    public static int getSize() {
        return MOVES.length;
    }
}
