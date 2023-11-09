import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class T3GUI extends JFrame {
    private T3Model game = new T3Model();
    private JCheckBox checkBox;
    private JPanel grid;
    private JLabel status;
    private ArrayList<Button> buttons;

    T3GUI() {
        setTitle("TIC-TAC-TOE");
        setSize(600, 620);

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
        grid.setLayout(new GridLayout(3, 3, 20, 20));
        grid.setBackground(Color.BLACK);

        status = new JLabel();
        add(status, BorderLayout.SOUTH);
        status.setFont(new Font("Courier", Font.BOLD, 16));

        setupNewGame();
        setVisible(true);
    }

    private void setupNewGame() {
        buttons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(i, j);
                grid.add(button);
                button.setFont(new Font("Courier", Font.BOLD, 190));
                button.setBackground(Color.WHITE);
                ActionListener gridAL = new GridEventHandler();
                button.addActionListener(gridAL);
                buttons.add(button);
            }
        }
        checkBox.setEnabled(true);
        status.setText("X's Turn");
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
            T3Model.CellValue value;
            for (Button b : buttons) {
                value = game.getCellValue(b.getRow(), b.getCol());
                if (value != null)
                    b.setText(String.valueOf(value));
            }
            T3Model.CellValue winner = game.winner();
            status.setText(getStatus(winner));
        }
    }

    class GridEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!game.isGameOver()) {
                try {
                    checkBox.setEnabled(false);
                    Button b = (Button) e.getSource();
                    int row = b.getRow();
                    int col = b.getCol();
                    T3Model.CellValue winner = game.makeMoveAndReturnWinnerHuman(row, col);
                    b.setText(String.valueOf(game.getCellValue(row, col)));
                    status.setText(getStatus(winner));
                    if (game.isGameOver())
                        checkBox.setEnabled(true);
                } catch (RuntimeException exception) {
                    JOptionPane.showMessageDialog(null, "Space Already Taken. Please choose a different space.");
                return;
                }
                if (!game.isGameOver() && checkBox.isSelected()) {
                    T3WinnerWithPosition winnerWithPosition = game.makeMoveAndReturnWinnerComputer();
                    int row = winnerWithPosition.getRow();
                    int col = winnerWithPosition.getCol();
                    for (Button b : buttons) {
                        if (row == b.getRow() && col == b.getCol()) {
                            b.setText(String.valueOf(game.getCellValue(row, col)));
                            break;
                        }
                    }
                    status.setText(getStatus(winnerWithPosition.getWinner()));
                    if (game.isGameOver())
                        checkBox.setEnabled(true);
                }
            }
        }
    }

    private String getStatus(T3Model.CellValue winner) {
        String status;
        boolean gameOver = game.isGameOver();
        if (winner != T3Model.CellValue.NONE) {
            status = winner + " wins. Please click the New Game button if you want to play again.";
        } else if (gameOver) {
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
