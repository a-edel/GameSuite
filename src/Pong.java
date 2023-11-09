import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static javax.swing.JOptionPane.*;

public class Pong extends JFrame
{
    private JLabel header = new JLabel();
    private JLabel status = new JLabel();
    private int score;
    private PongHighScoreComparator hsc = new PongHighScoreComparator();
    private PriorityQueue<PongScore> scores = new PriorityQueue<>(hsc);
    PongScore firstPlace;
    PongScore secondPlace;
    PongScore thirdPlace;
    Timer ballTimer;

    public Pong()
    {
        setTitle("PONG One Player");
        setSize(700, 750);
        setBackground(Color.WHITE);

        Font blippoBold = null;
        try {
            blippoBold = Font.createFont(Font.TRUETYPE_FONT, new File("Blippo Bold.ttf")).deriveFont(40f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(blippoBold);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        add(header, BorderLayout.NORTH);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(blippoBold);
        header.setOpaque(true);
        header.setBackground(Color.DARK_GRAY);


        status.setHorizontalAlignment(SwingConstants.CENTER);
        add(status, BorderLayout.SOUTH);
        status.setFont(blippoBold);
        status.setOpaque(true);
        status.setBackground(Color.DARK_GRAY);

        add(new GamePanel(), BorderLayout.CENTER);

        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                Properties props = new Properties();
                try {
                    props.load(new FileInputStream("Pong Leaderboard.ini"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                String firstPlacePoints = props.getProperty("first_place_points");
                String firstPlaceInitials = props.getProperty("first_place_initials");
                if (firstPlacePoints != null) {
                    firstPlace = new PongScore(parseInt(firstPlacePoints), firstPlaceInitials);
                    scores.add(firstPlace);
                }
                String secondPlacePoints = props.getProperty("second_place_points");
                String secondPlaceInitials = props.getProperty("second_place_initials");
                if (secondPlacePoints != null) {
                    PongScore secondPlace = new PongScore(parseInt(secondPlacePoints), secondPlaceInitials);
                    scores.add(secondPlace);
                }
                String thirdPlacePoints = props.getProperty("third_place_points");
                String thirdPlaceInitials = props.getProperty("third_place_initials");
                if (thirdPlacePoints != null) {
                    thirdPlace = new PongScore(parseInt(thirdPlacePoints), thirdPlaceInitials);
                    scores.add(thirdPlace);
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                ballTimer.stop();
                Properties props = new Properties();
                if (firstPlace != null) {
                    props.put("first_place_points", firstPlace.getPoints() + "");
                    props.put("first_place_initials", firstPlace.getInitials());
                }
                if (secondPlace != null) {
                    props.put("second_place_points", secondPlace.getPoints() + "");
                    props.put("second_place_initials", secondPlace.getInitials());
                }
                if (thirdPlace != null) {
                    props.put("third_place_points", thirdPlace.getPoints() + "");
                    props.put("third_place_initials", thirdPlace.getInitials());
                }
                try {
                    props.store(new FileOutputStream("Pong Leaderboard.ini"), "");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private class GamePanel extends JPanel
    {
        private Point ball;
        private Point delta;
        private Point paddle;
        private Point topWall = new Point (0, 0);
        private Point oppositeWall = new Point(677, 0);
        private Point bottomWall = new Point (0, 611);

        GamePanel()
        {
            setFocusable(true);
            requestFocus();
            setBackground(Color.BLACK);
            ballTimer = new Timer(10,
                    e -> {
                        ball.translate(delta.x, delta.y);
                        repaint();
                        if (ball.y < 10 || ball.y > 571) // top or bottom border
                        {
                            delta.y = -delta.y;
                        }
                        boolean intersect = ball.x == 40 && ((ball.y >= paddle.y && ball.y <= (paddle.y + 100)) || ((ball.y + 40) >= paddle.y && (ball.y + 40) <= (paddle.y + 100)));
                        if (intersect)
                        {
                            ++score;
                            header.setText(score + "");
                        }
                        if (intersect || ball.x > 637) // top or bottom border
                        {
                            delta.x = -delta.x;
                        }
                        if (ball.x == 0)
                        {
                            ballTimer.stop();
                            header.setForeground(Color.MAGENTA);
                            status.setBackground(Color.DARK_GRAY);
                            status.setForeground(Color.MAGENTA);
                            status.setText("G   A   M   E      O   V   E   R");
                            String winnerInitials;
                            do
                            {
                                winnerInitials = showInputDialog("Please enter your initials.");
                            }
                            while (winnerInitials == null);
                            scores.add(new PongScore(score, winnerInitials));
                            int reply = showConfirmDialog(null, "Play Again?", "", YES_NO_OPTION);
                            playAgain(reply);
                        }
                    });

            addMouseWheelListener( e -> {
                paddle.y += e.getPreciseWheelRotation() * 20;
                repaint();
            });
            addKeyListener(new KeyAdapter() {
               @Override
               public void keyPressed(KeyEvent e) {
                   int keyCode = e.getKeyCode();
                   if (keyCode == KeyEvent.VK_UP) {
                       paddle.y -= 15;
                   } else if (keyCode == KeyEvent.VK_DOWN) {
                       paddle.y += 15;
                   }
                   repaint();
               }
            });
            newGame();
        }

        public void playAgain(int reply)
        {
            if(reply == YES_OPTION)
            {
                newGame();
            }
            else if (reply == NO_OPTION)
            {
                StringBuilder winners = new StringBuilder("SCORE:                  Player:\n");
                firstPlace = scores.poll();
                secondPlace = scores.poll();
                thirdPlace = scores.poll();
                winners.append(firstPlace.getPoints()).append("                           ").append(firstPlace.getInitials()).append("\n");
                if (secondPlace != null)
                    winners.append(secondPlace.getPoints()).append("                           ").append(secondPlace.getInitials()).append("\n");
                if (thirdPlace != null)
                    winners.append(thirdPlace.getPoints()).append("                           ").append(thirdPlace.getInitials()).append("\n");
                winners.append("\n Play again?");
                showMessageDialog(null, winners, "Leaderboard", INFORMATION_MESSAGE);
                processWindowEvent(new WindowEvent((Window)getTopLevelAncestor(), WindowEvent.WINDOW_CLOSING));
            }
        }

        public void newGame()
        {
            ball = new Point(200, 100);
            delta = new Point(+5,-5);
            paddle = new Point(20, 270);
            score = 0;
            header.setText("0");
            header.setForeground(Color.CYAN);
            status.setForeground(Color.CYAN);
            status.setText("P     O     N     G");
            ballTimer.start();
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);
            g.setColor(Color.WHITE);
            g.fillOval(ball.x, ball.y, 40,40);
            g.fillRect(paddle.x, paddle.y, 20,100);
            g.fillRect(topWall.x, topWall.y, 680,10);
            g.fillRect(oppositeWall.x, oppositeWall.y, 10,621);
            g.fillRect(bottomWall.x, bottomWall.y, 680,10);
        }
    }
}