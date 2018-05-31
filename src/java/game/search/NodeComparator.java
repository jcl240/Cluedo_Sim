package search;
import java.util.Comparator;

public class NodeComparator implements Comparator<Node>{

    @Override
    public int compare(Node x, Node y)
    {
        double x_f = x.currentCost+x.distanceToGoal;
        double y_f = y.currentCost+y.distanceToGoal;
        if (x_f < y_f)
        {
            return -1;
        }
        if (x_f > y_f)
        {
            return 1;
        }
        return 0;
    }
}
