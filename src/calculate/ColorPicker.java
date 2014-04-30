package calculate;

import java.awt.Color;


public class ColorPicker {
	
	private static final int color1x = 0, color1y = 0, color1z = 0;
	private static final int color2x = 255, color2y = 255, color2z = 255;
	
	private static final Color[][] colorCircles = new Color[][] {
		new Color[]{Color.black, Color.black},
	    new Color[]{Color.yellow, Color.cyan, Color.magenta,Color.yellow},
		new Color[]{Color.red, Color.blue, Color.green, Color.red}
	};
	
	private boolean useCircularColors = true;
	private int currentColorCircle = 2;
	private Color[] colorCircle = colorCircles[currentColorCircle]; 
	
	public Color getColor(float number) {
		return useCircularColors ? getCircularColor(number) : getLinearColor(number);
	}
	
	// number in [0,1] 
	private Color getCircularColor(float number) {
		if (trim(number) == 1.f) {
			number = 0.f;
		}
		int parts = colorCircle.length - 1;
		float fraction = (parts)*number;
		int intPart = (int)fraction;
		float floatPart = fraction - intPart;
		
		return midColor(colorCircle[intPart], colorCircle[intPart+1], floatPart);
	}

	//a little convoluted?
	public void cycleColorCircle() {
		if (!useCircularColors) {
			useCircularColors = true;
			currentColorCircle = 0;
		} else if (++currentColorCircle >= colorCircles.length) {
			useCircularColors = false;
		}
		
		if (useCircularColors) {
			colorCircle = colorCircles[currentColorCircle];
		}
	}
	
	//number in [0,1]
	private Color getLinearColor(float number) {
		float x = average(number, color1x, color2x);
		float y = average(number, color1y, color2y);
		float z = average(number, color1z, color2z);
		return new Color(x, y, z);
	}
	
	private Color midColor(Color start, Color end, float along) {
		float newr = midPoint(start.getRed(), end.getRed(), along)/255.f;
		float newg = midPoint(start.getGreen(), end.getGreen(), along)/255.f;
		float newb = midPoint(start.getBlue(), end.getBlue(), along)/255.f;
		float radius = (float) Math.sqrt((newr*newr + newg*newg + newb*newb));
		newr = trim(newr/radius);
		newg = trim(newg/radius);
		newb = trim(newb/radius);
		return new Color(newr, newg, newb);
	}
	
	//XXX shit name
	private float average(float number, float start, float end) {
		//return (float) (number * start + (N - number) * end) / (float) (255 * N);
		return (number * start + (1 - number) * end) / 255 ;
	}
	
	private static float trim(float f) {
		if (f < 0 && f > -0.0001) {
			return 0.0f;
		}
		if (f > 1 && f < 1.0001) {
			return 1.0f;
		}
		return f;
	}
	
	private float midPoint(int start, int end, float along) {
		return start*(1-along) + end*along;
	}
	
	//sorry
	public boolean noBackground() {
		return currentColorCircle == 0;
	}
	
}