package calculate.maths;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import calculate.ColorPicker;

//XXX shit name, shit class
public class SmallPrimes {

    public static final int[] someSmallPrimes = new int[]{2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79
        ,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239};
    public static HashMap<Integer, Color> majorScaleColors = new HashMap<Integer, Color>();

    private static int primeCount = someSmallPrimes.length;
    //the use of the ColorPicker class is just horrendous
    private static ColorPicker colorPicker = new ColorPicker();
    
    static {
        float octaves = (primeCount-1)/7 + 1;
        float octaveOffset = 1/(2*octaves);
        for (int i=0; i<primeCount; i++) {
            float mod = i%7; float div = i/7;
            float color = mod*1.f/7.f + octaveOffset*div;
            color = mod1(color);
            majorScaleColors.put(someSmallPrimes[i], colorPicker.getColor(color));
        }
    }
    
    public static boolean isPrime(int n) {
        for (int smallPrime : someSmallPrimes) {
            if (smallPrime*smallPrime > n) {
                return true;
            }
            if (n%smallPrime ==0 ) {
                return false;
            }
        }
        throw new RuntimeException();              
    }
    
    public static Color getColor(List<Integer> divisorPair, int N) {
        int prime1 = getBestDivisor(divisorPair.get(0), N);
        int prime2 = getBestDivisor(divisorPair.get(1), N);
        return majorScaleColors.get(Math.max(prime1, prime2));
    }
    
    private static int getBestDivisor(Integer integer, int N) {
        for (int i=primeCount-1; i>= 0; i--) {
            if (someSmallPrimes[i]*someSmallPrimes[i] <= N && integer%someSmallPrimes[i] == 0 && N%someSmallPrimes[i] == 0){
                return someSmallPrimes[i];
            }
        }
        return 1;
    }
    
    //eww
    private static float mod1(float f) {
        while (f > 1.f) {
            f-=1.f;
        }
        return f;
    }
}
