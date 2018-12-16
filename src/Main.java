public class Main {

    // -------- parameters -------------
    private static final Point a = new Point(0.0, f(0.0)); // left border point
    private static final Point b = new Point(Math.PI, f(Math.PI)); // right border point

    private static final int xSectors = 2; // the amount of sectors for x coordinate (approximation will be more accurate)
    private static final int ySectors = 2;

    private static final double ymax = 5;
    private static final double ymin = -5;

    private static double f(double x) { // function to approximate
        return Math.sin(x);
    }

    private static double F(double x) { // depends on f!!!
        return -Math.cos(x);
    }

    // ----------parameters end -----------



    private static final double xSectorLength = Math.abs(b.getX() - a.getX()) / xSectors;
    private static final double ySectorLength = Math.abs(ymax - ymin) / ySectors;
    private static Node[][] nodes;

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
     * the value we want - the difference in the area between the line and the function
     * (line from p1 to p2, function from x1 to x2)
     */
    private static double difference(Point p1, Point p2) {
        return Math.abs(integral(p1.getX(), p2.getX()) - intergralOfLine(p1, p2));
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
                    if (from.getCost() + diff < nodes[i][j].getCost()) {
                        nodes[i][j].setCost(from.getCost() + diff);
                        nodes[i][j].setPrevious(from.getCoordinate());
                    }
                }
            }
        }
    }

    private static void printThePathFromTheNode(Node current) {
        if (current == null) {
            return;
        }

        System.out.println(current.getCoordinate().getX() + " " + current.getCoordinate().getY());
        printThePathFromTheNode(
                nodes[getXIndex(current.getPrevious())]
                        [getYIndex(current.getPrevious())]
        );
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
    }
}
