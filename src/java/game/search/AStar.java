package search;
import java.util.*;

public class AStar {

    Comparator<Node> comparator = new NodeComparator();
    PriorityQueue<Node> openQueue = new PriorityQueue<Node>(comparator);


    public static void main(String[] args){

    }
}
