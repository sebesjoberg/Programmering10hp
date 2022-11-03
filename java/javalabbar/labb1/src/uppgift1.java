//program to calcualte the number sum

public class uppgift1
{
    public static void main(String[] args)
    {
      test();
    }
    public static void test()
    {
        int[] numbs = {121, 122};
        for (int numb : numbs) {
            int sum = siffsum(numb);
            System.out.println(sum);
        }
    }
public static int siffsum(int numb)
    {
       int sum = 0;
        while (numb > 0){
            sum = sum + numb % 10;
            numb = Math.floorDiv(numb,10);

    }
    return sum;
    }
}
