package byow.Core;

public class StringInput implements Input {
    private String input;
    private int index;

    public StringInput(String s) {
        index = 0;
        input = s;
    }

    @Override
    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return Character.toUpperCase(returnChar);
    }

    @Override
    public boolean possibleNextInput() {
        return index < input.length();
    }
}
