package calculate;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import misc.Pixmap;
import calculate.Factoriser.DivisorPair;
import calculate.Factoriser.DivisorPairWithPrime;
import calculate.Factoriser.GeneratedDivisorPairs;


public class Creator {

    private static final Color INTERSECTION_COLOR = Color.black;
    private static final Color SUPRISE_COLOR = Color.white;
    private static final Color NO_BACKGROUND_COLOR = Color.gray;
    
    private static final int[][] possibleNeighbours = new int[][] {
        new int[]{0,0},
        new int[]{1,0}, new int[]{-1,0}, new int[]{0,1}, new int[]{0,-1},
        new int[]{1,1}, new int[]{-1,1}, new int[]{1,-1}, new int[]{-1,-1},
        new int[]{2,0}, new int[]{-2,0}, new int[]{0,2}, new int[]{0,-2}
    };
    
    
    private final ColorPicker colorPicker = new ColorPicker();
    
    private Pixmap currentPixmap;
    private Set<List<Integer>> divisorPairs;
    private int N;
    private boolean showUnsuprising = true;
    private boolean showSuprising = true;
    private int thickness = 3;
    private int[][] neighbours = Arrays.copyOf(possibleNeighbours, 9);
    
    //could I preempt the need for this synchronized keyword?
    private synchronized BufferedImage paint() {
        currentPixmap = new Pixmap(N-1, N-1);
        divisorPairs = new HashSet<List<Integer>>();
        Color[][] triangle = null;
        
        if (!colorPicker.noBackground()) {
            //make centre around middle, not right hand side?
            triangle = new Color[N-1][];
            
            for (int row = 1; row <= (N+1)/2; row++) {
                triangle[row-1] = new Color[row];
                triangle[N-row-1] = new Color[row];
                for (int col = 1; col <= row; col++) {
                    int product = (row*col)%N;
                    float s1Value = ((float)product)/((float)N);
                    // :/
                    triangle[row-1][col-1] = colorPicker.getColor(s1Value);
                    triangle[N-row-1][col-1] = colorPicker.getColor(1-s1Value);                
                }
            }
        }

        //XXX: swap iteration around?
        for (int row = 1; row <= N-1; row++) {
            for (int col = 1; col <= Math.min(row, N-row); col++) {
                Color color;
                if (!colorPicker.noBackground()) {
                    color = triangle[row-1][col-1];
                } else {
                    color = NO_BACKGROUND_COLOR;
                }

                currentPixmap.setColor(row-1, col-1, color);
                currentPixmap.setColor(col-1, row-1, color);
                currentPixmap.setColor(N-col-1, N-row-1, color);
                currentPixmap.setColor(N-row-1, N-col-1, color);
            }
        }
        
        GeneratedDivisorPairs divisorPairs = Factoriser.generateDivisorPairs(N);
        
        for (DivisorPairWithPrime divisorPair : divisorPairs.divisorPairWithPrime) {
            Color divisorColor = SmallPrimes.majorScaleColors.get(divisorPair.primeResponsible);
            if (divisorColor == null) {
                System.out.println(divisorPair.primeResponsible);
            }
            doStuff(divisorPair, divisorColor);

        }
        
        for (DivisorPair divisorPair: divisorPairs.intersectionPairs) {
            doStuff(divisorPair, INTERSECTION_COLOR);
        }
        
        if (showSuprising) {
            //System.out.println(N + " " + divisorPairs);
            for (DivisorPair divisorPair : divisorPairs.suprisePairs) {
                //XXX copy and paste aaaah
                for (int[] coords : neighbours) {
                    currentPixmap.setColor(divisorPair.a-1+coords[0], divisorPair.b-1+coords[1], SUPRISE_COLOR);
                    currentPixmap.setColor(divisorPair.a-1+coords[0], divisorPair.b-1+coords[1], SUPRISE_COLOR);
                    currentPixmap.setColor(N-divisorPair.a-1+coords[0], N-divisorPair.b-1+coords[1], SUPRISE_COLOR);
                    currentPixmap.setColor(N-divisorPair.a-1+coords[0], N-divisorPair.b-1+coords[1], SUPRISE_COLOR);
                }
            }
        }
        return currentPixmap.getImage();
    }
    
    private void doStuff(DivisorPair divisorPair, Color divisorColor) {
        if (showUnsuprising) {
            for (int[] coords : neighbours) {
                currentPixmap.setColor(divisorPair.a-1+coords[0], divisorPair.b-1+coords[1], divisorColor);
                currentPixmap.setColor(divisorPair.b-1+coords[0], divisorPair.a-1+coords[1], divisorColor);
            }
        }
        divisorPairs.remove(Arrays.asList(divisorPair.a, divisorPair.b));
        divisorPairs.remove(Arrays.asList(divisorPair.b, divisorPair.a));
    }

    //a bit iffy all this really
    private Integer[][] getCombos(int row, int col) {
        int mrow = N-row; int mcol = N-col;
        return new Integer[][]{
            new Integer[]{row, col}, new Integer[]{mrow, mcol}, new Integer[]{col, row}, new Integer[]{mcol, mrow},
            new Integer[]{mrow, col}, new Integer[]{row, mcol}, new Integer[]{mcol, row}, new Integer[]{col, mrow},
        };
        
    }
    
    //XXX: should these really be returning paint()?
    /////////////////////////////////////////////////////////
    public BufferedImage setN(int N){
        this.N = N;
        return paint();
    }
    
    public BufferedImage cycleColor() {
        colorPicker.cycleColorCircle();
        return paint();
    }
    
    public BufferedImage toggleUnsuprising() {
        showUnsuprising = !showUnsuprising;
        return paint();
    }
    
    public BufferedImage toggleSuprising() {
        showSuprising  = !showSuprising;
        return paint();
    }
    
    public BufferedImage cycleThickness() {
        if (++thickness > 4) {
            thickness = 1; 
        }
        int length = 1 + (thickness-1)*4;
        neighbours = Arrays.copyOf(possibleNeighbours, length);
        return paint();
    }
    
    ////////////////////////////////////////////////////////
    
    public void writeToFile() throws IOException {
        writeToFile("C:\\Users\\William\\Desktop\\ripple\\" + N + ".png");
    }
    
    public void writeToFile(String file) throws IOException {
        currentPixmap.write(file);
    }
    
}