import java.util.Arrays;
//Program to perform bubblesort
public class task2 {
    /**
     *
     * @author Sebastian Sjöberg
     */
    public static void main(String[] args){
      test();
    }
    public static void test()
    {
        int[][] arrays = new int[][]{{1,2,3},{0,0,0},{},{3,2,1,2,3}};
        for(int[] array : arrays)
        {
            bubblesort(array);
            System.out.println(Arrays.toString(array));
        }
    }
    public static void bubblesort(int[] array)
    {
        boolean cond = true;
        while (cond){
            cond = bytintill(array);
        }

    }
    public static boolean bytintill(int[] array) {
        boolean cond = false;
        for (int i = 0; i < array.length-1; i = i + 1) {
          if(array[i]>array[i+1]){
              int temp = array[i];
              array[i] = array[i+1];
              array[i+1] = temp;
              cond = true;
          }
        }
        return cond;

    }
}
