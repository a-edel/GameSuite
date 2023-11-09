import java.io.*;
import java.util.Random;

public class T3Model implements Serializable
{
    public enum CellValue {X, O, NONE}
    private CellValue[][] board = new CellValue[3][3];
    private CellValue currentPlayer = CellValue.X;
    private int turns;
    private boolean isWinner;
    private boolean isComputerOpponent;

    public CellValue getCellValue(int row, int col) //added for testing purposes
    {
        return board[row][col];
    }

    public CellValue makeMoveAndReturnWinnerHuman(int row, int col)
    {
        if(board[row][col] != null)
            throw new RuntimeException("Spot Already Taken");
        board[row][col] = currentPlayer;
        ++turns;
        CellValue winner = winner();
        currentPlayer = currentPlayer == CellValue.X ? CellValue.O : CellValue.X;
        return winner;
    }

    public T3WinnerWithPosition makeMoveAndReturnWinnerComputer()
    {
        Random r = new Random();
        int row;
        int col;
        do
        {
            row = r.nextInt(0, 3);
            col = r.nextInt(0, 3);
        }
        while(getCellValue(row, col) != null);
        CellValue winner = makeMoveAndReturnWinnerHuman(row, col);
        return new T3WinnerWithPosition(winner, row, col);
    }

    public CellValue winner()
    {
        for (int i = 0; i < 3; i++)
        {
            CellValue firstCol = board[i][0];
            if(firstCol != null && firstCol == board[i][1] && firstCol == board[i][2])
            {
                isWinner = true;
                return currentPlayer;
            }
        }
        for (int i = 0; i < 3; i++)
        {
            CellValue firstRow = board[0][i];
            if(firstRow != null && firstRow == board[1][i] && firstRow == board[2][i])
            {
                isWinner = true;
                return currentPlayer;
            }
        }
        CellValue upperLeftCorner = board[0][0];
        if (upperLeftCorner != null && upperLeftCorner == board[1][1] && upperLeftCorner == board[2][2])
        {
            isWinner = true;
            return currentPlayer;
        }
        CellValue upperRightCorner = board[0][2];
        if (upperRightCorner != null && upperRightCorner == board[1][1] && upperRightCorner == board[2][0])
        {
            isWinner = true;
            return currentPlayer;
        }
        return CellValue.NONE;
    }

    public boolean isGameOver()
    {
        return isWinner || turns == 9;
    }

    public void newGame()
    {
        board = new CellValue[3][3];
        isWinner = false;
        currentPlayer = CellValue.X;
        turns = 0;
    }

    public CellValue currentPlayer()
    {
        return currentPlayer;
    }

    public void writeObject()
    {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("T3 Grid.bin");
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("T3 Grid.bin"))) {
            T3Model savedGame = (T3Model) ois.readObject();
            board = savedGame.board;
            currentPlayer = savedGame.currentPlayer;
            turns = savedGame.turns;
            isWinner = savedGame.isWinner;
            isComputerOpponent = savedGame.isComputerOpponent;
        }
        catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isComputerOpponent() {
        return isComputerOpponent;
    }

    public void setComputerOpponent(boolean computerOpponent) {
        this.isComputerOpponent = computerOpponent;
    }
}
