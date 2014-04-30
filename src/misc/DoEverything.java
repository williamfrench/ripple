package misc;
import java.io.IOException;

import calculate.Creator;

//We've moved on from animated gifs
@Deprecated()
public class DoEverything {
    
    public static void main(String[] args) throws IOException {
        int N = 5040;//= 2*2*2*3*5*7;
        //d(5000);
//        for (int i=2; i<5000; i++) {
//            p(i);
//            Pixmap pixmap =  new ImageMaker(i);
//            pixmap.write("C:\\Users\\William\\Desktop\\ripple\\batch\\" + i + ".png");
//
//        }
        Creator creator = new Creator();
        creator.setN(N);
        creator.writeToFile();
    }
    
    public static void d(int i) throws IOException {
    }
    
    public static void p(Object a){
        System.out.println(a);
    }
}
