package priorityqueues;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class JavaPriorityQueue<T> implements ExtrinsicMinPQ<T> {

    PriorityQueue<T> queue;
    JavaPriorityQueueVertexComparator<T> comparator;
    public static int START_INDEX = 0;

    public JavaPriorityQueue() {
        comparator = new JavaPriorityQueueVertexComparator<>();
        queue = new PriorityQueue<T>(comparator);
    }

    /**
     * Adds an item with the given priority value.
     * @throws IllegalArgumentException if item is null or is already present in the PQ
     */
    public void add(T item, double priority) {
        if (queue.contains(item) || item == null) {
            throw new IllegalArgumentException();
        }
        comparator.addToMap(item, priority);
        queue.add(item);
    }

    /** Returns true if the PQ contains the given item; false otherwise. */
    public boolean contains(T item) {
        return queue.contains(item);
    }

    /**
     * Returns the item with the least-valued priority.
     * @throws NoSuchElementException if the PQ is empty
     */
    public T peekMin() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException();
        }
        return queue.peek();
    }

    /**
     * Removes and returns the item with the least-valued priority.
     * @throws NoSuchElementException if the PQ is empty
     */
    public T removeMin() {
        T minElement = queue.remove();
        comparator.removeFromMap(minElement);
        return minElement;
    }

    /**
     * Changes the priority of the given item.
     * @throws NoSuchElementException if the item is not present in the PQ
     */
    public void changePriority(T item, double priority) {
        boolean removalOccurred = this.queue.remove(item);
        if (removalOccurred) {
            this.comparator.changePriority(item, priority);
            this.queue.add(item);
        } else {
            throw new NoSuchElementException();
        }
    }

    /** Returns the number of items in the PQ. */
    public int size() {
        return queue.size();
    }

    /** Returns true if the PQ is empty; false otherwise. */
    public boolean isEmpty() {
        return queue.size() == 0;
    }

    public List<PriorityNode<T>> toList() {
        List<PriorityNode<T>> nodes = new LinkedList<>();
        Object[] arr = queue.toArray();
        for (int i=0; i<arr.length; ++i) {
            nodes.add(new PriorityNode<T>((T) arr[i], ((JavaPriorityQueueVertexComparator<T>) queue.comparator()).map().get((T) arr[i])));
        }
        return nodes;
    }

}
