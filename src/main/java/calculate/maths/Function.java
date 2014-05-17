package calculate.maths;

import calculate.Creator.Point;

public interface Function {

    public Point<Float> doYourThing(Point<Integer> input, int N);
    
    Function NONE = new Function() {
        @Override
        public Point<Float> doYourThing(Point<Integer> point, int N) {
            return new Point<Float>((float)point.x, (float)point.y);
        }
    };
    
    //possibly off by one errors everywhere
    Function TO_CIRCLE = new Function() {
        @Override
        public Point<Float> doYourThing(Point<Integer> point, int N) {
            float radius = (N-1)/2.f;
            Point<Float> sanitised = new Point<Float>(point.x-radius, point.y-radius); 
            double ratio = Math.abs(sanitised.y) > Math.abs(sanitised.x) 
                               ? Math.sqrt(1+(sanitised.x*sanitised.x)/(sanitised.y*sanitised.y)) 
                               : Math.sqrt(1+(sanitised.y*sanitised.y)/(sanitised.x*sanitised.x));

            return new Point<Float>(sanitised.x/(float)ratio + radius, sanitised.y/(float)ratio + radius);
        }
    };
    
    Function TO_INVERSE_CIRCLE = new Function() {
        @Override
        public Point<Float> doYourThing(Point<Integer> point, int N) {
            Point<Float> circled = TO_CIRCLE.doYourThing(point, N);
            float radius = (N-1)/2;
            Point<Float> sanitised = new Point<Float>(circled.x-radius, circled.y-radius);
            float new_abs = (float) (radius-Math.sqrt(sanitised.x*sanitised.x + sanitised.y*sanitised.y));
            float ratio = (float) (new_abs/Math.sqrt(sanitised.x*sanitised.x + sanitised.y*sanitised.y));
            return new Point<Float>(sanitised.x*ratio + radius, sanitised.y*ratio + radius);

        }
    };
    
    
    
    
//    Function TO_INVERSE_SQUARE = new Function() {
//        @Override
//        public Point<Float> doYourThing(Point<Integer> point, int N) {
//            Point<Float> inverseCircled = TO_INVERSE_CIRCLE.doYourThing(point, N);
//            float radius = (N-1)/2.f;
//            Point<Float> sanitised = new Point<Float>(point.x-radius, point.y-radius);
//            
//        }
//    };
}
