import static org.junit.jupiter.api.Assertions.*;

class T3ModelTest
{
    T3Model game = new T3Model();

    @org.junit.jupiter.api.Test
    void getCellValue() {
        assertNull(game.getCellValue(0, 0));
    }

    @org.junit.jupiter.api.Test
    void makeMoveAndReturnWinnerHuman() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        assertEquals(T3Model.CellValue.X, game.getCellValue(0,0));
        game.makeMoveAndReturnWinnerHuman(0, 1);
        assertEquals(T3Model.CellValue.O, game.getCellValue(0,1));
    }

    @org.junit.jupiter.api.Test
    void makeMoveAndReturnWinnerHumanException() {
        try
        {
            game.makeMoveAndReturnWinnerHuman(0, 0);
            game.makeMoveAndReturnWinnerHuman(0, 0);
        }
        catch(RuntimeException e)
        {
            return;
        }
        fail("Did not throw exception.");
    }

    @org.junit.jupiter.api.Test
    void winnerHorizontal() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        assertEquals(T3Model.CellValue.X, game.makeMoveAndReturnWinnerHuman(0, 2));
    }

    @org.junit.jupiter.api.Test
    void winnerVertical() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        assertEquals(T3Model.CellValue.X, game.makeMoveAndReturnWinnerHuman(2, 0));
    }

    @org.junit.jupiter.api.Test
    void winnerDiagonal1() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        assertEquals(T3Model.CellValue.X, game.makeMoveAndReturnWinnerHuman(2, 2));
    }

    @org.junit.jupiter.api.Test
    void winnerDiagonal2() {
        game.makeMoveAndReturnWinnerHuman(0, 2);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        assertEquals(T3Model.CellValue.X, game.makeMoveAndReturnWinnerHuman(2, 0));
    }

    @org.junit.jupiter.api.Test
    void winnerNone() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        game.makeMoveAndReturnWinnerHuman(1, 2);
        game.makeMoveAndReturnWinnerHuman(0, 2);
        game.makeMoveAndReturnWinnerHuman(2, 0);
        game.makeMoveAndReturnWinnerHuman(2, 1);
        assertEquals(T3Model.CellValue.NONE, game.makeMoveAndReturnWinnerHuman(2, 2));
    }

    @org.junit.jupiter.api.Test
    void isGameOverWinner() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        assertFalse(game.isGameOver());
        game.makeMoveAndReturnWinnerHuman(0, 2);
        assertTrue(game.isGameOver());
    }

    @org.junit.jupiter.api.Test
    void isGameOverFullBoard() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.makeMoveAndReturnWinnerHuman(0, 1);
        game.makeMoveAndReturnWinnerHuman(1, 1);
        game.makeMoveAndReturnWinnerHuman(1, 2);
        game.makeMoveAndReturnWinnerHuman(0, 2);
        game.makeMoveAndReturnWinnerHuman(2, 0);
        game.makeMoveAndReturnWinnerHuman(2, 1);
        assertFalse(game.isGameOver());
        game.makeMoveAndReturnWinnerHuman(2, 2);
        assertTrue(game.isGameOver());
    }

    @org.junit.jupiter.api.Test
    void newGame() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.makeMoveAndReturnWinnerHuman(1, 0);
        game.newGame();
        assertNull(game.getCellValue(0, 0));
        assertNull(game.getCellValue(0, 1));
    }

    @org.junit.jupiter.api.Test
    void currentPlayer() {
        assertEquals(T3Model.CellValue.X, game.currentPlayer());
        game.makeMoveAndReturnWinnerHuman(0, 0);
        assertEquals(T3Model.CellValue.O, game.currentPlayer());
    }
    @org.junit.jupiter.api.Test
    void makeMoveAndReturnWinnerComputer() {
        game.makeMoveAndReturnWinnerComputer();
        boolean moveMade = false;
        for(int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                if (game.getCellValue(i, j) != null)
                    moveMade = true;
            }
        }
        assertTrue(moveMade);
    }

    @org.junit.jupiter.api.Test
    void saveAndRestore() {
        game.makeMoveAndReturnWinnerHuman(0, 0);
        game.writeObject();
        game.newGame();
        game.readObject();
        assertEquals(T3Model.CellValue.X, game.getCellValue(0, 0));
    }
}