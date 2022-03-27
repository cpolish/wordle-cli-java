package wordle;

enum LetterColour {
    GREY("\033[1;9m"),
    WHITE("\033[1m"),
    YELLOW("\033[1;93m"),
    GREEN("\033[1;92m");

    private final String ansiCode;

    private LetterColour(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    @Override
    public String toString() {
        return ansiCode;
    }
}
