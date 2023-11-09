import javax.swing.*;
import java.awt.*;

public class GameSuite extends JFrame
{
    public GameSuite()
    {
        setTitle("TIC-TAC-TOE");
        setSize(600, 600);
        JPanel grid = new JPanel();
        add(grid);
        grid.setLayout(new GridLayout(2, 2));
        grid.setBackground(Color.BLACK);
        Font font = new Font("Courier", Font.BOLD, 46);

        JButton othelloButton = new JButton("Othello");
        othelloButton.setFont(font);
        othelloButton.setForeground(Color.GREEN);
        othelloButton.setBackground(Color.BLACK);
        othelloButton.setFocusable(false);
        othelloButton.addActionListener((e) -> new OthelloGUI());
        grid.add(othelloButton);

        JButton ticTacToeButton = new JButton("Tic-Tac-Toe");
        ticTacToeButton.setFont(font);
        ticTacToeButton.setForeground(Color.GREEN);
        ticTacToeButton.setBackground(Color.BLACK);
        ticTacToeButton.setFocusable(false);
        ticTacToeButton.addActionListener((e) -> new T3GUI());
        grid.add(ticTacToeButton);

        JButton wordleButton = new JButton("Wordle");
        wordleButton.setFont(font);
        wordleButton.setForeground(Color.GREEN);
        wordleButton.setBackground(Color.BLACK);
        wordleButton.setFocusable(false);
        wordleButton.addActionListener((e) -> new WordleGUI());
        grid.add(wordleButton);

        JButton pongButton = new JButton("Pong");
        pongButton.setFont(font);
        pongButton.setForeground(Color.GREEN);
        pongButton.setBackground(Color.BLACK);
        pongButton.setFocusable(false);
        pongButton.addActionListener((e) -> new Pong());
        grid.add(pongButton);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
