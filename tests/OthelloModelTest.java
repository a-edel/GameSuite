import static org.junit.jupiter.api.Assertions.*;

class OthelloModelTest
{
    OthelloModel game = new OthelloModel();

    @org.junit.jupiter.api.Test
    void getCellValue() {
        assertEquals(OthelloModel.CellValue.NONE, game.getCellValue(0, 0));
    }

    @org.junit.jupiter.api.Test
    void makeMoveAndReturnWinnerHuman() {
        game.makeMoveAndReturnWinnerHuman(5, 3);
        assertEquals(OthelloModel.CellValue.BLACK, game.getCellValue(5,3));
        game.makeMoveAndReturnWinnerHuman(5, 4);
        assertEquals(OthelloModel.CellValue.WHITE, game.getCellValue(5,4));
    }

    @org.junit.jupiter.api.Test
    void makeMoveAndReturnWinnerHumanException() {
        try
        {
            game.makeMoveAndReturnWinnerHuman(5, 3);
            game.makeMoveAndReturnWinnerHuman(5, 3);
        }
        catch(RuntimeException e)
        {
            return;
        }
        fail("Did not throw exception.");
    }

    @org.junit.jupiter.api.Test
    void winner() {
        assertEquals(OthelloModel.CellValue.NONE, game.winner());
    }

    @org.junit.jupiter.api.Test
    void isGameOver() {
        assertFalse(game.isGameOver());
    }

    @org.junit.jupiter.api.Test
    void newGame() {
        game.makeMoveAndReturnWinnerHuman(5, 3);
        game.newGame();
        assertEquals(OthelloModel.CellValue.NONE, game.makeMoveAndReturnWinnerHuman(5, 3));
    }

    @org.junit.jupiter.api.Test
    void currentPlayer() {
        assertEquals(OthelloModel.CellValue.BLACK, game.currentPlayer());
        game.makeMoveAndReturnWinnerHuman(5, 3);
        assertEquals(OthelloModel.CellValue.WHITE, game.currentPlayer());
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
        game.makeMoveAndReturnWinnerHuman(5, 3);
        game.writeObject();
        game.newGame();
        game.readObject();
        assertEquals(OthelloModel.CellValue.BLACK, game.getCellValue(5, 3));
    }
}