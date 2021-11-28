package killian.core;

public enum Campus {
    KKL (0),
    WST (1),
    DVL (2);

    private final int index;

    Campus(int i) {
        this.index = i;
    }

    public int getIndex() {
        return index;
    }
}
