import java.io.*;
import java.util.*;

public class WordleModel implements Serializable
{
    private String target;
    private int numGuesses = 0;
    private boolean isWinner;
    private WordleLetterIndexAndResponse[][] board = new WordleLetterIndexAndResponse[6][5];
    private transient List<String> wordleWords;


    public WordleModel()
    {
        createWordleWordsList();
    }

    private void createWordleWordsList() 
    {
        File file = new File("valid-wordle-words.txt");
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        wordleWords = new ArrayList<>();
        while (sc.hasNextLine())
            wordleWords.add(sc.nextLine().toUpperCase());
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target.toUpperCase();
    }

    public void makeGuess(String guess)
    {
        guess = guess.toUpperCase();
        if (target == null)
        {
            generateTargetWord();
        }
        if (target.equals(guess))
        {
            isWinner = true;
        }
        List<Integer> availableTargetIndices = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        List<Integer> availableGuessIndices = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        List<WordleLetterIndexAndResponse> responses = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++)
        {
            char c = guess.charAt(i);
            if (c == target.charAt(i))
            {
                responses.add(new WordleLetterIndexAndResponse(c, i, WordleLetterIndexAndResponse.LetterResponse.CORRECT_LOCATION));
                availableTargetIndices.remove((Integer) i);
                availableGuessIndices.remove((Integer) i);
            }
        }
        for (ListIterator<Integer> guessLi = availableGuessIndices.listIterator(); guessLi.hasNext();)
        {
            int i = guessLi.next();
            char c = guess.charAt(i);
            for (ListIterator<Integer> targetLi = availableTargetIndices.listIterator(); targetLi.hasNext();)
            {
                int j = targetLi.next();
                if (c == target.charAt(j))
                {
                    responses.add(new WordleLetterIndexAndResponse(c, i, WordleLetterIndexAndResponse.LetterResponse.WRONG_LOCATION));
                    guessLi.remove();
                    targetLi.remove();
                    break;
                }
            }
        }
        for (int i : availableGuessIndices)
        {
            responses.add(new WordleLetterIndexAndResponse(guess.charAt(i), i, WordleLetterIndexAndResponse.LetterResponse.WRONG_LETTER));
        }
        responses.sort(Comparator.comparingInt(o -> o.getIndex()));
        for (int i = 0; i < responses.size(); i++)
        {
            board[numGuesses][i] = responses.get(i);
        }
        ++numGuesses;
    }

    private void generateTargetWord()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(wordleWords.size());
        setTarget(wordleWords.get(randomIndex));
    }

    public WordleLetterIndexAndResponse getWordleLetterIndexAndResponse(int row, int col) //added for testing purposes
    {
        return board[row][col];
    }

    public boolean isGameOver()
    {
        return numGuesses == 6 || isWinner;
    }

    public void newGame()
    {
        target = null;
        numGuesses = 0;
        isWinner = false;
        board = new WordleLetterIndexAndResponse[6][5];
    }

    public void writeObject()
    {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("Wordle Game.bin");
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject(this);
            oos.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void readObject()
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Wordle Game.bin"))) {
            WordleModel savedGame = (WordleModel) ois.readObject();
            target = savedGame.target;
            numGuesses = savedGame.numGuesses;
            isWinner = savedGame.isWinner;
            board = savedGame.board;
            createWordleWordsList();
        }
        catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumGuesses()
    {
        return numGuesses;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public boolean isValidWord(String s) {
        return wordleWords.contains(s);
    }
}
