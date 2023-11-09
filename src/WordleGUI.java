import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class WordleGUI extends JFrame
{
    private WordleModel game = new WordleModel();
    private JPanel grid;
    private JButton enterButton;
    private ArrayList<LetterBox> letterBoxes;
    JButton inputCustomWordButton;
    JButton newGameButton;

    WordleGUI()
    {
        setTitle("WORDLE");
        setSize(600, 650);

        JPanel north = new JPanel();
        add(north, BorderLayout.NORTH);
        north.setLayout(new GridLayout(1, 4));

        inputCustomWordButton = new JButton("Input Custom Word");
        ActionListener inputCustomWordButtonAL = new InputCustomWordButtonEventHandler();
        inputCustomWordButton.addActionListener(inputCustomWordButtonAL);
        north.add(inputCustomWordButton);

        newGameButton = new JButton("New Game");
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
        grid.setLayout(new GridLayout(6, 5, 3, 3));

        enterButton = new JButton("ENTER");
        ActionListener enterButtonAL = new EnterButtonEventHandler();
        enterButton.addActionListener(enterButtonAL);
        add(enterButton, BorderLayout.SOUTH);
        enterButton.setFont(new Font("Courier", Font.BOLD, 16));
        enterButton.setHorizontalAlignment(SwingConstants.CENTER);

        setupNewGame();
    }

    private void setupNewGame() {
        letterBoxes = new ArrayList<>();
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                LetterBox letterBox = new LetterBox(i, j);
                letterBox.addKeyListener(new LetterBoxTypingEventHandler());
                letterBoxes.add(letterBox);
                grid.add(letterBox);
            }
        }
        enterButton.setText("ENTER");
        inputCustomWordButton.setEnabled(true);
        setVisible(true);
        letterBoxes.get(0).requestFocusInWindow();
    }

    class EnterButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sb = new StringBuilder();
            int guessNumber = game.getNumGuesses();
            ArrayList<LetterBox> currentGuessLetterBoxes = new ArrayList<>();
            for(LetterBox lb : letterBoxes)
            {
                int row = lb.getRow();
                if(row == guessNumber)
                {
                    sb.append(lb.getText());
                    currentGuessLetterBoxes.add(lb);
                }
            }
            if (sb.length() == 5)
            {
                String sbAsString = String.valueOf(sb);
                if (!game.isValidWord(sbAsString)) {
                    JOptionPane.showMessageDialog(null, "Not in word list");
                }
                else
                {
                    game.makeGuess(sbAsString);
                    colorLetterBoxBackground(currentGuessLetterBoxes);
                    for (LetterBox lb : letterBoxes)
                    {
                        if (lb.getRow() == game.getNumGuesses() - 1)
                        {
                            lb.setEnabled(false);
                        }
                    }
                    setEnterButtonText();
                }
            }
        }
    }

    private void setEnterButtonText() {
        int numGuesses = game.getNumGuesses();
        if (game.isWinner())
        {
            enterButton.setText("YOU WON");
            newGameButton.requestFocusInWindow();
        }
        else if (numGuesses == 6)
        {
            enterButton.setText("The Word Was " + game.getTarget());
            newGameButton.requestFocusInWindow();
        }
        else
        {
            letterBoxes.get(numGuesses * 5).requestFocusInWindow();
        }
    }

    private void colorLetterBoxBackground(List<LetterBox> letterBoxList)
    {
        WordleLetterIndexAndResponse wordleLetterIndexAndResponse;
        for(LetterBox lb : letterBoxList)
        {
            wordleLetterIndexAndResponse = game.getWordleLetterIndexAndResponse(lb.getRow(), lb.getCol());
            if (wordleLetterIndexAndResponse != null)
            {
                inputCustomWordButton.setEnabled(false);
                lb.setEnabled(false);
                WordleLetterIndexAndResponse.LetterResponse lr = wordleLetterIndexAndResponse.getResponse();
                if (lr == WordleLetterIndexAndResponse.LetterResponse.CORRECT_LOCATION)
                    lb.setBackground(Color.decode("#6ca965"));
                else if (lr == WordleLetterIndexAndResponse.LetterResponse.WRONG_LOCATION)
                    lb.setBackground(Color.decode("#c8b653"));
                else if (lr == WordleLetterIndexAndResponse.LetterResponse.WRONG_LETTER)
                    lb.setBackground(Color.decode("#787c7f"));
            }
        }
    }

    class InputCustomWordButtonEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String target = JOptionPane.showInputDialog("Enter target word for player to figure out:");
            if (target != null && !target.isEmpty())
            {
                game.setTarget(target);
                letterBoxes.get(0).requestFocusInWindow();
            }
        }
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
            setEnterButtonText();
        }
    }

    class LetterBoxTypingEventHandler extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            inputCustomWordButton.setEnabled(false);
            char typedChar = e.getKeyChar();
            LetterBox currentLetterBox = (LetterBox) e.getSource();
            int row = currentLetterBox.getRow();
            int col = currentLetterBox.getCol();
            int currentLetterBoxesIndex = row * 5 + col;
            int previousIndex = currentLetterBoxesIndex - 1;
            if (typedChar == '\n')
            {
                enterButton.doClick();
            }
            else if (typedChar == '\b' && currentLetterBoxesIndex > game.getNumGuesses() * 5)
            {
                LetterBox previousLetterBox = letterBoxes.get(previousIndex);
                if (currentLetterBox.getText().length() == 0)
                {
                    SwingUtilities.invokeLater(() -> previousLetterBox.setText(""));
                    previousLetterBox.requestFocusInWindow();
                }
                else
                {
                    SwingUtilities.invokeLater(() -> currentLetterBox.setText(""));
                }
            }
            else if (Character.isLetter(typedChar) && row == game.getNumGuesses())
            {
                SwingUtilities.invokeLater(() -> currentLetterBox.setText(String.valueOf(typedChar).toUpperCase()));
                if ((currentLetterBoxesIndex + 1) % 5 != 0)
                {
                    letterBoxes.get(currentLetterBoxesIndex + 1).requestFocusInWindow();
                }
            }
            else
            {
                SwingUtilities.invokeLater(() -> currentLetterBox.setText(""));
            }
        }
    }

    private void fillBoard()
    {
        for (LetterBox lb : letterBoxes)
        {
            WordleLetterIndexAndResponse wordleLetterIndexAndResponse = game.getWordleLetterIndexAndResponse(lb.getRow(),lb.getCol());
            if (wordleLetterIndexAndResponse != null)
                lb.setText(String.valueOf(wordleLetterIndexAndResponse.getC()));
        }
        colorLetterBoxBackground(letterBoxes);
    }

    private static class LetterBox extends JTextField {
        private int row;
        private int col;

        private LetterBox(int row, int col)
        {
            this.row = row;
            this.col = col;
            setFont(new Font("Courier", Font.BOLD, 40));
            setHorizontalAlignment(JTextField.CENTER);
            setDisabledTextColor(Color.WHITE);
            Action beep = getActionMap().get(DefaultEditorKit.deletePrevCharAction);
            beep.setEnabled(false);
        }

        private int getRow()
        {
            return row;
        }

        private int getCol()
        {
            return col;
        }

        public boolean isFocusable()
        {
            return true;
        }
    }
}
