package search;

public class Node {
    public Node parent;
    public int currentCost;
    public double distanceToGoal;
    public int x;
    public int y;

    public Node(Node parent, int currentCost, int x, int y, Node Goal) {
        this.parent = parent;
        this.currentCost = currentCost;
        this.x = x;
        this.y = y;
        setDistanceToGoal(Goal);
    }

    private void setDistanceToGoal(Node Goal){
        this.distanceToGoal = Math.sqrt((Goal.x - this.x)^2 + (Goal.y-this.y)^2);
    }
}
