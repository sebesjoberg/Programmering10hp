public class labb2 {
    public static void main(String[] args)
    {
      //village village1 = new village(true);
      Village village = new Village(false,true);
      DisplayPandemic display = new DisplayPandemic(village);
      int sick = village.countSick();
      display.show();
        while(sick>0)
        {
            village.dayPassesAll();
            sick = village.countSick();
            System.out.println(sick);
            display.show();

        }
        System.out.println("finished");


    }
}
