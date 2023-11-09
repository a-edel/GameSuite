import java.io.*;
import java.util.ArrayList;

public class OthelloModel implements Serializable
{
    public enum CellValue {BLACK, WHITE, NONE}
    private CellValue[][] board;
    private CellValue currentPlayer;
    private int turns;
    private boolean isWinner;
    private boolean computerOpponent;
    private final int NUM_ROWS = 8;
    private final int NUM_COLS = 8;


    public OthelloModel()
    {
        newGame();
    }
    public CellValue getCellValue(int row, int col) //added for testing purposes
    {
        return board[row][col];
    }

    public CellValue makeMoveAndReturnWinnerHuman(int row, int col)
    {
        if (!isValidMove(row, col))
            throw new IllegalArgumentException("Invalid move.");
        makeMove(row, col);
        switchPlayer();
        return winner();
    }

    private void switchPlayer()
    {
        currentPlayer = currentPlayer == CellValue.BLACK ? CellValue.WHITE : CellValue.BLACK;
    }

    public CellValue makeMoveAndReturnWinnerComputer()
    {

        ArrayList<int[]> possibleMoves = new ArrayList<>();

        // Iterate through all cells on the board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (isValidMove(row, col)) {
                    // Add the cell coordinates to the list of possible moves
                    possibleMoves.add(new int[]{row, col});
                }
            }
        }

        int bestRow = possibleMoves.get(0)[0];
        int bestCol = possibleMoves.get(0)[1];
        int maxFlipped = 0;

        // Make a copy of the current board for simulation
        writeObject();

        // Iterate through all possible moves
        for (int[] move : possibleMoves) {
            int row = move[0];
            int col = move[1];

            // Simulate the move by making a temporary move
            OthelloModel boardCopy = new OthelloModel();
            boardCopy.readObject();

            int flipped = 0;
            boardCopy.makeMove(row,col);
            for (int i = 0; i < NUM_ROWS; i++)
            {
                for (int j = 0; j < NUM_COLS; j++)
                {
                    if (getCellValue(i, j) != boardCopy.getCellValue(i, j))
                        ++flipped;
                }
            }
            // Update the best move if necessary
            if (flipped > maxFlipped) {
                maxFlipped = flipped;
                bestRow = row;
                bestCol = col;
            }
        }
        makeMove(bestRow, bestCol);
        CellValue winner = winner();
        switchPlayer();
        return winner;
    }

    private boolean isValidMove(int row, int col)
    {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) {
            return false;
        }

        // Check if the cell is empty
        if (board[row][col] != CellValue.NONE) {
            return false;
        }

        // Check if there is at least one opponent's piece adjacent to the cell
        boolean hasAdjacentOpponent = false;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                // Skip the current cell
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int newRow = row + dr;
                int newCol = col + dc;
                // Check if the new cell is within the board bounds
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 && board[newRow][newCol] != CellValue.NONE && board[newRow][newCol] != currentPlayer) {
                    hasAdjacentOpponent = true;
                    break;
                }
            }
            if (hasAdjacentOpponent) {
                break;
            }
        }

        // If there is no adjacent opponent's piece, it's not a legal move
        if (!hasAdjacentOpponent) {
            return false;
        }

        // Check if there is a straight line of opponent's pieces in any direction
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                // Skip the current cell
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int newRow = row + dr;
                int newCol = col + dc;
                int count = 0;
                // Check if the new cell is within the board bounds and contains opponent's piece
                while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 && board[newRow][newCol] != CellValue.NONE && board[newRow][newCol] != currentPlayer) {
                    newRow += dr;
                    newCol += dc;
                    count++;
                }
                // If the line ends with player's piece and has at least one opponent's piece in between, it's a legal move
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 && board[newRow][newCol] == currentPlayer && count > 0) {
                    return true;
                }
            }
        }

        return false; // If no straight line of opponent's pieces found, it's not a legal move
    }

    private void makeMove(int row, int col)
    {
        board[row][col] = currentPlayer;

        // Define the eight directions: N, S, E, W, NE, NW, SE, SW
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, 1 }, { 0, -1 }, { -1, 1 }, { -1, -1 }, { 1, 1 }, { 1, -1 } };

        // Iterate through each direction
        for (int[] dir : directions) {
            int dx = dir[0]; // X direction
            int dy = dir[1]; // Y direction
            int newRow = row + dx; // New row after moving in X direction
            int newCol = col + dy; // New column after moving in Y direction
            int count = 0; // Count of opponent's pieces to flip

            // Move in the current direction until the end of the board or an empty cell
            while (newRow >= 0 && newRow < NUM_ROWS && newCol >= 0 && newCol < NUM_COLS && board[newRow][newCol] != CellValue.NONE
                    && board[newRow][newCol] != currentPlayer) {
                newRow += dx;
                newCol += dy;
                count++;
            }

            // If there are opponent's pieces to flip and the next cell is the player's piece,
            // then flip the opponent's pieces in this direction
            if (count > 0 && newRow >= 0 && newRow < NUM_ROWS && newCol >= 0 && newCol < NUM_COLS
                    && board[newRow][newCol] == currentPlayer) {
                newRow = row + dx;
                newCol = col + dy;
                while (count > 0) {
                    board[newRow][newCol] = currentPlayer; // Flip the opponent's piece to player's piece
                    newRow += dx;
                    newCol += dy;
                    count--;
                }
            }
        }
        ++turns;
    }

    public CellValue winner()
    {
        if (!isGameOver())
        {
            return CellValue.NONE;
        }
        else
        {
            int blackTotal = 0;
            int whiteTotal = 0;
            for(int i = 0; i < NUM_ROWS; i++)
            {
                for (int j = 0; j < NUM_COLS; j++)
                {
                    if (board[i][j] == CellValue.BLACK)
                        ++blackTotal;
                    else if (board[i][j] == CellValue.WHITE)
                        ++whiteTotal;
                }
            }
            if (blackTotal > whiteTotal)
                return CellValue.BLACK;
            else if (whiteTotal > blackTotal)
                return CellValue.WHITE;
        }
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
        if (turns == NUM_ROWS * NUM_COLS)
            return true;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (board[i][j] == CellValue.NONE) {
                    if (isValidMove(i, j)) {
                        return false;
                    }
                }
            }
        }
        switchPlayer();
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (board[i][j] == CellValue.NONE) {
                    if (isValidMove(i, j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void newGame()
    {
        board = new CellValue[NUM_ROWS][NUM_COLS];
        for(int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_COLS; j++)
            {
                board[i][j] = CellValue.NONE;
            }
        }
        board[3][3] = CellValue.BLACK;
        board[3][4] = CellValue.WHITE;
        board[4][3] = CellValue.WHITE;
        board[4][4] = CellValue.BLACK;
        isWinner = false;
        currentPlayer = CellValue.BLACK;
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
            fos = new FileOutputStream("Othello Grid.bin");
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Othello Grid.bin"))) {
            OthelloModel savedGame = (OthelloModel) ois.readObject();
            board = savedGame.board;
            currentPlayer = savedGame.currentPlayer;
            turns = savedGame.turns;
            isWinner = savedGame.isWinner;
        }
        catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isComputerOpponent() {
        return computerOpponent;
    }

    public void setComputerOpponent(boolean computerOpponent) {
        this.computerOpponent = computerOpponent;
    }
}
