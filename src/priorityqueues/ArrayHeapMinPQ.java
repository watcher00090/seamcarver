package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

//public class ArrayHeapMinPQ<T extends Comparable<T>> implements ExtrinsicMinPQ<T> {
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    List<PriorityNode<T>> items;
    TreeMap<T, Double> ts;
    HashMap<T, Integer> hm; // keys = items, values = indices of the item in the items list (null if no longer present)

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        ts = new TreeMap<>();
        hm = new HashMap<>();
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        /*
        PriorityNode<T> node1 = items.get(a);
        PriorityNode<T> node2 = items.get(b);
        T item1 = node1.getItem();
        T item2 = node2.getItem();
        double priority1 = node1.getPriority();
        double priority2 = node2.getPriority();
        node1.setItem(item2);
        node1.setPriority(priority2);
        node2.setItem(item1);
        node2.setPriority(priority1);
        hm.put(item1, b);
        hm.put(item2, a);
        */
        PriorityNode<T> nodeA = items.get(a);
        PriorityNode<T> nodeB = items.get(b);
        T itemA = nodeA.getItem();
        T itemB = nodeB.getItem();
        double priorityA = nodeA.getPriority();
        double priorityB = nodeB.getPriority();

        PriorityNode<T> nodeAdup = new PriorityNode<>(itemA, priorityA);

        items.set(a, nodeB);
        items.set(b, nodeAdup);
        hm.put(itemA, b);
        hm.put(itemB, a);

        /*
        nodeA.setItem(itemB);
        nodeA.setPriority(priorityB);
        nodeB.setItem(itemA);
        nodeB.setPriority(priorityA);
        hm.put(itemA, b);
        hm.put(itemB, a);
        */
    }

    // given integer = starting index
    private void percolateUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (items.get(parentIndex).getPriority() > items.get(index).getPriority()) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    // given integer = starting index
    private void percolateDown(int index) {
        while (true) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            if (leftChildIndex >= size() && rightChildIndex >= size()) { // node is a leaf
                return;
            }
            // node is not a leaf

            // has two children of smaller priority
            if (leftChildIndex < size() && rightChildIndex < size()
                && items.get(index).getPriority() > items.get(leftChildIndex).getPriority()
                && items.get(index).getPriority() > items.get(rightChildIndex).getPriority()) {

                if (items.get(leftChildIndex).getPriority() < items.get(rightChildIndex).getPriority()) {
                    swap(leftChildIndex, index);
                    index = leftChildIndex;
                } else {
                    swap(rightChildIndex, index);
                    index = rightChildIndex;
                }
            } else { // does not have two children of smaller priority: find any child and swap if possible
                if (leftChildIndex < size()
                    && items.get(index).getPriority() > items.get(leftChildIndex).getPriority()) {
                    swap(leftChildIndex, index);
                    index = leftChildIndex;
                    continue;
                }
                if (rightChildIndex < size()
                    && items.get(index).getPriority() > items.get(rightChildIndex).getPriority()) {
                    swap(rightChildIndex, index);
                    index = rightChildIndex;
                    continue;
                }
                // neither child was swapped: node is in its proper place
                break;
            }


        }
    }

    public void remove(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void add(T item, double priority) {
        PriorityNode<T> newNode = new PriorityNode<T>(item, priority);
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        // item not already in heap
        int index = size();
        items.add(newNode);
        hm.put(item, index); // update index
        ts.put(item, priority);
        percolateUp(index);
    }

    @Override
    public boolean contains(T item) {
        return ts.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return items.get(START_INDEX).getItem();
    }

    @Override
    public T removeMin() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        T key = items.get(START_INDEX).getItem();
        swap(START_INDEX, size() - 1);
        items.remove(size() - 1); // remove last entry from list
        percolateDown(START_INDEX);
        ts.remove(key);
        hm.put(key, null);
        return key;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int index = hm.get(item);
        double oldPriority = items.get(index).getPriority();
        items.get(index).setPriority(priority);
        if (priority < oldPriority) {
            percolateUp(index);
        } else {
            percolateDown(index);
        }
    }

    @Override
    public int size() {
        return items.size();
    }
}
