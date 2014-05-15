package calculate.maths;

import static calculate.maths.SmallPrimes.someSmallPrimes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Factoriser {

    //TODO: replace second list with a Pair
    /** keeps quiet about any prime > sqrt(n)*/
    private static List<List<Integer>> factorise(int number) {
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
    
    
    private static class Divisor {        
        public Divisor(int divisor, int prime) {
            this.divisor = divisor;
            this.prime = prime;
        }
        
        final int divisor;
        final int prime;
        
        @Override
        public String toString() {
            return divisor + " " + prime;
        }
    }
    
    private static final Comparator<Divisor> divisorComparitor = new Comparator<Divisor>() {
        @Override
        public int compare(Divisor o1, Divisor o2) {
            return o1.divisor - o2.divisor;
        }
    };
    
    /**
     * only ones <= sqrt(N)
     */
    private static List<Divisor> generateDivisors(int number) {
        List<List<Integer>> factorised = factorise(number);
        List<Divisor> divisors = new ArrayList<Divisor>(Arrays.asList(new Divisor(1,-1)));
        for (List<Integer> tuple : factorised) {
            List<Divisor> newDivisors = new ArrayList<Divisor>();
            int primePower = 1;
            for (int i=0; i<tuple.get(1); i++) {
                primePower*=tuple.get(0);
                for (Divisor divisor : divisors) {
                    int product = divisor.divisor*primePower;
                    if (product*product > number) {
                        break;
                    }
                    int prime = divisor.divisor == 1 ? tuple.get(0) : -1 ;//I might forget what this means
                    Divisor newDivisor = new Divisor(product, prime);
                    newDivisors.add(newDivisor);
                }
            }
            divisors.addAll(newDivisors);
            Collections.sort(divisors, divisorComparitor);
        }
        return divisors;
    }
    
    public static GeneratedDivisorPairs generateDivisorPairs(int number) {
        List<Divisor> divisors = generateDivisors(number);
        Set<Pair> allDivisorPairs = new HashSet<Pair>();
        DivisorPairWithPrimes divisorPairWithPrimes = new DivisorPairWithPrimes();
        for (Divisor divisor : divisors) {
            int remainder = number/(divisor.divisor);
            for (int a = divisor.divisor; a < number; a+=divisor.divisor) {
                for (int b = remainder; b < number; b+=remainder) {
                    if (divisor.prime != -1) {
                        DivisorPairWithPrime divisorPair = new DivisorPairWithPrime(a, b, divisor.prime);
                        allDivisorPairs.add(divisorPair);
                        divisorPairWithPrimes.add(divisorPair);
                    }
                    else {
                        allDivisorPairs.add(new Pair(a,b));
                    }
                }
            }
        }
        Set<DivisorPairWithPrime> divisorPairWithPrimeSet = divisorPairWithPrimes.getDistinct();
        Set<Pair> intersectionSet = divisorPairWithPrimes.getIntersection();
        allDivisorPairs.removeAll(divisorPairWithPrimeSet);
        allDivisorPairs.removeAll(intersectionSet);
        return new GeneratedDivisorPairs(divisorPairWithPrimeSet, intersectionSet, allDivisorPairs);
    }
    
    public static class Pair {
        public final int a;
        public final int b;

        public Pair(int a, int b) {
            this.a = Math.min(a, b);
            this.b = Math.max(a, b);
        }
        
        @Override
        public boolean equals(Object obj) {
            Pair that = (Pair) obj;
            return a == that.a && b == that.b;
        }
        
        @Override
        public int hashCode() {
            //crude
            return a + (b<<16);
        }
        
        @Override
        public String toString() {
            return a + " " + b;
        }
    }
        
    public static class DivisorPairWithPrime extends Pair {
        public final int primeResponsible;

        public DivisorPairWithPrime(int a, int b, int primeResponsible) {
            super(a, b);
            this.primeResponsible = primeResponsible;
        }
    }
    
    private static class DivisorPairWithPrimes {
        HashMap<Integer, Set<DivisorPairWithPrime>> primeToSet = new HashMap<Integer, Set<DivisorPairWithPrime>>();
        
        public void add(DivisorPairWithPrime divisorPairWithPrime) {
            Set<DivisorPairWithPrime> setForPrime = primeToSet.get(divisorPairWithPrime.primeResponsible);
            if (setForPrime == null) {
                setForPrime = new HashSet<DivisorPairWithPrime>();
                primeToSet.put(divisorPairWithPrime.primeResponsible, setForPrime);
            }
            setForPrime.add(divisorPairWithPrime);
        }

        private final Set<DivisorPairWithPrime> distinct = new HashSet<DivisorPairWithPrime>();
        private final Set<Pair> intersection = new HashSet<Pair>();
        
        //FIXME: second method depends on the first being called
        public Set<DivisorPairWithPrime> getDistinct() {
            for (Set<DivisorPairWithPrime> divisorPairSet : primeToSet.values()) {
                for (DivisorPairWithPrime divisor : divisorPairSet) {
                    if (intersection.contains(divisor)) {
                        continue;
                    }
                    if (!distinct.add(divisor)) {
                        distinct.remove(divisor);
                        intersection.add(divisor);
                    }
                }
            }
            return distinct;
        }
        
        public Set<Pair> getIntersection() {
            return intersection;
        }
       
    }
    
    public static class GeneratedDivisorPairs {
        
        public GeneratedDivisorPairs(Set<DivisorPairWithPrime> divisorPairWithPrime, 
                Set<Pair> intersectionPairs, Set<Pair> suprisePairs) {
            this.divisorPairWithPrime = divisorPairWithPrime;
            this.intersectionPairs = intersectionPairs;
            this.suprisePairs = suprisePairs;
        }
        
        public final Set<DivisorPairWithPrime> divisorPairWithPrime;
        public final Set<Pair> intersectionPairs;
        public final Set<Pair> suprisePairs;
    }
}
