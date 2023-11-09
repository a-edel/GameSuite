import java.io.Serializable;

public class WordleLetterIndexAndResponse implements Serializable {
    public enum LetterResponse {CORRECT_LOCATION, WRONG_LOCATION, WRONG_LETTER }
    private char c;
    private int index;
    private LetterResponse response;

    public WordleLetterIndexAndResponse(char c, int index, LetterResponse response)
    {
        this.c = c;
        this.index = index;
        this.response = response;
    }

    public char getC() {
        return c;
    }

    public int getIndex() {
        return index;
    }

    public LetterResponse getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "WordleResponse {" +
                "c = " + "'" + c + "'" +
                ", index = " + index +
                ", LetterResponse = " + response +
                '}';
    }
}
