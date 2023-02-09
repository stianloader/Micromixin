package de.geolykt.starloader.micromixin.test.j8.selector;

public class NumberSelectorTest {

    public static void printNumbers() {
        System.out.println("START OF TEXT.");
        System.out.println("5");
        System.out.println("2");
        System.out.println(5);
        System.out.println(2);
        System.out.println(5L);
        System.out.println(2L);
        System.out.println(5D);
        System.out.println(2D);
        System.out.println(5F);
        System.out.println(2F);
        System.out.println("END OF TEXT.");
    }
}
