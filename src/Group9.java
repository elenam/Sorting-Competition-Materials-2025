import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group9 {

	// You may add variables here, as long as the total memory is constant and no
	// more than
	// 1000 * string length.

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		// You may not change anything in main, except the lines that are specifically
		// commented allowing certain changes.

		if (args.length < 2) {
			System.out.println("Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];

		// Uncomment to test comparator methods
		// SortingCompetitionComparator.runComparatorTests();

		ArrayList<String> input = readData(inputFileName); // includes the target string
		// move the target string out of data into a variable:
		String target = input.get(input.size() - 1);
		input.remove(input.size() - 1);

		String[] data = input.toArray(new String[0]); // read data as strings

		// Test print
		// System.out.println("The target string is: " + target);

		String[] toSort = data.clone(); // clone the data

		// If you changed the return type of 'sort', you may change this line
		// accordingly

		sort(toSort, target); // we call the sorting method once for JVM warmup (JVM optimizations)

		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		// If you changed the return type of 'sort', you may change this line
		// accordingly
		sort(toSort, target); // sort again, using JVM optimizations that occurred in the warmup sorting

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		// If you returned a different type of an array from sort, you may
		// pass this array to the writeOutResult method
		writeOutResult(toSort, outFileName); // write out the results

	}

	private static ArrayList<String> readData(String inputFileName) throws FileNotFoundException {
		// You are allowed to collect a constant amount of information as you are
		// reading
		// the data. The variables needs to be allocated for it before you start
		// reading.

		ArrayList<String> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			input.add(in.next());
		}

		in.close();

		// returns the input as an ArrayList. The last element is the target string
		return input;
	}

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}
	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs

	private static void sort(String[] toSort, String target) {
		if (target.length() > 64)
			UInt128.sort(toSort, target);
		else
			LongAndString.shortSort(toSort, target);
	}
}
