package search;

class Node {
    Node parent = null;
    int currentCost;
    double distanceToGoal;
    int x;
    int y;
    static Node Goal = null;

    /**
     *
     * @param currentCost
     * @param x
     * @param y
     */
    Node(int currentCost, int x, int y) {
        this.currentCost = currentCost;
        this.x = x;
        this.y = y;
        if( Goal != null )setDistanceToGoal(Goal);
        else this.distanceToGoal = 0;
    }

    /**
     *
     * @param Goal
     */
    private void setDistanceToGoal(Node Goal){
        this.distanceToGoal = Math.sqrt((Math.pow(Goal.x - this.x,2) + Math.pow(Goal.y-this.y,2)));
    }
}
