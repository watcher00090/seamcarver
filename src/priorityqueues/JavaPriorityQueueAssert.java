package priorityqueues;

import java.util.List;

/**
 * An implementation of AbstractHeapMinPQAssert that extracts the proper fields from an
 * JavaPriorityQueue implementation.
 */
public class JavaPriorityQueueAssert<T extends Comparable<T>> extends AbstractHeapMinPQAssert<T> {

    public JavaPriorityQueueAssert(JavaPriorityQueue<T> actual) {
        super((ExtrinsicMinPQ<T>) actual, JavaPriorityQueueAssert.class);
    }

    @Override
    protected int extractStartIndex(ExtrinsicMinPQ<T> actual) {
        return JavaPriorityQueue.START_INDEX;
    }

    @Override
    protected List<PriorityNode<T>> extractHeap(ExtrinsicMinPQ<T> actual) {
        return ((JavaPriorityQueue<T>) actual).toList();
    }
}
