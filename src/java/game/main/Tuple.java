package main;

public class Tuple<X, Y> {
    public X x;
    public Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Tuple<X,Y> tuple = (Tuple<X,Y>)obj;
        return (this.x.equals(tuple.x) && this.y.equals(tuple.y));
    }

}
