package gc;

public class GCDemo {

    private final static long KB = 1000;
    private final static long MB = KB * KB;
    private final static Runtime ENVIRONMENT = Runtime.getRuntime();

    public static void info() {
        final long freeMemory = ENVIRONMENT.freeMemory();
        final long totalMemory = ENVIRONMENT.totalMemory();
        final long maxMemory = ENVIRONMENT.maxMemory();
        System.out.println("=== Environment state ===");
        System.out.printf("Free - %d%n", freeMemory / MB);
        System.out.printf("Total - %d%n", totalMemory / MB);
        System.out.printf("maxMemory - %d%n", maxMemory / MB);
    }

    public static void main(String[] args) {
        info();
        for (int i = 0; i < 10000; i++) {
            new Person(i, "N" + i);
        }
        System.gc();
        info();
    }
}
