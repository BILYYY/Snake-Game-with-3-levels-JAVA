import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.awt.Color;
public class GamePanel extends JPanel implements ActionListener {

    //Defining some constants for screen size, unit size, game units and delay
    static final int SCREEN_WIDTH =650;
    static final int SCREEN_HEIGHT =650;
    static final int UNIT_SIZE=25;
    static final int GAME_UNITS= (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100;

    //Declaring variables to hold the x and y coordinates of the snake, as well as some other properties of the game
    final int x[]=new int[GAME_UNITS];
    final int y[]=new int[GAME_UNITS];
    int bodyParts = 5;
    int applesEaten;
    int appleX;
    int appleY;
    char direction='R';
    boolean running=false;
    Timer timer;
    Random random = new Random();

    private static final int EASY_SPEED = 100;
    private static final int MEDIUM_SPEED = 75;
    private static final int HARD_SPEED = 50;
    private int currentLevel = 1;

    //Creating the constructor for the GamePanel class
    GamePanel(){
        Random random ;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        //Adding a key listener to the panel
        this.addKeyListener(new MyKeAdapter());
        startGame();
    }

    //Method to start the game
    public void startGame(){
        newFood();
        running = true;
        //Creating a new timer with a delay of DELAY and adding an ActionListener to it
        timer = new Timer(getDelayForLevel(currentLevel), this);
        timer.start();
    }
    private int getDelayForLevel(int level) {
        switch(level) {
            case 1:
                return EASY_SPEED;
            case 2:
                return MEDIUM_SPEED;
            case 3:
                return HARD_SPEED;
            default:
                return MEDIUM_SPEED;
        }
    }
    //Method to paint the components of the panel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    //Method to draw the components of the panel
    public void draw(Graphics g){

        if(running){
           /* for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++){
                g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE,SCREEN_HEIGHT);
                g.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE);
            }*/
            //paint apple
            g.setColor(Color.RED);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
            //Painting the body parts of the snake
            for(int i=0; i<bodyParts ;i++){
                //paint head
                if(i==0){
                    g.setColor(new Color(2, 255, 209));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
                //paint body
                else {
                    //g.setColor(new Color(0, 176, 132));
                    //Painting the snake in a random color
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            //Painting the score on the screen
            g.setColor(new Color(4, 70, 133));
            g.setFont( new Font("Ink Free",Font.BOLD, 20));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        }
        else{
            // If the game is not running, show the game over screen
            gameOver(g);
        }
    }
    // Define a method to place new food objects
    public void newFood(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        // Shift the body parts of the snake forward
        for(int i= bodyParts;i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        // Move the head of the snake in the desired direction
        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            default:
                break;
        }
    }
    public void checkFood(){
        // Move the head of the snake in the desired direction
        if((x[0]==appleX&& y[0]==appleY)){
            // Increase the number of body parts and apples eaten
            bodyParts++;
            applesEaten++;
            // Spawn a new apple
            newFood();
        }
    }
    public void checkCollision(){
        //check for collision with body parts
        for(int i=bodyParts; i>0;i--){
            if((x[0]==x[i])&&(y[0]==y[i])){
                running=false;
            }
        }
        //check if head touches left border
        if(x[0]<0){
            running=false;
        }
        //check if head touches right border
        if(x[0]>SCREEN_WIDTH){
            running=false;
        }
        //if head touches bottom border
        if(y[0]>SCREEN_HEIGHT){
            running=false;
        }
        //if head touches top border
        if(y[0]<0){
            running=false;
        }
        //check for speed change
        switch (applesEaten) {
            case 10:
                currentLevel = 2;
                break;
            case 25:
                currentLevel = 3;
                break;
            default:
                break;
        }
        timer.setDelay(getDelayForLevel(currentLevel));

    }
    public void gameOver(Graphics g){
        // Draw the score on the screen
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // If the game is still running, update the game state
        if(running){
            move();
            checkFood();
            checkCollision();
        }
        // Repaint the game screen
        repaint();
    }
    public class MyKeAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Update the direction of the snake based on the pressed key
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                  if(direction !='D'){
                      direction='U';
                  }
                  break;
                case KeyEvent.VK_DOWN:
                    if(direction!='U'){
                        direction='D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if(direction!='R'){
                        direction='L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction!='L'){
                        direction='R';
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
