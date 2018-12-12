public class Main {

    // -------- parameters -------------
    private static Point a = new Point(0.0, f(0.0)); // left border point
    private static Point b = new Point(Math.PI, f(Math.PI)); // right border point

    private static int xSectors = 2; // the amount of sectors for x coordinate (approximation will be more accurate)
    private static int ySectors = 2;

    private static double ymax = 5;
    private static double ymin = -5;

    private static double f(double x) { // function to approximate
        return Math.sin(x);
    }

    private static double F(double x) { // depends on f!!!
        return -Math.cos(x);
    }

    // ----------parameters end -----------



    private static double xSectorLength = Math.abs(b.getX() - a.getX()) / xSectors;
    private static double ySectorLength = Math.abs(ymax - ymin) / ySectors;

    private static double integral(double x1, double x2) { // the area under the f from x1 to x2
        return Math.abs(F(x2) - F(x1));
    }

    /*
     * the area under the line from p1 to p2
     */
    private static double intergralOfLine(Point p1, Point p2) {

        // line function:
        // y = ((x - x2)* (y2-y1)/(x2-x1)) + y2

        double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());

        // y = kx - kx2 + y2

        double b = p2.getY() - k * p2.getX();

        // y = kx + b
        // integral is (k * x * x /2.0 + b * x)

        return Math.abs(
                (k * p2.getX() * p2.getX() /2.0 + b * p2.getX())
                    - (k * p1.getX() * p1.getX() /2.0 + b * p1.getX())
        );
    }

    /*
     * the value we want - the difference between the line and the function
     * line from (x1;y1) to (x2;y2), function from x1 to x2
     */
    private static double difference(Point p1, Point p2) {
        return Math.abs(integral(p1.getX(), p2.getX()) - intergralOfLine(p1, p2));
    }

    /*
     * the approximate line cant be vertical, because there are no vertical functions
     */
    private static Node[][] getNodesArray() {
        Node[][] arr = new Node [xSectors] [ySectors + 1];
        for(int i = 1; i < xSectors; i++) { // x
            for (int j = 0; j <= ySectors; j++) { // y
                arr [i - 1][j] = new Node(
                        new Point(a.getX() + i * xSectorLength, ymin + j * ySectorLength),
                        Double.MAX_VALUE,
                        new Point()
                );
            }
        }
        arr[xSectors -1][0] = new Node(b, Double.MAX_VALUE, new Point()); // the point b
        return arr;
    }

    public static void main (String args[]) {
        // we start at the a
        Node[][] nodes = getNodesArray();
    }
}
