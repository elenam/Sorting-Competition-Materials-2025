import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Group3 {
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
        sort(toSort, target); 

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
        while (in.hasNext()) input.add(in.next());
        in.close();
        return input;
    }
    private static class BinaryString {
        String value;
        int distance;
        long hi, lo;

        BinaryString(String value, String target) {
            this.value = value;
            this.distance = computeDistance(value, target);
            long[] bits = parseBits(value);
            this.hi = bits[0];
            this.lo = bits[1];
        }

        private int computeDistance(String a, String b) {
            int dist = 0;
            for (int i = 0; i < a.length(); i++)
                if (a.charAt(i) != b.charAt(i)) dist++;
            return dist;
        }

        private long[] parseBits(String s) {
            int L = s.length();
            long hi = 0, lo = 0;
            int split = Math.max(0, L - 64);
            for (int i = 0; i < L; i++) {
                int bit = s.charAt(i) == '1' ? 1 : 0;
                if (i < split) hi = (hi << 1) | bit;
                else lo = (lo << 1) | bit;
            }
            return new long[]{hi, lo};
        }

        public int compareNumeric(BinaryString other) {
            int cmp = Long.compareUnsigned(this.hi, other.hi);
            if (cmp != 0) return cmp;
            return Long.compareUnsigned(this.lo, other.lo);
        }
    }

    private static void sort(String[] toSort, String target) {
        int L = target.length();
        BinaryString[] wrapped = new BinaryString[toSort.length];
        for (int i = 0; i < toSort.length; i++) wrapped[i] = new BinaryString(toSort[i], target);

        // Create buckets for distances
        @SuppressWarnings("unchecked")
        ArrayList<BinaryString>[] buckets = new ArrayList[L + 1];
        for (BinaryString b : wrapped) {
            int d = b.distance;
            if (buckets[d] == null) buckets[d] = new ArrayList<>();
            buckets[d].add(b);
        }
        int pos = 0;
        for (int d = 0; d <= L; d++) {
            if (buckets[d] == null) continue;
            ArrayList<BinaryString> bucket = buckets[d];
            bucket.sort(BinaryString::compareNumeric);
            for (BinaryString b : bucket) toSort[pos++] = b.value;
        }
    }
    private static void writeOutResult(String[] sorted, String outputFilename)
            throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFilename);
        for (String s : sorted) out.println(s);
        out.close();
    }
}
