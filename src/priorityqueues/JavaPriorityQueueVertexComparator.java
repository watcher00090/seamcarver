package priorityqueues;

import java.util.HashMap;
import java.util.Map;

public class JavaPriorityQueueVertexComparator<T> implements java.util.Comparator<T> {

    Map<T, Double> priorities;

    public JavaPriorityQueueVertexComparator() {
        this.priorities = new HashMap<T, Double>();
    }

    public Map<T, Double> map() {
        return priorities;
    }

    @Override
    public int compare(T o1, T o2) {
        return (int) Math.signum(priorities.get(o1) - priorities.get(o2));
    }

    public void changePriority(T item, double priority) {
        priorities.put(item, priority);
    }

    public void addToMap(T item, double priority) {
        priorities.put(item, priority);
    }

    public void removeFromMap(T item) {
        priorities.remove(item);
    }
}
