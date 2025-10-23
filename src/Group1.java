// Haider Merchant and Abhineswari Seelam.

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Group1 {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Please run with input and output file names.");
            System.exit(0);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        String[] data;
        String target;
        {
            Scanner sc = new Scanner(new File(inputFile));
            int n = 0;
            while (sc.hasNext()) { sc.next(); n++; }
            sc.close();

            Scanner sc2 = new Scanner(new File(inputFile));
            data = new String[n - 1];
            for (int i = 0; i < n - 1; i++) {
                data[i] = sc2.next();
            }
            target = sc2.next();
            sc2.close();
        }

        String[] warmup = data.clone();
        sortStrings(warmup, target);

        Thread.sleep(10);

        String[] toSort = data.clone();
        long start = System.currentTimeMillis();
        sortStrings(toSort, target);
        long end = System.currentTimeMillis();

        System.out.println(end - start);

        try (PrintWriter out = new PrintWriter(outputFile)) {
            for (String s : toSort) {
                out.println(s);
            }
        }
    }

    private static void sortStrings(String[] data, String target) {
        int n = data.length;
        int len = target.length();

        int[] dist = new int[n];
        int maxDist = 0;
        for (int i = 0; i < n; i++) {
            dist[i] = hammingDistance(data[i], target);
            if (dist[i] > maxDist) maxDist = dist[i];
        }

        int[] count = new int[maxDist + 2];
        for (int d : dist) count[d + 1]++;

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            temp[count[dist[i]]++] = data[i];
        }

        int start = 0;
        for (int d = 0; d <= maxDist; d++) {
            int end = count[d + 1];
            if (start < end) {
                radixSortBinary(temp, start, end, len);
            }
            start = end;
        }

        System.arraycopy(temp, 0, data, 0, n);
    }

    private static int hammingDistance(String a, String b) {
        int d = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) d++;
        }
        return d;
    }

    private static void radixSortBinary(String[] arr, int start, int end, int len) {
        String[] aux = new String[end - start];
        for (int bit = len - 1; bit >= 0; bit--) {
            int zeroCount = 0;
            for (int i = start; i < end; i++) {
                if (arr[i].charAt(bit) == '0') zeroCount++;
            }

            int z = 0, o = zeroCount;
            for (int i = start; i < end; i++) {
                if (arr[i].charAt(bit) == '0') aux[z++] = arr[i];
                else aux[o++] = arr[i];
            }

            for (int i = 0; i < aux.length; i++) {
                arr[start + i] = aux[i];
            }
        }
    }
}