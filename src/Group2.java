import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//Group 2 Mahathir and Blake

//NOTE: Used Co-pilot to write comments explaining bits of the code so its nicer to read.


//Our improved sorting methods:

// 1. precompute distances efficiently
// 2. use counting sort by distance
// 3. replace BigInteger with faster binary comparison
// 4. tuned for dataset characteristics

public class Group2 {
	
	// Global variables used by the sorting routine.
	// These are reset each time `sort` is called so we avoid
	// reallocating large arrays repeatedly during a single run.
	private static int[] distances;   // Hamming distance from each string to target
	private static String target;     // the target string we're comparing against
	private static int stringLength;  // cached length of strings (all the same length)
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		if (args.length < 2) {
			System.out.println("Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];

	// Read the file: all lines are binary strings, last line is the target.
	ArrayList<String> input = readData(inputFileName);
	// The competition format puts the 'target' as the last element in the file.
	String target = input.get(input.size() - 1);
	// Remove the target from the list of strings we need to sort.
	input.remove(input.size() - 1);
		
		String[] data = input.toArray(new String[0]);
		
		//System.out.println("The target string is: " + target);    		// Test print 


	// Make a copy for the warmup run. The JVM sometimes benefits from
	// a first, untimed call to allow JIT optimizations to kick in.
	String[] toSort = data.clone();
	sort(toSort, target); // warmup (not timed)

		toSort = data.clone();

		Thread.sleep(10);

		long start = System.currentTimeMillis();

		sort(toSort, target);

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		// write the final, sorted result to the output file
		writeOutResult(toSort, outFileName);
	}

	private static ArrayList<String> readData(String inputFileName) throws FileNotFoundException {
		ArrayList<String> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			input.add(in.next());
		}

		in.close();
		
		return input;
	}

	private static void sort(String[] toSort, String target) {
		// Initialize shared state for this sorting operation.
		// We intentionally use class-level variables to avoid passing large
		// structures through method parameters and to reduce repeated allocations.
		Group2.target = target;
		Group2.stringLength = target.length();
		Group2.distances = new int[toSort.length];

		// First, compute the Hamming distance of every string to the target. This is the expensive part we'll reuse during sorting.
		precomputeDistances(toSort);

		// Perform a stable-ish sort by distance (counting sort), then break ties
		// within each distance bucket by the binary value of the string.
		countingSort(toSort);

		// Clear references to help GC and avoid accidental reuse.
		Group2.target = null;
		Group2.distances = null;
	}
	
	private static void precomputeDistances(String[] toSort) {

		// If the strings fit within 64 bits we use a fast path: parse once to
		// a long and use XOR + bitCount to compute Hamming distance.
		if (stringLength <= 64) {
			long targetLong = parseBinaryToLong(target);
			for (int i = 0; i < toSort.length; i++) {
				long stringLong = parseBinaryToLong(toSort[i]);
				// XOR flips bits that differ; popcount gives the number of differing bits
				distances[i] = Long.bitCount(targetLong ^ stringLong);
			}
		} else {
			// For longer strings we parse into arrays of longs and sum the
			// bit counts across 64-bit chunks.
			precomputeDistancesLong(toSort);
		}
	}
	
	private static void precomputeDistancesLong(String[] toSort) {
		// Handle strings longer than 64 bits by dividing them into 64-bit chunks.
		// We align bits to the left when packing into a long so that comparisons
		// remain consistent with lexicographic ordering of the strings.
		int chunks = (stringLength + 63) / 64;
		long[] targetChunks = parseBinaryToLongArray(target);

		for (int i = 0; i < toSort.length; i++) {
			long[] stringChunks = parseBinaryToLongArray(toSort[i]);
			int distance = 0;
			for (int chunk = 0; chunk < chunks; chunk++) {
				distance += Long.bitCount(targetChunks[chunk] ^ stringChunks[chunk]);
			}
			distances[i] = distance;
		}
	}
	
	private static long[] parseBinaryToLongArray(String binary) {
		// Pack the binary string into a sequence of longs. Each long stores up to
		// 64 bits. We left-align the meaningful bits inside each long so that
		// leading zeros in the last chunk don't change comparisons.
		int chunks = (binary.length() + 63) / 64;
		long[] result = new long[chunks];

		for (int chunk = 0; chunk < chunks; chunk++) {
			int start = chunk * 64;
			int end = Math.min(start + 64, binary.length());
			int length = end - start;

			long value = 0;
			for (int i = 0; i < length; i++) {
				value = (value << 1) | (binary.charAt(start + i) - '0');
			}
			// Shift the packed bits up to the high side of the 64-bit word. This keeps the relative order consistent when comparing long arrays.
			value <<= (64 - length);
			result[chunk] = value;
		}

		return result;
	}
	
	private static long parseBinaryToLong(String binary) {
		// Parse a binary string (length <= 64) into a long. The leftmost bit in the string becomes the most significant bit in the long.
		long result = 0;
		for (int i = 0; i < binary.length(); i++) {
			result = (result << 1) | (binary.charAt(i) - '0');
		}
		return result;
	}
	
	private static void countingSort(String[] toSort) {
		// Perform counting sort by precomputed Hamming distance. Counting sort is linear in n + range(distance).
		int maxDistance = 0;
		for (int distance : distances) {
			maxDistance = Math.max(maxDistance, distance);
		}

		int[] count = new int[maxDistance + 1];
		for (int distance : distances) {
			count[distance]++;
		}

		// position[d] will be theg starting index in the output array for items with distance == d.
		int[] position = new int[maxDistance + 1];
		int currentPos = 0;
		for (int i = 0; i <= maxDistance; i++) {
			position[i] = currentPos;
			currentPos += count[i];
		}

		// Scatter into a temporary array according to positions we computed.
		String[] temp = new String[toSort.length];
		int[] tempDistances = new int[toSort.length];

		for (int i = 0; i < toSort.length; i++) {
			int distance = distances[i];
			int pos = position[distance]++;
			temp[pos] = toSort[i];
			tempDistances[pos] = distance; // kept only for clarity/debugging
		}

		// For each bucket of equal distance, sort by the binary value of the string to produce a deterministic total order. We use quicksort on each bucket.
		int start = 0;
		for (int distance = 0; distance <= maxDistance; distance++) {
			int end = start + count[distance];
			if (end > start) {
				// sort the bucket in-place in `temp` from start..end-1
				quickSortByBinaryValue(temp, start, end - 1);
				start = end;
			}
		}

		// Copy the sorted result back into the original array.
		System.arraycopy(temp, 0, toSort, 0, toSort.length);
	}
	
	private static void quickSortByBinaryValue(String[] arr, int low, int high) {
		if (low < high) {
			int pivotIndex = partitionByBinaryValue(arr, low, high);
			quickSortByBinaryValue(arr, low, pivotIndex - 1);
			quickSortByBinaryValue(arr, pivotIndex + 1, high);
		}
	}
	
	private static int partitionByBinaryValue(String[] arr, int low, int high) {
		String pivot = arr[high];
		int i = low - 1;
		
		for (int j = low; j < high; j++) {
			if (compareBinaryValues(arr[j], pivot) <= 0) {
				i++;
				swap(arr, i, j);
			}
		}
		
		swap(arr, i + 1, high);
		return i + 1;
	}
	
	private static void swap(String[] arr, int i, int j) {
		String temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	private static int compareBinaryValues(String s1, String s2) {

		// Compare two binary strings lexicographically. Since characters '0' and '1' compare in their ASCII order, a simple char compare works fine.
		// We keep this simple for speed and clarity.
		for (int i = 0; i < s1.length(); i++) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return 0;
	}

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}
}
