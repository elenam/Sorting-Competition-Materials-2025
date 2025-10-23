
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group6 {
	
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
		sort(toSort,target); // sort again, using JVM optimizations that occurred in the warmup sorting

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		// If you returned a different type of an array from sort, you may 
		// pass this array to the writeOutResult method
		writeOutResult(toSort, outFileName); // write out the results

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

	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs

	/* 
	 GROUP 6 Jakob&John ALGORITHM: 
	 Thinking about using a modified quick sort algorithm, it follows 
	 assignment constraints on memory cap. Complexity for average case is
	 O(n log n) which will be effective and fast in sorting.

	 Would like names to appear like: Jakob and John
	*/
	
	private static void sort(String[] toSort, String target) {
		// This, and all the methods/objects used here, can be changed/removed as you want. 
		// You may modify the comparator, or not use it at all.
		// You may change the type of elements you are sorting and return an array of different
		// type, as long as it has the same elements (just stored differently) in the same order
		// as the sorted array of strings. 
		
		// If you are creating or modifying any global variables, you need to reset them 
		// to the original state after your sorting is done.  
		
		// Use an in-place quicksort with the competition comparator.
		if (toSort == null || toSort.length <= 1) return;
		SortingCompetitionComparator comp = new SortingCompetitionComparator(target);
		quicksort(toSort, 0, toSort.length - 1, comp);
	}
    
	// Dual-pivot quicksort helpers (using two pivots -> three pivots)
	private static final int INSERTION_SORT_THRESHOLD = 16;

	private static void quicksort(String[] arr, int low, int high, SortingCompetitionComparator comp) {
		while (low < high) {
			int len = high - low + 1;
			// Use insertion sort for small pivots
			if (len <= INSERTION_SORT_THRESHOLD) {
				insertionSort(arr, low, high, comp);
				return;
			}

			// median-of-three to choose better pivots: pick low, mid, high
			int mid = low + (high - low) / 2;
			// Order low, mid, high
			if (comp.compare(arr[low], arr[mid]) > 0) swap(arr, low, mid);
			if (comp.compare(arr[low], arr[high]) > 0) swap(arr, low, high);
			if (comp.compare(arr[mid], arr[high]) > 0) swap(arr, mid, high);

			// Use arr[low] and arr[high] as pivots 
			if (comp.compare(arr[low], arr[high]) > 0) swap(arr, low, high);

			String pivot1 = arr[low];
			String pivot2 = arr[high];

			int lt = low + 1; // boundary for elements < pivot1
			int gt = high - 1; // boundary for elements > pivot2
			int i = low + 1;

			while (i <= gt) {
				int cmpToP1 = comp.compare(arr[i], pivot1);
				if (cmpToP1 < 0) {
					swap(arr, i, lt);
					lt++;
					i++;
				} else {
					int cmpToP2 = comp.compare(arr[i], pivot2);
					if (cmpToP2 > 0) {
						swap(arr, i, gt);
						gt--;
					} else {
						i++;
					}
				}
			}

			// place pivots to their correct positions
			swap(arr, low, lt - 1);
			swap(arr, high, gt + 1);

			int p1 = lt - 1;
			int p2 = gt + 1;

			// Recurse on smaller pivots first to limit stack depth
			int leftSize = p1 - 1 - low;
			int middleSize = p2 - 1 - (p1 + 1) + 1; // p2 - p1 - 1
			int rightSize = high - (p2 + 1) + 1;

			// sort three pivots: [low, p1-1], [p1+1, p2-1], [p2+1, high]
			// Recurse on smallest, loop on the largest to reduce recursion depth
			if (leftSize <= middleSize && leftSize <= rightSize) {
				quicksort(arr, low, p1 - 1, comp);
				if (middleSize <= rightSize) {
					quicksort(arr, p1 + 1, p2 - 1, comp);
					low = p2 + 1;
				} else {
					quicksort(arr, p2 + 1, high, comp);
					low = p1 + 1;
					high = p2 - 1;
				}
			} else if (middleSize <= leftSize && middleSize <= rightSize) {
				quicksort(arr, p1 + 1, p2 - 1, comp);
				if (leftSize <= rightSize) {
					quicksort(arr, low, p1 - 1, comp);
					low = p2 + 1;
				} else {
					quicksort(arr, p2 + 1, high, comp);
					high = p1 - 1;
				}
			} else {
				quicksort(arr, p2 + 1, high, comp);
				if (leftSize <= middleSize) {
					quicksort(arr, low, p1 - 1, comp);
					low = p1 + 1;
					high = p2 - 1;
				} else {
					quicksort(arr, p1 + 1, p2 - 1, comp);
					high = p1 - 1;
				}
			}
		}
	}

	private static void insertionSort(String[] arr, int low, int high, SortingCompetitionComparator comp) {
		for (int i = low + 1; i <= high; i++) {
			String key = arr[i];
			int j = i - 1;
			while (j >= low && comp.compare(arr[j], key) > 0) {
				arr[j + 1] = arr[j];
				j--;
			}
			arr[j + 1] = key;
		}
	}

	private static void swap(String[] arr, int i, int j) {
		if (i == j) return;
		String tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
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
			
			// second criterion: lexicographic compare of binary strings (same length)
			if (count1 - count2 != 0) { // if the difference from the target string aren't the same 
				return (count1 - count2); // return a number < 0 if the first string is closer, > 0 if the second one is
			}

			// if the two counts are the same, lexicographic compare equals numeric compare for equal-length binary strings
			return s1.compareTo(s2);

		}
		
		private int distanceToTarget(String str) {
			// It's ok to comment out this test during the competition,
			// it's for debugging purposes only. 
			if (str.length() != target.length()) {
				throw new RuntimeException("Comparing strings of different lengths");
			}
			
			int count = 0;
			
			// Finding the difference for s1
			for (int i = 0; i < target.length(); ++i) {
				if (target.charAt(i) != str.charAt(i)) {
					count++;
				}
			}
			
			return count;
		}
		
		// private static void runComparatorTests() {
		// 	// creating an instance of a comparator with a mock target string:
		// 	SortingCompetitionComparator strComp = new SortingCompetitionComparator("0000000000");
			
			
		// 	// Testing distance to target
		// 	System.out.println("distanceToTarget(\"1010101010\") = " + strComp.distanceToTarget("1010101010")); // should be 5
		// 	System.out.println("distanceToTarget(\"1111101111\") = " + strComp.distanceToTarget("1111101111")); // should be 9
			
		// 	// Testing the comparator:
		// 	System.out.println(strComp.compare("1010000000","0010101000")); // different distance from the target, 1st string smaller
		// 	System.out.println(strComp.compare("1010011001","0010101000")); // different distance from the target, 1st string larger
		// 	System.out.println(strComp.compare("1010000000","0010001000")); // same distance from target string, 1st string larger
		// 	System.out.println(strComp.compare("1010000000","1110001000")); // same distance from target string, 1st string smaller
		// 	System.out.println(strComp.compare("1010000000","1010000000")); // string equals to itself
			
		// 	// Testing real-test 100 character strings; all comparisons should come out negative
		// 	SortingCompetitionComparator strComp1 = new SortingCompetitionComparator("0110011111000000110010001001111100001100000100111101011101100110101000011010000100101010011011100110");
		// 	System.out.println(strComp1.compare("0100010011000001111100001011110100000100011101111001010100100110110010101011000101010010001111100100",
		// 			"0100010011000001111100001011110100000100011101111001010100100110110010101011000101010010001110100100"));
		// 	System.out.println(strComp1.compare("0010001101010101111111000011110101000100101100011101011111101001100011111000000000101010011010101000",
		// 			"0110011001010000100110111010110010000000011001111101010000010010101001011000000110110010011101101110"));
			
		// }
	}

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}

}
