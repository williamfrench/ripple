package calculate;

import static calculate.SmallPrimes.someSmallPrimes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Factoriser {

    //TODO: replace second list with a Pair
    /** keeps quiet about any prime > sqrt(n)*/
    public static List<List<Integer>> factorise(int number) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        int index = 0;
        int n = number; int curPrime = someSmallPrimes[index];
        while (curPrime*curPrime <= number) {
            int times = 0;
            while (n%curPrime == 0 && n!=1) {
                n/=curPrime;
                times++;
            }
            if (times > 0) {
                result.add(Arrays.asList(curPrime, times));
            }
            index+=1;
            curPrime = someSmallPrimes[index];
        }
        //dont actually want this one:
//        if (n != 1) {
//            result.add(Arrays.asList(n, 1));
//        }
        
        return result;
    }
    
    //XXX: hacky return type
    public static List<Set<DivisorPair>> generateDivisorPairs(int number) {
        Set<DivisorPair> results = new HashSet<DivisorPair>();
        Set<DivisorPair> globallyDuped = new HashSet<DivisorPair>();
        List<List<Integer>> factorised = factorise(number);
        for (int k=0; k<factorised.size(); k++) {
            int prime = factorised.get(k).get(0);
            int exponent =  factorised.get(k).get(1);
            int power = 1;
            Set<DivisorPair> resultForPrime = new HashSet<DivisorPair>();
            for (int i=1; i<=exponent; i++) {
                power*=prime;
                if (power*power >  number) {
                    break;
                }
                int quotient = number/power;
                for (int a=quotient; a<number; a+=quotient) {
                    for (int b=power; b<number; b+=power) {
                        //not too efficient:
                        resultForPrime.add(new DivisorPair(a,b,prime));
                    }
                }
            }
            for (DivisorPair result : resultForPrime) {
                if (!globallyDuped.contains(result)) {
                    if (!results.add(result)) {
                        results.remove(result);
                        globallyDuped.add(result);
                    }
                }
            }
        }
        return Arrays.asList(results, globallyDuped);
    }

    public static class DivisorPair {
        public final int a;
        public final int b;
        public final int primeResponsible;

        public DivisorPair(int a, int b, int primeResponsible) {
            this.a = Math.min(a, b);
            this.b = Math.max(a, b);
            this.primeResponsible = primeResponsible;
        }
        
        @Override
        public boolean equals(Object obj) {
            DivisorPair that = (DivisorPair) obj;
            return a == that.a && b == that.b;
        }
        
        @Override
        public int hashCode() {
            //crude
            return a + (b<<16);
        }
        
        @Override
        public String toString() {
            return a + " " + b + " " + primeResponsible;
        }
    }
}
