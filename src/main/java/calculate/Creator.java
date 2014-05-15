package calculate;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import misc.Pixmap;
import ui.Shower.p;
import calculate.maths.Factoriser;
import calculate.maths.Factoriser.DivisorPairWithPrime;
import calculate.maths.Factoriser.GeneratedDivisorPairs;
import calculate.maths.Factoriser.Pair;
import calculate.maths.Function;
import calculate.maths.OneModFinder;
import calculate.maths.SmallPrimes;


public class Creator {

    private static final int lingerPeriod = 10000;
    private static final LinkedList<Set<Pair>> vapourTrail = new LinkedList<Set<Pair>>();
    private boolean letsVapourTrail = false;
    private final boolean remOne = true;
    
    private static final Color INTERSECTION_COLOR = Color.black;
    private static final Color SUPRISE_COLOR = Color.white;
    private static final Color NO_BACKGROUND_COLOR = Color.WHITE;
    
    private static final int[][] possibleNeighbours = new int[][] {
        new int[]{0,0},
        new int[]{1,0}, new int[]{-1,0}, new int[]{0,1}, new int[]{0,-1},
        new int[]{1,1}, new int[]{-1,1}, new int[]{1,-1}, new int[]{-1,-1},
        new int[]{2,0}, new int[]{-2,0}, new int[]{0,2}, new int[]{0,-2}
    };
    
    
    private final ColorPicker colorPicker = new ColorPicker();
    
    private Pixmap currentPixmap;
    private int N;
    private boolean showUnsuprising = true;
    private boolean showSuprising = true;
    private int thickness = 3;
    private int[][] neighbours = Arrays.copyOf(possibleNeighbours, 9);
    private final Function[] functions = new Function[]{Function.NONE, Function.TO_CIRCLE, Function.TO_INVERSE_CIRCLE};
    private int currentFunction = 0;
    
    //could I preempt the need for this synchronized keyword?
    private synchronized BufferedImage paint() {
        currentPixmap = new Pixmap(N-1, N-1);
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
        
        if (letsVapourTrail) {
            Set<Pair> mergedDivisorPairs;
        
            if (!remOne) {
                mergedDivisorPairs = new HashSet<Pair>(divisorPairs.suprisePairs);
                mergedDivisorPairs.addAll(divisorPairs.intersectionPairs);
                mergedDivisorPairs.addAll(divisorPairs.divisorPairWithPrime);
            } else {
                mergedDivisorPairs = OneModFinder.oneModN(N);
//                mergedDivisorPairs = new HashSet<Pair>();
//                for (int row = 1; row <= N; row++) {
//                    for (int col = 1; col <= row; col++) {
//                        //if ((row*col)%N!=0 && (row*col)%N<=N/500) {
//                        if ((row*col)%N!=0 && (row*col)%N==1) {
//
//                            mergedDivisorPairs.add(new Pair(row, col));
//                        }
//                    }
//                }
            }
            vapourTrail.addFirst(mergedDivisorPairs);
            if (vapourTrail.size() > lingerPeriod) {
                vapourTrail.removeLast();
            } 
            while (vapourTrail.size() > N-1) {
                vapourTrail.removeLast();
            }
            
            for (int i=0; i<vapourTrail.size()-1; i++) {
                //float blackness = (float)i/(float)lingerPeriod;
                int flipped = vapourTrail.size()-i-1;
                final float ratio = (float)N/(float)(N-flipped);
                Function function = new Function() {
                    
                    @Override
                    public Point<Float> doYourThing(Point<Integer> input, int k) {
                        return new Point<Float>(ratio*input.x, ratio*input.y);
                    }
                };
                for (Pair divisorPair : vapourTrail.get(flipped)) {
                    doTransformedPointWithColor(new Point<Integer>(divisorPair.a-1, divisorPair.b-1), function, currentPixmap, Color.black);
                    doTransformedPointWithColor(new Point<Integer>(divisorPair.b-1, divisorPair.a-1), function, currentPixmap, Color.black);
                }
            }
        }
        else {
            for (DivisorPairWithPrime divisorPair : divisorPairs.divisorPairWithPrime) {
                Color divisorColor = SmallPrimes.majorScaleColors.get(divisorPair.primeResponsible);
                if (divisorColor == null) {
                    System.out.println(divisorPair.primeResponsible);
                }
                doStuff(divisorPair, divisorColor);
    
            }
            
            for (Pair divisorPair: divisorPairs.intersectionPairs) {
                doStuff(divisorPair, INTERSECTION_COLOR);
            }
            
            if (showSuprising) {
                //System.out.println(N + " " + divisorPairs);
                for (Pair divisorPair : divisorPairs.suprisePairs) {
                    //XXX copy and paste aaaah
                    for (int[] coords : neighbours) {
                        currentPixmap.setColor(divisorPair.a-1+coords[0], divisorPair.b-1+coords[1], SUPRISE_COLOR);
                        currentPixmap.setColor(divisorPair.b-1+coords[0], divisorPair.a-1+coords[1], SUPRISE_COLOR);
                    }
                }
            }
        }
        transformPixmap(functions[currentFunction]);
        return currentPixmap.getImage();
    }
    

    //to do rename Point -> Pair, Pair -> DivisorPair
    public static class Point<T> {
        public final T x;
        public final T y;
        
        public Point(T x, T y) {
            this.x = x; this.y = y;
        }
        
        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    private void transformPixmap(Function function) {
        Pixmap newPixmap = new Pixmap(N-1,N-1);
        for (int i=0; i<N; i++) {
            for (int j=0; j<N; j++) {
                newPixmap.setColor(i, j, NO_BACKGROUND_COLOR);
            }
        }
        for (int i=0; i<N; i++) {
            for (int j=0; j<N; j++) {
                doTransformedPoint(new Point<Integer>(i, j), function, newPixmap);
            }
        }
        currentPixmap = newPixmap;
    }
    
    private void doTransformedPoint(Point<Integer> point, Function function, Pixmap newPixmap) {
        //float currentColour = currentPixmap.getColor(point.x, point.y).getBlue()/255.f;//spoiler alert: its a shade of grey, so this is enough
        Color currentColour = currentPixmap.getColor(point.x, point.y);
        doTransformedPointWithColor(point, function, newPixmap, currentColour);
    }
    
    //fairly crude atm, only diagonals, but anything else would require a lot of braining
    private void doTransformedPointWithColor(Point<Integer> point, Function function, Pixmap newPixmap, Color currentColour) {
        Point<Float> tl = function.doYourThing(point, N);

        //p.p(point + " " + tl);
        Point<Float> br = function.doYourThing(new Point<Integer>(point.x+1, point.y+1), N);
        Point<Integer> topLeft = new Point<Integer>((int)Math.min(tl.x, br.x), (int)Math.min(tl.y, br.y));
        Point<Integer> bottomRight = new Point<Integer>((int)Math.max(tl.x, br.x), (int)Math.max(tl.y, br.y));
        
        bottomRight = new Point<Integer>(Math.max(bottomRight.x, topLeft.x+1), Math.max(bottomRight.y, topLeft.y+1));
        if (point.x==1 && point.y==N/2){
            p.p(tl + " " + br);
        }
        if (((topLeft.x-bottomRight.x)*(topLeft.y-bottomRight.y) > 10)){
            return;
        }
        
        //float newColour = currentColour;///((float)(bottomRight.x-topLeft.x)*(bottomRight.y-topLeft.y));
        
        for (int x=topLeft.x; x<bottomRight.x; x++) {
            for (int y=topLeft.y; y<bottomRight.y; y++) {
                if (point.x==1 && point.y==N/2){
                    p.p(x + " " + y);
                }
                //if (newPixmap.getColor(x,y).getBlue() == 255) {
//                    if (newColour < 0) newColour=0;
//                    if (newColour > 1.f) newColour=0.999f;
                    newPixmap.setColor(x, y, currentColour);
                    //p.p(x+" "+y+" "+newColour);
                //}
                //p.p(newPixmap.getColor(x,y));
            }
        }
    }
    
    private void doPoint(int x, int y, float colorf) {
        Color color;
        try {
            color = new Color(colorf, colorf, colorf);
        } catch (Exception e){
            e.printStackTrace();
            p.p(colorf + " " + thickness);
            return; 
        }
        currentPixmap.setColor(x, y, color);
    }

    
    private void doStuff(Pair divisorPair, Color divisorColor) {
        if (showUnsuprising) {
            for (int[] coords : neighbours) {
                currentPixmap.setColor(divisorPair.a-1+coords[0], divisorPair.b-1+coords[1], divisorColor);
                currentPixmap.setColor(divisorPair.b-1+coords[0], divisorPair.a-1+coords[1], divisorColor);
            }
        }
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
    
    public BufferedImage clearVapours() {
        vapourTrail.clear();
        return paint();
    }
    
    public BufferedImage toggleLetsVapour() {
        letsVapourTrail = !letsVapourTrail;
        return paint();
    }
    
    public BufferedImage cycleFunction() {
        currentFunction++;
        if (currentFunction == functions.length) {
            currentFunction = 0;
        }
        return paint();
    }
    
    ////////////////////////////////////////////////////////
    
    public void writeToFile() throws IOException {
        int version=0;
        String basePath = "C:\\Users\\William\\Desktop\\ripple\\", fileName;
        do {
            fileName = basePath + N + "." + version++ + ".png";
        } while (new File(fileName).exists()); {}
        
        writeToFile(fileName);
    }
    
    public void writeToFile(String file) throws IOException {
        currentPixmap.write(file);
    }
    
}