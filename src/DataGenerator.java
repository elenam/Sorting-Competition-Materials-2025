
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class DataGenerator {
	private static int seed = 1001; // change the seed to get different data
	private static Random r = new Random(seed);
	private static int minLength = 20;
	private static int maxLength = 120;

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 3) {
			System.out.println(
					"Please run with these command line arguments: output file name, the length of the numbers, and the number of items");
			System.exit(0);
		}
		String outFileName = args[0];
		int len = Integer.parseInt(args[1]);
		int n = Integer.parseInt(args[2]);

		if (len < minLength || len > maxLength) {
			System.out.println(
					"Length must be between " + minLength + " and " + maxLength + " (inclusive), given " + len);
			System.exit(0);
		}

		String[] data = new String[n+1]; // Adding the target string to the output as the last value
		
		// Trying BigInteger constructor, to remove:
//		data[0] = "0101"; // Value = 5, leading 0
//		BigInteger test = new BigInteger(data[0],2);
//		data[1] = test.toString();
		

//		for (int i = 0; i < n; ++i) {
//			StringBuffer num = new StringBuffer();
//
//			// generating digits of the numbers
//			for (int j = 0; j < len; ++j) {
//				char d = (char) ('0' + r.nextInt(10));
//				num.append(d);
//			}
//
//			data[i] = num.toString();
//		}

		// Generate one more string and add it to the output:
		
		
		PrintWriter out = new PrintWriter(outFileName);
		for (String s : data) {
			out.println(s);
		}
		out.close();
	}
	
	/*
	 * A method that generates a new random string of 0s and 1s of length len 
	 */
	public static String generateNewStr(int len) {
		return "0101";
	}
	
	/*
	 * A method that generates a string based on a given string str of length len
	 * by randomly choosing toChange bits of it and then changing each bit to the opposite 
	 * for with probability 1/2 (resulting of toChange/2 bits changed on average)
	 */
	public static String changeString(String str, int len, int toChange) {
		return "0101";
	}

}
