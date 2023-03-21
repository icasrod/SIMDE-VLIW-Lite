/**
 * 
 */
package testSobelFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Iván Castilla
 *
 */
public class SobelFilter {
	final private static String INPUT_FILE = "C:\\Users\\Iván Castilla\\Downloads\\ojo.txt";
	final private static String OUTPUT_FILE = "C:\\Users\\Iván Castilla\\Downloads\\ojo2.txt";
	final private static int SIZE = 16;
	final private static double[][] FILTER = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}; 
	
	public static double[][] applyFilter(double [][] img) {
		final double [][] newImg = new double[SIZE][SIZE];
		double maxValue = 0.0;
		double minValue = 255.0;
		for (int i = 1; i < SIZE - 1; i++) {
			for (int j = 1; j < SIZE - 1; j++) {
				newImg[i][j] =	FILTER[0][0] * img[i-1][j-1] + FILTER[0][1] * img[i-1][j] + FILTER[0][2] * img[i-1][j+1] +				
								FILTER[1][0] * img[i][j-1] + FILTER[1][1] * img[i][j] + FILTER[1][2] * img[i][j+1] +
								FILTER[2][0] * img[i+1][j-1] + FILTER[2][1] * img[i+1][j] + FILTER[2][2] * img[i+1][j+1];
				if (newImg[i][j] < minValue)
					minValue = newImg[i][j];
				if (newImg[i][j] > maxValue)
					maxValue = newImg[i][j];
				System.out.print(newImg[i][j] + " ");
			}
		}
		for (int i = 1; i < SIZE - 1; i++) {
			for (int j = 1; j < SIZE - 1; j++) {
				newImg[i][j] = (newImg[i][j] - minValue) * 255 / (maxValue - minValue);
			}
		}
		return newImg;
	}

	public static void main(String[] args) {
		final File file = new File(INPUT_FILE);
		Scanner scan;
		try {
			scan = new Scanner(file);
			final double [][] img = new double[SIZE][SIZE];
			
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					String str = scan.next();
					img[i][j] = Double.parseDouble(str);
					System.out.print(img[i][j] + " ");
				}
			}
			System.out.println();
			final File outFile = new File(OUTPUT_FILE);
			final PrintWriter out = new PrintWriter(outFile);
			final double [][] outImg = applyFilter(img); 
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					out.print(outImg[i][j] + " ");
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
