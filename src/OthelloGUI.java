import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OthelloGUI extends JFrame
{
    private OthelloModel game = new OthelloModel();
    private JCheckBox checkBox;
    private JPanel grid;
    private JLabel status;
    private ArrayList<Button> buttons;
    private final int NUM_ROWS = 8;
    private final int NUM_COLS = 8;

    OthelloGUI() {
        setTitle("OTHELLO");
        setSize(600, 650);

        JPanel north = new JPanel();
        add(north, BorderLayout.NORTH);
        north.setLayout(new GridLayout(1, 4));

        checkBox = new JCheckBox("Computer Opponent", true);
        ActionListener checkBoxAL = e -> {
            boolean computerOpponent = game.isComputerOpponent();
            game.setComputerOpponent(!computerOpponent);
        };
        checkBox.addActionListener(checkBoxAL);
        north.add(checkBox);

        JButton newGameButton = new JButton("New Game");
        ActionListener newGameButtonAL = new NewGameButtonEventHandler();
        newGameButton.addActionListener(newGameButtonAL);
        north.add(newGameButton);

        JButton saveButton = new JButton("Save");
        ActionListener saveButtonAL = new SaveButtonEventHandler();
        saveButton.addActionListener(saveButtonAL);
        north.add(saveButton);

        JButton restoreButton = new JButton("Restore");
        ActionListener restoreButtonAL = new RestoreButtonEventHandler();
        restoreButton.addActionListener(restoreButtonAL);
        north.add(restoreButton);

        grid = new JPanel();
        add(grid);
        grid.setBackground(Color.BLACK);
        grid.setLayout(new GridLayout(NUM_ROWS, NUM_COLS, 3, 3));

        status = new JLabel();
        add(status, BorderLayout.SOUTH);
        status.setFont(new Font("Courier", Font.BOLD, 16));
        status.setHorizontalAlignment(SwingConstants.CENTER);

        setupNewGame();
    }

    private void setupNewGame() {
        buttons = new ArrayList<>();
        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_COLS; j++)
            {
                Button button = new Button(i, j);
                grid.add(button);
                if ((i == 3 && j == 3 ) || (i == 4 && j == 4))
                {
                    button.setBackground(Color.BLACK);
                }
                else if ((i == 3 && j == 4) || (i == 4 && j == 3))
                {
                    button.setBackground(Color.WHITE);

                }
                else
                {
                    button.setBackground(Color.GREEN);
                    ActionListener gridAL = new GridEventHandler();
                    button.addActionListener(gridAL);
                }
                buttons.add(button);
            }
        }
        status.setText("BLACK's Turn");
        checkBox.setEnabled(true);
        setVisible(true);
    }

    class NewGameButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.isGameOver()) {
                restartGame();
            } else {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?",
                        "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    restartGame();
                }
            }
        }

        private void restartGame() {
            game.newGame();
            grid.removeAll();
            setupNewGame();
        }
    }

    class SaveButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.writeObject();
        }
    }

    class RestoreButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.readObject();
            fillBoard();
            OthelloModel.CellValue winner = game.winner();
            status.setText(getStatus(winner));
        }
    }

    class GridEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!game.isGameOver()) {
                try {
                    checkBox.setEnabled(false);
                    Button clickedButton = (Button) e.getSource();
                    OthelloModel.CellValue winner = game.makeMoveAndReturnWinnerHuman(clickedButton.getRow(), clickedButton.getCol());
                    fillBoard();
                    status.setText(getStatus(winner));
                    if (game.isGameOver())
                        checkBox.setEnabled(true);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Space. Please choose a different space.");
                    return;
                }
                if (!game.isGameOver() && checkBox.isSelected() && game.currentPlayer() == OthelloModel.CellValue.WHITE) {
                    OthelloModel.CellValue winner = game.makeMoveAndReturnWinnerComputer();
                    fillBoard();
                    status.setText(getStatus(winner));
                    if (game.isGameOver())
                        checkBox.setEnabled(true);
                }
            }
        }
    }

    private void fillBoard()
    {
        for (Button button : buttons)
        {
            OthelloModel.CellValue value = game.getCellValue(button.getRow(), button.getCol());
            if (value == OthelloModel.CellValue.BLACK)
                button.setBackground(Color.BLACK);
            else if (value == OthelloModel.CellValue.WHITE)
                button.setBackground(Color.WHITE);
        }
    }

    private String getStatus(OthelloModel.CellValue winner) {
        String status;
        if (winner != OthelloModel.CellValue.NONE) {
            status = winner + " wins. Please click the New Game button if you want to play again.";
        } else if (game.isGameOver()) {
            status = "No one wins. Please click the New Game button if you want to play again.";
        } else {
            status = game.currentPlayer() + "'s Turn";
        }
        return status;
    }

    private static class Button extends JButton {
        private int row;
        private int col;

        private Button(int row, int col) {
            this.row = row;
            this.col = col;
        }

        private int getRow() {
            return row;
        }

        private int getCol() {
            return col;
        }
    }
}
