import java.util.ArrayList;
import java.util.Random;

public class Main {

    // -------- parameters -------------
    private static final Point a = new Point(0.0, f(0.0)); // left border point
    private static final Point b = new Point(Math.PI, f(Math.PI)); // right border point

    private static final int xSectors = 60; // the amount of sectors for x coordinate (approximation will be more accurate)
    private static final int ySectors = 60;

    // the step to find the maximum diff in the diffModes.MAX_DIFF
    private static final double STEP = 0.001;

    private static final int amountOfPointsForSquares = 1000;

    private static final double ymax = 1.5;
    private static final double ymin = 0;

    private static final diffModes mode = diffModes.MAX_DIFF;

    private static double f(double x) { // function to approximate
        return Math.sin(x);
    }

    private static double integral(double x) { // depends on f!!! it is F
        return -Math.cos(x);
    }

    // ----------parameters end -----------



    private static final double xSectorLength = Math.abs(b.getX() - a.getX()) / xSectors;
    private static final double ySectorLength = Math.abs(ymax - ymin) / ySectors;

    private static Node[][] nodes;

    private enum diffModes {
        INTEGRAL, SUM_OF_DIFF_SQUARES, MAX_DIFF
    }

    private static final ArrayList <Double> pointsForSumOfDiffSquares = new ArrayList<>();
    static {
        Random r = new Random();
        double half = 0.5 * (b.getX() - a.getX());
        for(int i = 0; i < amountOfPointsForSquares; i++) {
            pointsForSumOfDiffSquares.add(
            r.nextGaussian() /* the num from -1 to 1 */ * half /*make it from - half to half*/
                    + (a.getX() + half) // the center between b and a
            );
        }
    }

    private static double integral(double x1, double x2) { // the area under the f from x1 to x2
        return integral(x2) - integral(x1);
    }

    /*
     * get k coefficient in y = kx + b line by two points
     */
    private static double getK(Point p1,Point p2) {
        // line function:
        // y = ((x - x2)* (y2-y1)/(x2-x1)) + y2 = (y2-y1)/(x2-x1) * x - (y2-y1)/(x2-x1) * x2 + y2
        return (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    }

    /*
     * get b coefficient in y = kx + b line by two points
     */
    private static double getB(Point p1, Point p2) {
        // y = kx - kx2 + y2 = kx + y2 - kx2
       return p2.getY() - getK(p1, p2) * p2.getX();
    }

    /*
     * get result value of g (line by two points) of x
     */
    private static double g(double x, Point p1, Point p2) {
        return getK(p1, p2) * x + getB(p1, p2);
    }

    /*
     * the area under the line from p1 to p2
     */
    private static double intergralOfLine(Point p1, Point p2) {
        double k = getK(p1,p2);
        double b = getB(p1, p2);

        // y = kx + b
        // integral is (k * x * x /2.0 + b * x)

        return (k * p2.getX() * p2.getX() /2.0 + b * p2.getX())
                    - (k * p1.getX() * p1.getX() /2.0 + b * p1.getX());
    }

    private static double getMaxDiff(Point p1, Point p2) {
        double result = 0;
        for (double i = p1.getX(); i < p2.getX(); i += STEP) {
            double diffAtI = Math.abs(f(i) - g(i, p1, p2));
            result = Math.max(diffAtI, result);
        }
        return result;

    }

    private static double getSumOfDiffSquares(Point p1, Point p2) {
        double sum = 0;
        for(int i = 0; i < pointsForSumOfDiffSquares.size(); i++) {
            if(pointsForSumOfDiffSquares.get(i) > p1.getX() && pointsForSumOfDiffSquares.get(i) <= p2.getX()) {
                sum += Math.pow(
                        f(pointsForSumOfDiffSquares.get(i)) -
                                g(pointsForSumOfDiffSquares.get(i), p1, p2),
                        2
                );
            }
        }
        return sum;
    }

    /*
     * the value we want - the difference between the line and the function
     * (line from p1 to p2, function from x1 to x2)
     */
    private static double difference(Point p1, Point p2) {
        switch (mode) {
            case INTEGRAL:
                return Math.abs(integral(p1.getX(), p2.getX()) - intergralOfLine(p1, p2));
            case SUM_OF_DIFF_SQUARES:
                return getSumOfDiffSquares(p1, p2);
            case MAX_DIFF:
            default:
                return getMaxDiff(p1, p2);
        }
    }

    /*
     * the approximate line cant be vertical, because there are no vertical functions
     */
    private static Node[][] getNodesArray() {
        Node[][] arr = new Node [xSectors] [ySectors + 1];
        for(int i = 0; i <= xSectors - 2; i++) { // x
            for (int j = 0; j <= ySectors; j++) { // y
                arr [i][j] = new Node(
                        new Point(a.getX() + (i + 1) * xSectorLength, ymin + j * ySectorLength),
                        Double.MAX_VALUE,
                        new Point()
                );
            }
        }
        arr[xSectors -1][0] = new Node(b, Double.MAX_VALUE, new Point()); // the point b
        return arr;
    }

    private static int getXIndex(Point p) {
        return (int) ((p.getX() - a.getX()) / xSectorLength) - 1;
    }

    private static int getYIndex(Point p) {
        return (int) ((p.getY() - ymin) / ySectorLength);
    }

    private static void refreshNodesInfo(Node from) {
        int begXNumber = getXIndex(from.getCoordinate()) + 1;
        for (int i = begXNumber; i < xSectors; i++) {
            for (int j = 0; j <= ySectors; j++) {
                if (nodes[i][j] != null) {
                    double diff = difference(from.getCoordinate(), nodes[i][j].getCoordinate());
                    double newPotentialCost = mode.equals(diffModes.MAX_DIFF) ?
                            Math.max(from.getCost(), diff) :
                            from.getCost() + diff;
                    if (newPotentialCost < nodes[i][j].getCost()) {
                        nodes[i][j].setCost(newPotentialCost);
                        nodes[i][j].setPrevious(from.getCoordinate());
                    }
                }
            }
        }
    }

    private static double round(double x) {
        return Math.round(x * 100) / 100.0;
    }

    private static void printThePathFromTheNode(Node current) {
        System.out.println(round(current.getCoordinate().getX()) + " " + round(current.getCoordinate().getY()));
        int prevXIndex = getXIndex(current.getPrevious());
        int prevYIndex = getYIndex(current.getPrevious());
        if (prevXIndex != -1) {
            printThePathFromTheNode(nodes[prevXIndex][prevYIndex]);
        } else {
            System.out.println(round(a.getX()) + " " + round(a.getY()));
        }
        return;
    }

    public static void main (String args[]) {
        nodes = getNodesArray();

        // we start at the a
        refreshNodesInfo(new Node(a, 0, null));

        for (int i = 0; i < xSectors; i++) {
            for (int j = 0; j <= ySectors; j++ ) {
                if(nodes[i][j] != null)
                    refreshNodesInfo(nodes[i][j]);
            }
        }

        // print from the "b" node
        printThePathFromTheNode(nodes[xSectors - 1][0]);

       System.out.println("-----------------------");
        // points to draw the function
        for (double i = a.getX(); i <= b.getX(); i+=0.05) {
            System.out.println(round(i) + " " + round(f(i)));
        }
    }
}
