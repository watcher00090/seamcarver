package sample;

/**
 * A weighted directed edge.
 *
 * @param <V> The vertex type.
 */
public class SeamGraphEdge<V> extends Edge<V> {
    public SeamGraphEdgeLocator type;

    public SeamGraphEdge(V from, V to, double weight, SeamGraphEdgeLocator type) {
        super(from, to, weight);
        this.type = type;
    }

    @Override
    public SeamGraphEdge<V> reversed() {
        return new SeamGraphEdge<>(this.to, this.from, this.weight, this.type);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", weight=" + weight +
                ", type=" + type +
                "} " + super.toString();
    }
}
