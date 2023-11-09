import java.io.Serializable;

public class T3WinnerWithPosition implements Serializable
{
    private T3Model.CellValue winner;
    private int row;
    private int col;

    public T3WinnerWithPosition (T3Model.CellValue winner, int row, int col)
    {
        this.winner = winner;
        this.row = row;
        this.col = col;
    }

    public T3Model.CellValue getWinner() {
        return winner;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
