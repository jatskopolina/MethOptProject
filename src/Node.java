class Node {
    private Point coordinate; //coordinate of the node
    private double cost; //the cost of the path to the node from a
    private Point previous; // the previous point in the path

    Node (Point coordinate, double cost, Point previous) {
        this.coordinate = coordinate;
        this.cost = cost;
        this.previous = previous;
    }

    Point getCoordinate() {
        return coordinate;
    }

    double getCost() {
        return cost;
    }

    void setCost(double cost) {
        this.cost = cost;
    }

    Point getPrevious() {
        return previous;
    }

    void setPrevious(Point previous) {
        this.previous = previous;
    }
}
