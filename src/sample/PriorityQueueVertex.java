package sample;

public class PriorityQueueVertex<T> {
    T baseVertex;
    double priority;
    public PriorityQueueVertex(T baseVertex, double priority) {
        this.baseVertex = baseVertex;
        this.priority = priority;
    }
}
