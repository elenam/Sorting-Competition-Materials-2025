import java.util.ArrayList;
import java.util.Arrays;

public class LongAndString implements Comparable<LongAndString> {
    long l;
    String raw;

    @Override
    public int compareTo(LongAndString o) {
        return Long.compareUnsigned(l, o.l);
    }

    LongAndString(long l, String raw) {
        this.l = l;
        this.raw = raw;
    }

    static void shortSort(String[] toSort, String target) {

        int width = target.length();
        LongAndString convertedTarget = BinaryStringToLong(target);

        @SuppressWarnings("unchecked")
        ArrayList<LongAndString>[] array = new ArrayList[width + 1];
        Arrays.setAll(array, i -> new ArrayList<LongAndString>(512));
        Arrays.stream(toSort).map(raw -> BinaryStringToLong(raw))
                .forEach(a -> array[Long.bitCount(a.l ^ convertedTarget.l)].add(a));
        int index = 0;
        // in each arraylist, sort the elements (null means use compareTo of elements)
        // then put the values of the whole array (as a string) in its spot.

        for (int i = 0; i < width + 1; i++) {
            array[i].sort(null);
            for (int j = 0; j < array[i].size(); j++) {
                toSort[index++] = array[i].get(j).raw;
            }
        }
    }

    private static LongAndString BinaryStringToLong(String raw) {
        long val = 0L;
        int width = raw.length();
        for (int i = 0; i < width; i++) {
            val = (val << 1) | (raw.charAt(i) & 1);
        }
        return new LongAndString(val, raw);
    }
}