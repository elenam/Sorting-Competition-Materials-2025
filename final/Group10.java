import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Group10 {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		if (args.length < 2) {
			System.out.println("Please run with two command line arguments: input and output file names");
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];

		ArrayList<String> input = readData(inputFileName);
		String target = input.get(input.size() - 1);
		input.remove(input.size() - 1);

		String[] data = input.toArray(new String[0]);

		String[] toSort = data.clone();

		sort(toSort, target); // JVM warmup

		toSort = data.clone();

		Thread.sleep(10);

		long start = System.currentTimeMillis();

		sort(toSort, target);

		long end = System.currentTimeMillis();

		System.out.println(end - start);

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

	private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {
			out.println(s);
		}
		out.close();
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	* Sort the array of strings using a comparator that uses buckets to keep track of Hamming distance
	* and Longs to store bit representations of the strings
	*
	* @param toSort
	*     the array of strings to sort
	* @param target
	*     the target string to compare Hamming distance against
	*
	* @author
	*     @emmahsax (Emma Sax)
	*/
	private static void sort(String[] toSort, String target) {
		int stringLength = target.length();
		int stringCount = toSort.length;

		// Convert target to char array for fast access
		char[] targetChars = target.toCharArray();

		// Create array of buckets, one for each possible Hamming distance value
		ArrayList<SortableElement>[] buckets = new ArrayList[stringLength + 1];

		// Split the string into two even parts for maximum efficiency
		int splitPoint = Math.max(0, stringLength - 64);

		// Go through each string to sort, calculate Hamming distance, convert to Long arrays, and place into
		// buckets based on Hamming distance
		for (int i = 0; i < stringCount; i++) {
			SortableElement elem = new SortableElement(toSort[i], targetChars, stringLength, splitPoint);
			int distance = elem.distance;

			if (buckets[distance] == null) {
				buckets[distance] = new ArrayList<>();
			}

			buckets[distance].add(elem);
		}

		int index = 0;

		// Sort interiors of each bucket and write back to original array
		for (int i = 0; i <= stringLength; i++) {
			if (buckets[i] != null) {
				ArrayList<SortableElement> bucket = buckets[i];

				// Sort the single bucket where each element has the same Hamming distance using comparator
				bucket.sort(null);

				// Write sorted elements back to original array
				for (SortableElement elem : bucket) {
					toSort[index++] = elem.original;
				}
			}
		}
	}

	/*
	* SortableElement class - stores string, Hamming distance, and bit representation as
	* two Longs (called chunks)
	*
	* @author
	*     @emmahsax (Emma Sax)
	*/
	private static final class SortableElement implements Comparable<SortableElement> {
		final String original;
		final int distance;
		final long firstChunk;
		final long lastChunk;

		/*
		* Constructor for SortableElement class
		*
		* @param original
		*     the original string
		* @param target
		*     the target string as a char array
		* @param stringLength
		*     the length of the strings
		* @param splitPoint
		*     the point to split the string into two chunks
		*
		* @author
		*     @emmahsax (Emma Sax)
		*/
		SortableElement(String original, char[] target, int stringLength, int splitPoint) {
			this.original = original;
			long firstChunkVal = 0L;
			long lastChunkVal = 0L;
			int distance = 0;

			// Calculate distance and parse bits
			for (int i = 0; i < stringLength; i++) {
				char c = original.charAt(i);
				char t = target[i];

				// Increase distance if the characters are different
				if (c != t) {
					distance++;
				}

				// Parse bit
				int bit = (c == '1') ? 1 : 0;

				// Use bitwise left shift and bitwise OR to add the incoming bit to the appropriate
				// chunk in the correct spot
				if (i < splitPoint) {
					firstChunkVal = (firstChunkVal << 1) | bit;
				} else {
					lastChunkVal = (lastChunkVal << 1) | bit;
				}
			}

			this.distance = distance;
			this.firstChunk = firstChunkVal;
			this.lastChunk = lastChunkVal;
		}

		/*
		* Compare two SortableElements based on their chunks
		*
		* @param other
		*     the other SortableElement to compare to
		*
		* @return
		*     an integer less than, equal to, or greater than zero if this object is less than,
		*     equal to, or greater than the parameter object
		*
		* @author
		*     @emmahsax (Emma Sax)
		*/
		@Override
		public int compareTo(SortableElement other) {
			// Compare chunks
			int comparison = Long.compareUnsigned(this.firstChunk, other.firstChunk);

			// If the first chunks are not equal, return the comparison
			if (comparison != 0) {
				return comparison;
			}

			// If the first chunks are equal, compare the last chunks and return the result
			return Long.compareUnsigned(this.lastChunk, other.lastChunk);
		}
	}
}
