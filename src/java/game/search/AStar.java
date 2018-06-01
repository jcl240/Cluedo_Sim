package search;
import java.util.*;

public class AStar {

    private Comparator<Node> comparator = new NodeComparator();
    private PriorityQueue<Node> openQueue = new PriorityQueue<>(comparator);
    private LinkedList<Node> closed = new LinkedList<>();
    private boolean[][] map;
    private Node startNode;
    private Node goalNode;
    private LinkedList<Node> finalPath = new LinkedList<>();

    /**
     *
     * @param start Where the search starts
     * @param end Where the search ends
     * @param map The game map defined as elaborated in Board.java
     */
    public AStar(int[] start, int[] end, boolean[][] map) {
        //Reset Goal node
        Node.Goal = null;
        //Set map, start and goal node then add start node to openQueue
        this.map = map;
        this.goalNode = new Node(1000, end[0], end[1]);
        Node.Goal = goalNode;
        this.startNode = new Node(0, start[0], start[1]);
        openQueue.add(startNode);
        closed.add(Node.Goal);
    }

    /**
     * Main search logic, expand most promising node and add successors,
     * if the expanded node is the goal node, finish search
     * @return
     */
    public int search(){
        while(!openQueue.isEmpty()){
            Node expanded = openQueue.poll();
            //if this is goal node finish search and break loop
            if(expanded == Node.Goal){ finishSearch(); break;}

            LinkedList<Node> successors = expandNode(expanded);
            for(Node successor : successors){
                int successor_new_cost = expanded.currentCost + 1;

                if(openQueue.contains(successor)){
                    if(successor.currentCost < successor_new_cost) continue;
                }
                else if(closed.contains(successor)){
                    if(successor.currentCost < successor_new_cost) continue;
                    closed.remove(successor);
                    openQueue.add(successor);
                }
                else{
                    openQueue.add(successor);
                }

                successor.currentCost = successor_new_cost;
                successor.parent = expanded;

            }
            closed.add(expanded);
        }
        if(finalPath.isEmpty()){
            return 0;
        }
        else return 1;
    }

    /**
     * Expand the node and find/return successors
     * @param expanded
     * @return
     */
    private LinkedList<Node> expandNode(Node expanded) {
        LinkedList<Node> successors = new LinkedList<>();
        int x = expanded.x;
        int y = expanded.y;
        int[][] successor_coordinates = {{x+1,y},{x-1,y},{x,y+1},{x,y-1}};
        for(int[] coord: successor_coordinates){
            Node successor = findNode(coord);
            if(successor == null && isValid(coord)){
                successor = new Node(1000,coord[0],coord[1]);
            }
            if(successor != null) successors.add(successor);
        }
        return successors;
    }

    /**
     * Helper method for determining if a coordinate is a valid game tile
     * @param coord
     * @return
     */
    private boolean isValid(int[] coord) {
        int x = coord[0];
        int y = coord[1];
        if(x < 0 || x > map.length-1
                || y < 0 || y > map[0].length-1) return false;
        return map[x][y];
    }

    /**
     * Helper method for checking if a Node with the specified coordinates
     * exists in the open or closed list
     * @param coord
     * @return
     */
    private Node findNode(int[] coord) {
        int x = coord[0];
        int y = coord[1];
        Node[] openArray = openQueue.toArray(new Node[openQueue.size()]);
        for(Node node : openArray){
            if(node.x == x && node.y == y) return node;
        }
        for(Node node : closed){
            if(node.x == x && node.y == y) return node;
        }
        return null;
    }

    /**
     * Traverse the parents and add them to the final path
     */
    private void finishSearch() {
        Node parent = Node.Goal;
        while(parent != null){
            finalPath.add(parent);
            parent = parent.parent;
        }
    }

    /**
     * Getter method that converts the final path (full of nodes)
     * into an array of coordinates from the start to the end
     * @return
     */
    public int[][] getFinalPath() {
        int[][] path = new int[finalPath.size()][2];
        int i = finalPath.size()-1;
        for(Node node: finalPath) {
            path[i][0] = node.x;
            path[i][1] = node.y;
            i--;
        }
        return path;
    }
}
