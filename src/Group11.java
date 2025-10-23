
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

public class Group11 {
	
	// You may add variables here, as long as the total memory is constant and no more than 
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
		
		// If you changed the return type of 'sort', you may change this line accordingly
		
		sort(toSort,target); // we call the sorting method once for JVM warmup (JVM optimizations)

		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		// If you changed the return type of 'sort', you may change this line accordingly
        BiggerInteger[] results = sort(toSort,target); // sort again, using JVM optimizations that occurred in the warmup sorting

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		// If you returned a different type of an array from sort, you may 
		// pass this array to the writeOutResult method
		writeOutResult(results, outFileName); // write out the results

	}

	private static ArrayList<String> readData(String inputFileName) throws FileNotFoundException {
		// You are allowed to collect a constant amount of information as you are reading
		// the data. The variables needs to be allocated for it before you start reading. 
		
		ArrayList<String> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			input.add(in.next());
		}

		in.close();
		
		// returns the input as an ArrayList. The last element is the target string
		return input;
	}

    public record BiggerInteger(long left, long right, String origin) implements Comparable<BiggerInteger> {
        public BiggerInteger(String origin) {
            this(origin.length() > 64 ? Long.parseUnsignedLong(origin.substring(0, origin.length() - 64), 2) : 0, origin.length() > 64 ? Long.parseUnsignedLong(origin.substring(origin.length() - 64), 2) : Long.parseUnsignedLong(origin, 2), origin);
        }

        @Override
        public String toString() {
            return this.origin;
        }

        public int bitCount(BiggerInteger other) {
            return Long.bitCount(other.left ^ left) + Long.bitCount(other.right ^ right);
        }

        @Override
        public int compareTo(BiggerInteger other) {
            if (this.left == other.left) {
                return Long.compareUnsigned(this.right, other.right);
            }
            return Long.compareUnsigned(this.left, other.left);
        }
    }

    // YOUR SORTING METHOD GOES HERE.
    // You may call other methods and use other classes.
    // Note: you may change the return type of the method.
    // You would need to provide your own function that prints your sorted array to
    // a file in the exact same format that my program outputs

    public static BiggerInteger[] sort(String[] toSort, String target) {
        // This, and all the methods/objects used here, can be changed/removed as you want.
        // You may modify the comparator, or not use it at all.
        // You may change the type of elements you are sorting and return an array of different
        // type, as long as it has the same elements (just stored differently) in the same order
        // as the sorted array of strings.

        // If you are creating or modifying any global variables, you need to reset them
        // to the original state after your sorting is done.

        // This sorting gives you the correct sorted order:

        //Initial data
        BiggerInteger[] target_data = new BiggerInteger[toSort.length];
        //Where we will place the finished data and return
        BiggerInteger[] scratch = new BiggerInteger[toSort.length];


        int[] bins = new int[target.length() + 1];
        int[] offsets = new int[target.length() + 1];

        var tl = new BiggerInteger(target);
        final int bit_width = toSort[0].length();

        for (int i = 0; i < toSort.length; i++) {
            target_data[i] = new BiggerInteger(toSort[i]);
            bins[target_data[i].bitCount(tl)]++;
        }

        for (int i = 1; i < bins.length; i++) {
            offsets[i] = offsets[i - 1] + bins[i - 1];
        }


        for (var dh : target_data) {
            var bc = dh.bitCount(tl);
            scratch[offsets[bc]++] = dh;
        }

        for (int i = 0; i < bins.length; i++) {
            if (bins[i] == 0) {
                continue;
            }
            if (bins[i] == 1) {
                target_data[offsets[i] - bins[i]] = scratch[offsets[i] - bins[i]];
                continue;
            }
            int start = offsets[i] - bins[i];
            int end = offsets[i];
            Arrays.sort(scratch, start, end, BiggerInteger::compareTo);
        }
        return scratch;

    }



	private static void writeOutResult(BiggerInteger[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (BiggerInteger d : sorted) {
			out.println(d);
		}
		out.close();
	}

}
