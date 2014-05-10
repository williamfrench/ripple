package calculate.maths;

import java.util.HashSet;

import calculate.maths.Factoriser.DivisorPair;

//sometimes java is not so amazing
public class OneModFinder {
    
    public static HashSet<DivisorPair> oneModN(int N) {
        HashSet<DivisorPair> divisorPairs = new HashSet<DivisorPair>();
        for (int i=1; i<N; i++) {
            GcdResult gcdResult = gcd(i, N);
            if (gcdResult.gcd == 1) {
                divisorPairs.add(new DivisorPair(mod(gcdResult.x,N), i));
            }
        }
        return divisorPairs;
    }
    
    private static GcdResult gcd(int a, int b) {
        if (a == 0) {
            return new GcdResult(0, 1, b);
        }
        else {
            GcdResult gcdResult = gcd(b % a, a);
            return new GcdResult(gcdResult.y - (b / a) * gcdResult.x, gcdResult.x, gcdResult.gcd);
        }
    }

    private static class GcdResult {
        public GcdResult(int x, int y, int gcd) {
            this.x = x;
            this.y = y;
            this.gcd = gcd;
        }

        final int x, y, gcd;
        
        @Override
        public String toString() {
            return x + " " + y + " -- " + gcd;
        }
    }
    
    private static int mod(int x, int m) {
        int r = x%m;
        if (r < 0) {
            r+=m;
        }
        return r;
    }
}
