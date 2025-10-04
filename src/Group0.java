
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group0 {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		if (args.length < 2) {
			System.out.println("Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];

		// Uncomment to test comparator methods
		SortingCompetitionComparator.runComparatorTests();

		ArrayList<String> input = readData(inputFileName); // includes the target string
		// move the target string out of data into a variable:
		String target = input.get(input.size() - 1);
		input.remove(input.size() - 1);
		
		String[] data = input.toArray(new String[0]); // read data as strings
		
		System.out.println("The target string is: " + target);

		String[] toSort = data.clone(); // clone the data

		sort(toSort,target); // call the sorting method once for JVM warmup

		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		sort(toSort,target); // sort again

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		writeOutResult(toSort, outFileName); // write out the results

	}

	private static ArrayList<String> readData(String inputFileName) throws FileNotFoundException {
		ArrayList<String> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			input.add(in.next());
		}

		in.close();
		
		// returns the input as an ArrayList. The last element is the target string
		return input;
	}

	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs

	private static void sort(String[] toSort, String target) {
		Arrays.sort(toSort, new SortingCompetitionComparator(target));
	}

	private static class SortingCompetitionComparator implements Comparator<String> {
		private String target;
		
		public SortingCompetitionComparator(String target) {
			this.target = target;
		}

		@Override
		public int compare(String s1, String s2) {
			// first criterion: the number of bits different from the target string:
			int count1 = distanceToTarget(s1);
			
			int count2 = distanceToTarget(s2);
			
			// second criterion: the value of the binary numbers in the strings:
			BigInteger n1 = new BigInteger(s1, 2); // converting a binary number into a BigInteger
			BigInteger n2 = new BigInteger(s2, 2); // converting a binary number into a BigInteger
			
			if (count1 - count2 != 0) { // if the difference from the target string aren't the same 
				return (count1 - count2); // return a number < 0 if the first string is closer, > 0 if the second one is
			}
			
			// if the two counts are the same
			return n1.compareTo(n2);

		}
		
		private int distanceToTarget(String str) {
			int count = 0;
			
			// Finding the difference for s1
			for (int i = 0; i < target.length(); ++i) {
				if (target.charAt(i) != str.charAt(i)) {
					count++;
				}
			}
			
			return count;
		}
		
		private static void runComparatorTests() {
			// creating an instance of a comparator with a mock target string:
			SortingCompetitionComparator strComp = new SortingCompetitionComparator("0000000000");
			
			
			// Testing distance to target
			System.out.println("distanceToTarget(\"1010101010\") = " + strComp.distanceToTarget("1010101010")); // should be 5
			System.out.println("distanceToTarget(\"1111101111\") = " + strComp.distanceToTarget("1111101111")); // should be 9
			
			// Testing the comparator:
			System.out.println(strComp.compare("1010000000","0010001000")); // same distance from target string, 1st string larger
					
		}
	}

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}

}
