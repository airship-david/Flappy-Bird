package flappyBird;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.TexturePaint;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.*;
import java.awt.Cursor;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.util.concurrent.TimeUnit;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 1200, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird, scoreBox, messageBox, messageExtraBox, ground;

    public final BufferedImage birdImage = ImageIO.read(getClass().getResourceAsStream("images/bird.png"));

    public final BufferedImage birdDeadImage = ImageIO.read(getClass().getResourceAsStream("images/bird-dead.png"));

    public final BufferedImage groundImage = ImageIO.read(getClass().getResourceAsStream("images/ground.png"));

    public final BufferedImage pipeImage = ImageIO.read(getClass().getResourceAsStream("images/pipe.png"));

    public final BufferedImage pipeCapImage = ImageIO.read(getClass().getResourceAsStream("images/pipe-cap.png"));

    public Font font = new Font("Arial", Font.BOLD, 96);

    public Font smallFont = new Font("Arial", Font.PLAIN, 24);

    public Clip music = AudioSystem.getClip();

    public AudioInputStream musicStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/music.wav")));

    public Clip welcome = AudioSystem.getClip();

    public AudioInputStream welcomeStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/welcome.wav")));

    public Clip death = AudioSystem.getClip();

    public AudioInputStream deathStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/death.wav")));

    public int speed, ticks, yMotion, jump, score;

    public int highScore = 0;

    public boolean gameOver, started, initialized, gameOverComplete, paused;

    public ArrayList<Rectangle> columns;

    public Random rand;

    public FlappyBird() throws IOException, LineUnavailableException, UnsupportedAudioFileException {

        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();

        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("Flappy Bird");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 -20, HEIGHT / 2 -15, 40, 30);
        scoreBox = new Rectangle(0, 10, WIDTH, 96);
        messageBox = new Rectangle(0, HEIGHT / 2 - 126, WIDTH, 96);
        messageExtraBox = new Rectangle(0, HEIGHT / 2 + 60, WIDTH, 24);
        columns = new ArrayList<Rectangle>();
        ground = new Rectangle(0, HEIGHT -120, WIDTH, 120);

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();

    }

    public void start() throws IOException, LineUnavailableException {

        if (!started) {
            started = true;
            welcome.stop();
            welcome.close();
            welcomeStream.close();
            music.open(musicStream);
            music.loop(music.LOOP_CONTINUOUSLY);
            music.start();
        }

    }

    public void restart() throws IOException, LineUnavailableException, UnsupportedAudioFileException {

        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 -20, HEIGHT / 2 -15, 40, 30);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;

            death.stop();
            death.close();
            deathStream.close();
            music = AudioSystem.getClip();
            musicStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/music.wav")));
            music.open(musicStream);
            music.loop(music.LOOP_CONTINUOUSLY);
            music.start();

        }

    }

    public void jump() {

        if (!gameOver) {

            if (yMotion > 0) {
                yMotion = 0;
            }

            jump = 10;

            yMotion -= jump;

        }

    }

    public void actionPerformed(ActionEvent e) {

        speed = 10;

        // make speed faster depending on score
        if (score >= 20 && score < 40) {
            speed = 11;
        } else if (score >= 40 && score < 60) {
            speed = 12;
        } else if (score >= 60 && score < 80) {
            speed = 13;
        } else if (score >= 80 && score < 100) {
            speed = 14;
        } else if (score >= 100 && score < 120) {
            speed = 15;
        } else if (score >= 120 && score < 140) {
            speed = 16;
        } else if (score >= 140 && score < 160) {
            speed = 17;
        }  else if (score >= 160 && score < 180) {
            speed = 18;
        } else if (score >= 180 && score < 200) {
            speed = 19;
        } else if (score >= 200 && score < 220) {
            speed = 20;
        } else if (score >= 220 && score < 240) {
            speed = 21;
        } else if (score >= 240 && score < 260) {
            speed = 22;
        } else if (score >= 260 && score < 280) {
            speed = 23;
        } else if (score >= 280 && score < 300) {
            speed = 24;
        } else if (score >= 300) {
            speed = 25;
        }

        ticks++;

        if (!initialized) {
            renderer.repaint();
            try {
                welcome.open(welcomeStream);
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            welcome.loop(0);
            welcome.start();
            getHighScore();
            initialized = true;
        }

        // If Game Just Ended
        if (gameOver && !gameOverComplete) {
            renderer.repaint();
            music.stop();
            music.close();
            try {
                musicStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                death = AudioSystem.getClip();
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }
            try {
                deathStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("sounds/death.wav")));
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                death.open(deathStream);
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            death.loop(0);
            death.start();
            if (score > 0 && score > highScore) {
                playSound("sounds/high-score.wav", false);
            }
            saveScore();
            getHighScore();
            gameOverComplete = true;
        }

        if (started && !gameOver && !paused) {

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }


            // move the ground
            ground.x -= speed;
            ground.width += speed;

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++) {

                Rectangle column = columns.get(i);

                if (column.x + column.width < 0) {

                    columns.remove(column);

                    if (column.y == 0) {
                        addColumn(false);
                    }

                }

            }

            bird.y += yMotion;

            for (Rectangle column : columns) {

                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 && bird.x + bird.width / 2 < column.x + column.width / 2 + speed + .5) {
                    if (!gameOver) {
                        score++;
                        playSound("sounds/coin.wav", false);
                    }
                }

                if (column.intersects(bird)) {

                    gameOver = true;

                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    } else {
                        if (bird.y != 0) {
                            if (bird.y < column.height) {
                                bird.y = column.height;
                            } else {
                                bird.y = column.y - bird.height;
                            }
                        }
                    }

                }

            }

            if (bird.y > HEIGHT - 120 - bird.height / 2 || bird.y < 0) {

                gameOver = true;

            }

            if (bird.y + yMotion >= HEIGHT - 120) {

                bird.y = HEIGHT - 120 - bird.height;

            }

            if (gameOver) {

                gameOverComplete = false;

            }

        }

        renderer.repaint();

    }

    public void addColumn(boolean start) {

        int space = 300;
        int width = 100;
        int height = 5 + rand.nextInt(300);

        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            columns.add(new Rectangle(columns.get(columns.size() -1).x + 600, HEIGHT - height -120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() -1).x, 0, width, HEIGHT - height - space));
        }

    }

    public void paintColumn(Graphics2D g2, Rectangle column) {

        if (column.y != 0) {

            g2.setPaint(new TexturePaint(pipeCapImage, new Rectangle(column.x - 5, column.y, 110, 51)));
            g2.fillRect(column.x -5, column.y, column.width + 10, 51);

            g2.setPaint(new TexturePaint(pipeImage, new Rectangle(column.x, column.y + 56, 100, 49)));
            g2.fillRect(column.x,column.y + 51, column.width, column.height -51);

        } else {

            g2.setPaint(new TexturePaint(pipeCapImage, new Rectangle(column.x - 5, column.height + column.y - 51, 110, 51)));
            g2.fillRect(column.x -5, column.height + column.y - 51, column.width + 10, 51);

            g2.setPaint(new TexturePaint(pipeImage, new Rectangle(column.x, column.y, 100, 49)));
            g2.fillRect(column.x, column.y, column.width, column.height -51);

        }

    }

    public void playSound(String fileName, boolean continuous) {

        if (continuous) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Clip clip = AudioSystem.getClip();
                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(fileName)));
                        clip.open(inputStream);
                        clip.loop(clip.LOOP_CONTINUOUSLY);
                        clip.start();
                        inputStream.close();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Clip clip = AudioSystem.getClip();
                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(fileName)));
                        clip.open(inputStream);
                        clip.loop(0);
                        clip.start();
                        inputStream.close();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }).start();
        }

    }

    public void repaint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(WIDTH / 2,0, Color.CYAN.darker(),WIDTH / 2, HEIGHT - 120, Color.CYAN.brighter().brighter()));
        g2.fillRect(0,0, WIDTH, HEIGHT - 120);

        if (!gameOver) {
            g.drawImage(birdImage, bird.x, bird.y, 40, 30, null);
        } else {
            g.drawImage(birdDeadImage, bird.x, bird.y, 40, 30, null);
        }

        for (Rectangle column : columns) {
            paintColumn(g2, column);
        }

        g2.setPaint(new TexturePaint(groundImage, new Rectangle(ground.x, HEIGHT - 120, 258, 120)));
        g2.fillRect(ground.x, ground.y, ground.width, ground.height);

        g.setColor(Color.white);

        if (!started) {
            // Display High Score
            g.setFont(smallFont);
            g.drawString("High: "+String.valueOf(highScore), 10, 30);
            // Display Message
            drawCenteredString(g, "Press Enter To Start", messageBox, smallFont);
            drawCenteredString(g, "Click To Fly", messageExtraBox, smallFont);
        }

        if (gameOver) {
            // Display Score
            drawCenteredString(g, String.valueOf(score), scoreBox, font);
            // Display High Score
            g.setFont(smallFont);
            g.drawString("High: "+String.valueOf(highScore), 10, 30);
            // Display Message
            g.setColor(Color.red);
            drawCenteredString(g, "Game Over", messageBox, font);
            g.setColor(Color.white);
            drawCenteredString(g, "Press Enter To Restart", messageExtraBox, smallFont);

        }

        if (!gameOver && started) {
            // Display Score
            drawCenteredString(g, String.valueOf(score), scoreBox, font);
            // Display High Score
            g.setFont(smallFont);
            g.drawString("High: "+String.valueOf(highScore), 10, 30);
            if (paused) {
                drawCenteredString(g, "Paused", messageBox, smallFont);
            }
        }

    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public void saveScore() {

        File directory = new File(System.getProperty("user.home")+"/Library/Application Support/Flappy Bird");

        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            if (highScore == 0 || score > highScore) {
                BufferedWriter output = new BufferedWriter(new FileWriter(new File(System.getProperty("user.home")+"/Library/Application Support/Flappy Bird/highScore.txt"), true));
                output.newLine();
                output.append("" + score);
                output.close();
            }
        } catch (IOException ex1) {
            System.out.printf("ERROR writing score to file: %s\n", ex1);
        }


    }

    public void getHighScore() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.home")+"/Library/Application Support/Flappy Bird/highScore.txt"));
            String line = reader.readLine();
            while (line != null) // read the score file line by line
            {
                try {
                    int thescore = Integer.parseInt(line.trim());
                    if (thescore > highScore)
                    {
                        highScore = thescore;
                    }
                } catch (NumberFormatException e1) {
                    // ignore invalid scores
                    //System.err.println("ignoring invalid score: " + line);
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException ex) {
            System.err.println("ERROR reading scores from file");
        }

    }

    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {

        flappyBird = new FlappyBird();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (started && !gameOver && !paused) {
            jump();
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!started && !gameOver) {
                try {
                    start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (LineUnavailableException e1) {
                    e1.printStackTrace();
                }
            } else if (started && gameOver) {
                try {
                    restart();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (LineUnavailableException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedAudioFileException e1) {
                    e1.printStackTrace();
                }
            } else if (started && !gameOver) {
                if (paused == false) {
                    paused = true;
                    music.stop();
                    renderer.repaint();
                    playSound("sounds/pause.wav", false);
                } else {
                    paused = false;
                    music.start();
                    renderer.repaint();
                    playSound("sounds/back.wav", false);
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (started && !gameOver && !paused) {
                jump();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
