import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;


// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group8 {

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




	/////////////////////////////////////////////////////////////////////////////////////////////////

	private static void sort(String[] toSort, String target) {
		int n = toSort.length;
		int k = target.length();

		// Array of ArrayLists, because we know that we will have at most k arrays, but
		// we need dynamic sizing for buckets.

		@SuppressWarnings("unchecked")
		ArrayList<String>[] count = new ArrayList[k];
		for (int i = 0; i < k; i++) {
			count[i] = new ArrayList<>();
		}

		// check distance to target string for every binary, and put them in the
		// arraylist for each distance

		for (int i = 0; i < n; i++) {
			count[distanceToTarget(toSort[i], target)].add(toSort[i]);
		}


		// sorting by value for each set of same-distance strings, ignores empty or size
		// 1 arrays, as they don't need to be sorted
		// then adding them to toSort in the same step
		
		int index = 0;
		for (int i = 0; i < k; i++) {
			if (count[i].isEmpty()) continue;

			if (count[i].size() == 1) {
				// no need to sort
			} else if (count[i].size() <= 24) {
				insertionSort(count[i]);
			} else {
				Collections.sort(count[i], new SortingSizeComparator());
			}

			// Write back into the main array to avoid extra memory allocation
			for (String s : count[i]) {
				toSort[index++] = s;
			}

			// Free bucket memory immediately
			count[i].clear();
		}
	}

	public static void insertionSort(ArrayList<String> array) {
    	int i, j;
    	for (i = 1; i < array.size(); i++) {
        	String tmp = array.get(i);
    	    j = i;
 	        while ((j > 0) && (binaryComp(array.get(j - 1), tmp) > 0)) {
            	array.set(j, array.get(j - 1));
        	    j--;
        	}
        	array.set(j, tmp);
    	}
	}

	private static class SortingSizeComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return binaryComp(s1, s2);
		}
	}





	public static int distanceToTarget(String str, String target) {
		int i = 0, count = 0;

		while (i < str.length()) {
			if (str.charAt(i) != target.charAt(i))
				count++;
			i++;
		}

		return count;
	}





	// faster method of comparing binary values
	public static int binaryComp(String s1, String s2) {
		int i = 0;

		while (i < s1.length() - 1) {
			if (s1.charAt(i) != s2.charAt(i)) {
				break;
			}
			i++;
		}

		return (s1.charAt(i) - s2.charAt(i));
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}
}

// updated case
// java Group0.java test_data.txt results0.txt
// java Group8Bucket.java test_data.txt results8Bucket.txt
// java DataGenerator.java test_data.txt 120 50000
// java Check.java results0.txt results8Bucket.txt

// used this to calculate the distance between 2 strings:
// https://en.wikipedia.org/wiki/Hamming_distance
// https://www.geeksforgeeks.org/dsa/hamming-distance-two-strings/

// sort is a VERY heavily modified version of this code:
// https://www.geeksforgeeks.org/dsa/sort-array-according-count-set-bits/