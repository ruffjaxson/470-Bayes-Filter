
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.text.Utilities;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] previousProbs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        previousProbs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    public static final int OPEN = 0;
    public static final int WALL = 1;
    public static final int LAVA = 2;
    public static final int GOAL = 3;
    double discountFactor = 1.0;

    Color bkgroundColor = new Color(230,230,230);
    double[] validDirections = new double[4];
    int[] sureValidDirections = new int[4];
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int currentX = -1, currentY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    double[][] previousProbs;
    double[][] spilledProbs;
    double[][] rewards;
    double[][] utilities;
    double[][] prevUtilities;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;

    public void copyProbs() {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                previousProbs[x][y] = probs[x][y];
            }
        }
    }
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                currentX = startX;
                currentY = startY;
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        // System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        // System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        previousProbs = new double[mundo.width][mundo.height];
        spilledProbs = new double[mundo.width][mundo.height];
        rewards = new double[mundo.width][mundo.height];
        utilities = new double[mundo.width][mundo.height];
        prevUtilities = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int numberOfAvailableSpaces = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        numberOfAvailableSpaces++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / numberOfAvailableSpaces;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }

    // void printWorldGrid() {
    //     System.out.println();
    //     for (int y = 0; y < mundo.height; y++) {
    //         for (int x = 0; x < mundo.width; x++) {
    //             System.out.print(mundo.grid[x][y]);
    //         }
    //         System.out.println();
    //     }
    //     System.out.println();
    // }

    void printGeneral(double general[][]) {
        System.out.println();
        int numSpaces = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                double valToPrint = general[x][y] < 5.0 ? general[x][y] : Math.round(general[x][y] * 10.0) / 10.0;
                System.out.print(valToPrint);
                if (general[x][y] == 0.0) {
                    numSpaces = 5;
                } else if (general[x][y] < -99.999) {
                    numSpaces = 2;
                } else if (general[x][y] < -9.999) {
                    numSpaces = 3;
                } else if (general[x][y] < 0.0) {
                    numSpaces = 4;
                } else if (general[x][y] < 10.0) {
                    numSpaces = 5;
                } else if (general[x][y] < 100.0) {
                    numSpaces = 4;
                } else {
                    numSpaces = 3;
                }
                
                for (int i = 0; i < numSpaces; i++) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    double getSensorModelProbability(int up, int down, int right, int left, int x, int y) {

        double probability = 1.0;
        if (up == mundo.grid[x][y-1]) {
            probability *= sensorAccuracy;
        } else {
            probability *= (1 - sensorAccuracy);
        }
        if (down == mundo.grid[x][y+1]) {
            probability *= sensorAccuracy;
        } else {
            probability *= (1 - sensorAccuracy);
        }
        if (right == mundo.grid[x+1][y]) {
            probability *= sensorAccuracy;
        } else {
            probability *= (1 - sensorAccuracy);
        }
        if (left == mundo.grid[x-1][y]) {
            probability *= sensorAccuracy;
        } else {
            probability *= (1 - sensorAccuracy);
        }

        // System.out.println("Got " + probability + " for sensor prob");
        return probability;
    }

    int getNumberOfActionsThatKeepUsInTheSameState(int x, int y, int action) {
        // TODO: double check this math
        int count = 1;
        if (action != 0 && mundo.grid[x][y-1] == 1) {
            count++;
        } 
        if (action != 1 && mundo.grid[x][y+1] == 1) {
            count++;
        } 
        if (action != 2 && mundo.grid[x+1][y] == 1) {
            count++;
        } 
        if (action != 3 && mundo.grid[x-1][y] == 1) {
            count++;
        } 
        return count;
    }

    int getValidDirections(int x, int y) {
        int count = 1;
        if (mundo.grid[x][y-1] != WALL) { // up
            validDirections[0] = previousProbs[x][y-1];
            sureValidDirections[0] = 1;
            count++;
        } else {
            validDirections[0] = 0;
            sureValidDirections[0] = 0;
        }
        if (mundo.grid[x][y+1] != WALL) { // down
            validDirections[1] = previousProbs[x][y+1];
            sureValidDirections[1] = 1;
            count++;
        } else {
            validDirections[1] = 0;
            sureValidDirections[1] = 0;
        }
        if (mundo.grid[x+1][y] != WALL) { // right
            validDirections[2] = previousProbs[x+1][y];
            sureValidDirections[2] = 1;
            count++;
        } else {
            validDirections[2] = 0;
            sureValidDirections[2] = 0;
        }
        if (mundo.grid[x-1][y] != WALL) { // left
            validDirections[3] = previousProbs[x-1][y];
            sureValidDirections[3] = 1;
            count++;
        } else {
            validDirections[3] = 0;
            sureValidDirections[3] = 0;
        }
        return count;
    }

    double getTransitionModelProbability(int up, int down, int right, int left, int x, int y, int actionTaken) {
        int numberOfValidDirections = getValidDirections(x, y);

        double probabilityOfSupposedPreviousMove = 0.0;
        double probability = 0.0;
        switch (actionTaken) {
            case 0: // up
                probabilityOfSupposedPreviousMove = previousProbs[x][y+1]; // look down
                break;
            case 1: // down
                probabilityOfSupposedPreviousMove = previousProbs[x][y-1]; // look up
                break;
            case 2: // right
                probabilityOfSupposedPreviousMove = previousProbs[x-1][y]; // look left
                break;
            case 3: // left
                probabilityOfSupposedPreviousMove = previousProbs[x+1][y]; // look right
                break;
        }
        probability += moveProb * probabilityOfSupposedPreviousMove; 
        double multiplier = (1 - moveProb) / numberOfValidDirections;



        for (int i = 0; i < 4 && i != actionTaken; i++) {
            if (validDirections[i] > 0.0) {
                probability += multiplier * validDirections[i];
            }
        }
        probability += multiplier * previousProbs[x][y];

        // if (probabilityOfSupposedPreviousMove > 0.0) { // supposed previous square was valid 
        //     probability = moveProb * probabilityOfSupposedPreviousMove;
        // } else {
        //     int numberOfMovesThatKeepUsInThisState = getNumberOfActionsThatKeepUsInTheSameState(x, y, actionTaken);
        //     probability = (1 - moveProb) * (numberOfMovesThatKeepUsInThisState / 4); // move was unsuccessful
        // }
        return probability;
    }

    void normalizeProbs() {
        double sum = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    sum += probs[x][y];
                }
            }
        }

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    probs[x][y] = probs[x][y] / sum;
                }
            }
        }

    }
    
    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        copyProbs();
        int isWallDetectedUp = sonars.charAt(0) == '1' ? 1 : 0;
        int isWallDetectedDown = sonars.charAt(1) == '1' ? 1 : 0;
        int isWallDetectedRight = sonars.charAt(2) == '1' ? 1 : 0;
        int isWallDetectedLeft = sonars.charAt(3) == '1' ? 1 : 0;
        double sensorProbability;
        double transitionProbability;

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    sensorProbability = getSensorModelProbability(isWallDetectedUp, isWallDetectedDown, isWallDetectedRight, isWallDetectedLeft, x, y);
                    transitionProbability = getTransitionModelProbability(isWallDetectedUp, isWallDetectedDown, isWallDetectedRight, isWallDetectedLeft, x, y, action);

                    probs[x][y] = sensorProbability * transitionProbability;
                } else {
                    probs[x][y] = 0;
                }
            }
            // System.out.println();
        }


        // System.out.println("probs before normalizing:");
        // printGeneral(probabilies);
        normalizeProbs();
        // printGeneral();
        // System.out.println("probs after normalizing:");
        // printGeneral();
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }
    
    double getUtilityOfAction(int x, int y, int action) {
        double totalUtility = 0.0;
        int numberOfAvailableMoves = getValidDirections(x, y);
        for (int i = 0; i < 5; i++) {
            double myMoveProb = action == i ? moveProb : (1 - moveProb) / numberOfAvailableMoves;    
            switch (i) {
                case NORTH:
                    if (mundo.grid[x][y - 1] != WALL) {
                        totalUtility += myMoveProb * prevUtilities[x][y - 1];
                    }
                    break;
                case SOUTH:
                    if (mundo.grid[x][y + 1] != WALL) {
                        totalUtility += myMoveProb * prevUtilities[x][y + 1];
                    }
                    break;
                case EAST:
                    if (mundo.grid[x + 1][y] != WALL) {
                        totalUtility += myMoveProb * prevUtilities[x + 1][y];
                    }
                    break;
                case WEST:
                    if (mundo.grid[x - 1][y] != WALL) {
                        totalUtility += myMoveProb * prevUtilities[x - 1][y];
                    }
                    break;
                case STAY:
                    totalUtility += myMoveProb * prevUtilities[x][y];
                    break;
            }
        }
        return totalUtility;
    }

    void updateCurrentLocation(int action) {
        switch (action) {
            case NORTH:
                currentY -= 1;
                break;
            case SOUTH:
                currentY += 1;
                break;
            case EAST:
                currentX += 1;
                break;
            case WEST:
                currentX -= 1;
                break;
        }
    }

    int getActionForKnownPosition() {
        double maxAction = 0.0;
        int maxIndex = STAY;
        getValidDirections(currentX, currentY);

        for (int i = 0; i < 4; i++) {
            if (sureValidDirections[i] == 1) {
                switch (i) {
                    case NORTH:
                        if (utilities[currentX][currentY - 1] > maxAction) {
                            maxIndex = i;
                            maxAction = utilities[currentX][currentY - 1];
                        }
                        break;
                    case SOUTH:
                        if (utilities[currentX][currentY + 1] > maxAction) {
                            maxIndex = i;
                            maxAction = utilities[currentX][currentY + 1];
                        }
                        break;
                    case EAST:
                        if (utilities[currentX + 1][currentY] > maxAction) {
                            maxIndex = i;
                            maxAction = utilities[currentX + 1][currentY];
                        }
                        break;
                    case WEST:
                        if (utilities[currentX - 1][currentY] > maxAction) {
                            maxIndex = i;
                            maxAction = utilities[currentX - 1][currentY];
                        }
                        break;
                }
            }
        }

        updateCurrentLocation(maxIndex);
        return maxIndex;
    }

    double getMostProbablePosition() {
        currentX = -1;
        currentY = -1;
        double maxProb = -1.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProb) {
                    currentX = x;
                    currentY = y;
                    maxProb = probs[x][y];
                }
            }
        }
        System.out.println("maxProb: " + maxProb);
        return maxProb;
    }

    int getBestActionForUnknown(int x, int y) {
        double maxUtility = Double.NEGATIVE_INFINITY;
        int bestAction = -1;
        getValidDirections(x, y);
        for (int i = 0; i < 4; i++) {
            if (sureValidDirections[i] == 1) {
                switch (i) {
                    case NORTH:
                        System.out.println("utility moving NORTH: " + utilities[x][y - 1]);
                        if (utilities[x][y - 1] > maxUtility) {
                            bestAction = i;
                            maxUtility = utilities[x][y - 1];
                        }
                        break;
                    case SOUTH:
                        System.out.println("utility moving SOUTH: " + utilities[x][y + 1]);
                        if (utilities[x][y + 1] > maxUtility) {
                            bestAction = i;
                            maxUtility = utilities[x][y + 1];
                        }
                        break;
                    case EAST:
                        System.out.println("utility moving EAST: " + utilities[x + 1][y]);
                        if (utilities[x + 1][y] > maxUtility) {
                            bestAction = i;
                            maxUtility = utilities[x + 1][y];
                        }
                        break;
                    case WEST:
                        System.out.println("utility moving WEST: " + utilities[x - 1][y]);
                        if (utilities[x - 1][y] > maxUtility) {
                            bestAction = i;
                            maxUtility = utilities[x - 1][y];
                        }
                        break;
                }
            }
        }
        return bestAction;
    }

    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction(String sonars) {
        if (knownPosition) {
            return getActionForKnownPosition();
        }

        double probability = getMostProbablePosition();

        if (probability < 0.5 && sonars != "Undefined") {
            // // find safer move
            // //since sonars are not defined on the first move, we 
            // //check that sonars doesn't equal "Undefined" to make sure
            // //the sonars have actually read something
            // System.out.println("Unsure of place, moving towards wall...");
            // int safeMoves[] = new int[4];
            // int numSafeMoves = 0;
            // for (int i = 0; i < 4; i++) {
            //     if (sonars.charAt(i) == '1') {
            //         safeMoves[numSafeMoves] = i;
            //         numSafeMoves += 1;
            //     }
            // }
            // int randAction = 0;
            // if (numSafeMoves > 0) {
            //     int randNum = ((int) Math.random()) % numSafeMoves;
            //     randAction = safeMoves[randNum];
            // }
            // else {
            //     randAction = ((int) Math.random()) % 4;
            // }
            // return randAction;
            HashMap<Integer, Integer> matchingDict = new HashMap<Integer, Integer>();
            int sonarArray[] = new int[4];
            for (int i = 0; i < 4; i++){
                //We're flipping it because we want to compare valid moves
                sonarArray[i] = sonars.charAt(i) == '1' ? 0 : 1;
            }
            int keyCounter = 0;
            int numCorrect = 0;
            for (int y = 0; y < mundo.height; y++){
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == OPEN) {
                        getValidDirections(x, y);
                        numCorrect = 0;
                        for (int j = 0; j < 4; j++) {
                            if (sonarArray[j] == sureValidDirections[j]) {
                                numCorrect += 1;
                            }
                        }
                        if (numCorrect > 0) {
                            matchingDict.put(keyCounter, numCorrect);
                        }
                    }
                    keyCounter += 1;
                }
            }
            // Map<Integer, Integer> mySortedMap = sortByValue(matchingDict);
            double bestScore = -500.0;
            int bestX = 1;
            int bestY = 1;
            int mapX = 0;
            int mapY = 0;
            for (Map.Entry<Integer, Integer> entry : matchingDict.entrySet()) {
                // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                mapY = (int) entry.getKey() / mundo.height;
                mapX = entry.getKey() % mundo.height;
                if (probs[mapX][mapY] * entry.getValue() > bestScore){
                    bestScore = probs[mapX][mapY] * entry.getValue();
                    bestX = mapX;
                    bestY = mapY;
                }
            }
            int bestAction = getBestActionForUnknown(bestX, bestY);
            return bestAction;
        }
        getValidDirections(currentX, currentY);

        
        // default action for now
        return getBestActionForUnknown(currentX, currentY);
    }

    void setInitialRewards() {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                switch (mundo.grid[x][y]) {
                    case OPEN:
                        rewards[x][y] = -1;
                        break;
                    case LAVA:
                        rewards[x][y] = -100;
                        break;
                    case GOAL:
                        rewards[x][y] = 60;
                        break;
                }
            }
        }
    }

    void setPreviousUtilties() {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                prevUtilities[x][y] = utilities[x][y];
            }
        }
    }

    void recalculateUtilities() {
        double maxDiff = 0.0;
        double maxUtility = 0.0;
        double tempUtility = 0.0;
        // printGeneral(rewards);
        do {
            maxDiff = 0.0;
            // try {
            //     System.out.println("Sleeping...");
            //     Thread.sleep(500);  // delay that is useful to see what is happening when the AI selects actions
            // } catch (InterruptedException e) {

            // }
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    maxUtility = Double.NEGATIVE_INFINITY;
                    if (mundo.grid[x][y] == OPEN) {
                        getValidDirections(x, y);
                        for (int i = 0; i < 4; i++) {
                            if (sureValidDirections[i] == 1) {
                                tempUtility = getUtilityOfAction(x, y, i);
                                switch (i) {
                                    case NORTH:
                                        if (tempUtility > maxUtility) {
                                            // maxUtility = prevUtilities[x][y-1];
                                            maxUtility = tempUtility;
                                        }
                                    case SOUTH:
                                        if (tempUtility > maxUtility) {
                                            // maxUtility = prevUtilities[x][y+1];
                                            maxUtility = tempUtility;
                                        }
                                    case EAST:
                                        if (tempUtility > maxUtility) {
                                            // maxUtility = prevUtilities[x+1][y];
                                            maxUtility = tempUtility;
                                        }
                                    case WEST:
                                        if (tempUtility > maxUtility) {
                                            // maxUtility = prevUtilities[x-1][y];
                                            maxUtility = tempUtility;
                                        }
                                }
                            }
                        }
                        utilities[x][y] = rewards[x][y] + (discountFactor * maxUtility);
                        maxDiff = Math.max(maxDiff, Math.abs(utilities[x][y] - prevUtilities[x][y]));
                    }
                }
            }
            // System.out.println("maxDiff: " + maxDiff);
            setPreviousUtilties();
        } while (maxDiff > 2.0);

    }            


    void setInitialUtilities() {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                prevUtilities[x][y] = rewards[x][y];
                utilities[x][y] = rewards[x][y];
            }
        }
    }

    void initializeValueIteration() {
        //initialize reward for goal and all other open tiles
        setInitialRewards();
        setInitialUtilities();
        recalculateUtilities();
    }
    
    void doStuff() {
        int action;
        
        //valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        initializeValueIteration();
        String sonars = "Undefined";
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(sonars); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars);
                recalculateUtilities(); 
                // System.out.println("Probabilities after iterations =================================================================");
                // printGeneral(probs);

                // System.out.println("\n\nUtilities after iterations =================================================================");
                // printGeneral(utilities);
                System.out.println("We think we're at: (" + currentX + ", " + currentY + ")");
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}