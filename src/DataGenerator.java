
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DataGenerator {
	private static int seed = 1001; // change the seed to get different data
	private static Random r = new Random(seed);
	// fixed parameters:
	private static int minLength = 20;
	private static int maxLength = 120;
	// these parameters may be changed, see README for ranges:
	private static int resetApprox = 500; // the probability of resetting a string sequence is 1 in resetApprox. 
	private static int minBitsPercent = 5; // In a string sequence the number of bits to change is uniformly distributed between the min and max percentages
	private static int maxBitsPercent = 20;
	

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

		String[] data = new String[n+1]; // n+1: Adding the target string to the output as the last value
		
		data[0] = generateNewStr(len); // starting the first sequence
		
		for (int i = 1; i < n; ++i) {
			if (r.nextInt(resetApprox)== 0) {
			 data[i] = generateNewStr(len);
			} else {
				// Generate the # of changed bits:
				int toChange = r.nextInt(minBitsPercent, maxBitsPercent);
				data[i] = changeString(data[i-1], len, toChange);
			}
		}

		// Generate one more string and add it to the output:
		data[n] = generateNewStr(len);
		
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
		StringBuilder str = new StringBuilder(len);
		
		for (int i = 0; i < len; ++i) {
			str.append(r.nextInt(2));
		}
		return str.toString();
	}
	
	/*
	 * A method that generates a string based on a given string str of length len
	 * by randomly choosing toChange bits of it and then changing each bit to the opposite 
	 * for with probability 1/2 (resulting of toChange/2 bits changed on average)
	 */
	public static String changeString(String str, int len, int toChange) {
		StringBuilder changedStr = new StringBuilder(str);
		// generate positions to change:
		Set<Integer> indices = new HashSet<>();
		while (indices.size() < toChange) {
			indices.add(r.nextInt(len)); // using set to avoid duplicated indices 
		}
		
		for (int i : indices) {
			// change the bit with the probability of 1/2
			if (r.nextInt(2) == 0) {
				if (changedStr.charAt(i) == '0') {
					changedStr.setCharAt(i, '1');
				} else {
					changedStr.setCharAt(i, '0');
				}
			}
		}
		
		return changedStr.toString();
	}

}
