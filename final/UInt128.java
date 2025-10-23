import java.util.ArrayList;
import java.util.Arrays;

public class UInt128 implements Comparable<UInt128> {
    long hi; // big part of the number 64 bit
    long lo; // small part of the number
    String raw; // e.g. "0101000010101010"

    public UInt128(long h, long l, String r) {
        hi = h;
        lo = l;
        raw = r;
    }

    @Override
    public int compareTo(UInt128 o) {
        int cmp = Long.compareUnsigned(hi, o.hi);
        return cmp != 0 ? cmp : Long.compareUnsigned(lo, o.lo);
    }

    static UInt128 parseBinary128(String raw) {
        // char array is faster because of no range checks in access.
        // "0101" -> ['0', '1', '0', '1']
        char[] bits = raw.toCharArray(); 
        int len = bits.length;
        int split = len - 64;

        long hi = 0L;
        long lo = 0L;

        // Parse high bits
        for (int i = 0; i < split; i++) {
            hi = (hi << 1) | (bits[i] & 1);
        }

        // Parse low bits
        for (int i = split; i < len; i++) {
            lo = (lo << 1) | (bits[i] & 1);
        }

        return new UInt128(hi, lo, raw);
    }

    public static void sort(String[] toSort, String target) {
        int n = toSort.length; // e.g. 7 million
        int width = target.length(); // e.g. 100

        @SuppressWarnings("unchecked")
        ArrayList<UInt128>[] array = new ArrayList[width + 1];

        // convert target into data type
        UInt128 convertedTarget = parseBinary128(target);

        Arrays.setAll(array, i -> new ArrayList<>(512));

        // the bitcount of XOR is the "distance" from target,
        // so for each value put it in arrayList dedicated for that distance
        for (int i = 0; i < n; i++) {
            UInt128 a = parseBinary128(toSort[i]);
            array[Long.bitCount(a.hi ^ convertedTarget.hi) + Long.bitCount(a.lo ^ convertedTarget.lo)].add(a);
        }
        
        // Find the first non-empty bucket
        int minIndex = 0;
        while (minIndex < array.length && array[minIndex].isEmpty())
        minIndex++;
        
        // Find last non-empty bucket
        int maxIndex = array.length - 1;
        while (maxIndex >= 0 && array[maxIndex].isEmpty())
        maxIndex--;

        int putBackAfterSortIndex = 0;
        // in each arraylist, sort the elements (null means use compareTo of elements)
        // then put the values of the whole array (as a string) in its spot.
        for (int i = minIndex; i < maxIndex; i++) {
            array[i].sort(null);
            for (int j = 0; j < array[i].size(); j++) {
                toSort[putBackAfterSortIndex++] = array[i].get(j).raw;
            }
        }
    }
}