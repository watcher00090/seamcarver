package priorityqueues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some provided tests for ArrayHeapMinPQ.
 *
 * If you wish, you can extend this class and override createMinPQ and assertThat to run on
 * NaiveMinPQ instead (although you'd need to make a dummy NaiveMinPQAssert class that with an
 * invariant check that does nothing).
 */
public class ArrayHeapMinPQTests extends BaseTest {

    static int N = 200000;
    static int K = 1000;

    protected <T extends Comparable<T>> ExtrinsicMinPQ<T> createMinPQ() {
        return new ArrayHeapMinPQ<>();
    }

    protected <T extends Comparable<T>> AbstractHeapMinPQAssert<T> assertThat(ExtrinsicMinPQ<T> pq) {
        return new ArrayHeapMinPQAssert<>((ArrayHeapMinPQ<T>) pq);
    }

    @Nested
    @DisplayName("New Empty")
    class NewEmpty {
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            return createMinPQ();
        }

        @Test
        void size_returns0() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.size();
            assertThat(output).isEqualTo(0);
            assertThat(pq).isValid();
        }

        @Test
        void contains_returnsFalse() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            boolean output = pq.contains(412);
            assertThat(output).isFalse();
            assertThat(pq).isValid();
        }

        @Test
        void peekMin_throwsNoSuchElement() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(pq::peekMin).isInstanceOf(NoSuchElementException.class);
            assertThat(pq).isValid();
        }

        @Test
        void removeMin_throwsNoSuchElement() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(pq::removeMin).isInstanceOf(NoSuchElementException.class);
            assertThat(pq).isValid();
        }

        @Test
        void changePriority_throwsNoSuchElement() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(() -> pq.changePriority(1, 7)).isInstanceOf(NoSuchElementException.class);
            assertThat(pq).isValid();
        }

        @Test
        void add_nullItem_throwsIllegalArgument() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(() -> pq.add(null, 15)).isInstanceOf(IllegalArgumentException.class);
            assertThat(pq).isValid();
        }
    }

    @Nested
    @DisplayName("Empty After Adding/Removing 3")
    class EmptyAfterAddRemove extends NewEmpty {
        @Override
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 1);
            pq.add(2, 2);
            pq.add(3, 3);
            pq.removeMin();
            pq.removeMin();
            pq.removeMin();
            return pq;
        }
    }

    @Nested
    @DisplayName("Add 3 Increasing Priority")
    class Add3Increasing {
        int min = 1;

        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 1);
            pq.add(2, 2);
            pq.add(3, 3);
            return pq;
        }

        @Test
        void isValid() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThat(pq).isValid();
        }

        @Test
        void size_returns3() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.size();
            assertThat(output).isEqualTo(3);
            assertThat(pq).isValid();
        }

        @Test
        void contains_withContainedItem_returnsTrue() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            boolean output = pq.contains(1);
            assertThat(output).isTrue();
            assertThat(pq).isValid();
        }

        @Test
        void contains_withNotContainedItem_returnsFalse() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            boolean output = pq.contains(412);
            assertThat(output).isFalse();
            assertThat(pq).isValid();
        }

        @Test
        void peekMin_returnsCorrectItem() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.peekMin();
            assertThat(output).isEqualTo(this.min);
            assertThat(pq).isValid();
        }

        @Test
        void removeMin_returnsCorrectItem() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.removeMin();
            assertThat(output).isEqualTo(this.min);
            assertThat(pq).isValid();
        }

        @Test
        void add_nullItem_throwsIllegalArgument() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(() -> pq.add(null, 15)).isInstanceOf(IllegalArgumentException.class);
            assertThat(pq).isValid();
        }

        @Test
        void add_duplicateItem_throwsIllegalArgument() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThatThrownBy(() -> pq.add(1, 15)).isInstanceOf(IllegalArgumentException.class);
            assertThat(pq).isValid();
        }
    }

    @Nested
    @DisplayName("Add 3 Decreasing Priority")
    class Add3Decreasing extends Add3Increasing {
        Add3Decreasing() {
            min = 3;
        }

        @Override
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 3);
            pq.add(2, 2);
            pq.add(3, 1);
            return pq;
        }
    }

    @Nested
    @DisplayName("Add 3 Arbitrary Priority")
    class Add3Arbitrary extends Add3Increasing {
        Add3Arbitrary() {
            min = 2;
        }

        @Override
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 3);
            pq.add(2, 1);
            pq.add(3, 2);
            return pq;
        }
    }

    @Nested
    @DisplayName("Add 3 Same Priority")
    class Add3Same {
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 7);
            pq.add(2, 7);
            pq.add(3, 7);
            return pq;
        }

        @Test
        void isValid() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThat(pq).isValid();
        }

        @Test
        void size_returns3() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.size();
            assertThat(output).isEqualTo(3);
            assertThat(pq).isValid();
        }

        @Test
        void contains_withContainedItem_returnsTrue() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            boolean output = pq.contains(1);
            assertThat(output).isTrue();
            assertThat(pq).isValid();
        }

        @Test
        void removeMinRepeatedly_returnsAllItems() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            List<Integer> output = removeAll(pq);
            assertThat(output).containsExactlyInAnyOrder(1, 2, 3);
            assertThat(pq).isValid();
        }
    }

    @Nested
    @DisplayName("Add 10 Increasing Priority")
    class Add10Increasing {
        Integer[] correctOrdering = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int startingMinItem = 1;

        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 1);
            pq.add(2, 2);
            pq.add(3, 3);
            pq.add(4, 4);
            pq.add(5, 5);
            pq.add(6, 6);
            pq.add(7, 7);
            pq.add(8, 8);
            pq.add(9, 9);
            pq.add(10, 10);
            return pq;
        }

        @Test
        void isValid() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            assertThat(pq).isValid();
        }

        @Test
        void size_returns10() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            int output = pq.size();
            assertThat(output).isEqualTo(10);
            assertThat(pq).isValid();
        }

        @Test
        void removeMinRepeatedly_returnsItemsInCorrectOrder() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            List<Integer> output = removeAll(pq);
            assertThat(output).containsExactly(correctOrdering);
            assertThat(pq).isValid();
        }

        @Test
        void changePriority_returnsNewMinAfterIncreasingMinPriority() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(1, .5);
            Integer output = pq.peekMin();
            assertThat(output).isEqualTo(1);
        }

        @Test
        void changePriority_returnsSameSizeAfterChangingRandomElementsPriorityTo5() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(1, 5);
            pq.changePriority(4, 5);
            pq.changePriority(5, 5);
            pq.changePriority(8, 5);
            pq.changePriority(9, 5);
            Integer output = pq.size();
            assertThat(output).isEqualTo(10);
        }

        @Test
        void changePriority_returnsCorrectMinAfterDecreasingMinPriority() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(1, .5);
            Integer output = pq.peekMin();
            assertThat(output).isEqualTo(1);
        }

        @Test
        void changePriority_returnsSameMinAfterIncreasingInternalElementsPriorities() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(2, 20);
            pq.changePriority(3, 30);
            pq.changePriority(4, 40);
            pq.changePriority(8, 80);
            Integer output = pq.peekMin();
            assertThat(output).isEqualTo(startingMinItem);
        }

        @Test
        void changePriority_containsCorrectlyReturnsFalseAfterChangingMinElementsPriority() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(2, 1.5);
            pq.changePriority(startingMinItem, 100);
            Integer output = pq.peekMin();
            assertThat(output).isNotEqualTo(startingMinItem);
            assertThat(output).isEqualTo(2);
        }

        @Test
        void changePriority_containsFindsElementOfNewPriority() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(1, 20);
            boolean output = pq.contains(1);
            assertThat(output).isEqualTo(true);
        }

    }

    @Nested
    @DisplayName("Add 10 Arbitrary Priority")
    class Add10Arbitrary extends Add10Increasing {

        Add10Arbitrary() {
            this.correctOrdering = new Integer[]{5, 8, 1, 4, 7, 9, 3, 6, 2, 10};
            this.startingMinItem = 5;
        }

        @Override
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 3);
            pq.add(2, 9);
            pq.add(3, 7);
            pq.add(4, 4);
            pq.add(5, 1);
            pq.add(6, 8);
            pq.add(7, 5);
            pq.add(8, 2);
            pq.add(9, 6);
            pq.add(10, 10);
            return pq;
        }

    }

    @Nested
    @DisplayName("Add 10 Arbitrary Large Priorities")
    class Add10ArbitraryLargeStartingPriorities extends Add10Arbitrary {
        Integer[] largeCorrectOrdering = {5, 7, 8, 9, 1, 12, 14, 3, 6, 4, 2, 13, 10, 11};

        Add10ArbitraryLargeStartingPriorities() {
            super();
        }

        @Override
        ExtrinsicMinPQ<Integer> setUpMinPQ() {
            ExtrinsicMinPQ<Integer> pq = createMinPQ();
            pq.add(1, 30);
            pq.add(2, 90);
            pq.add(3, 70);
            pq.add(4, 40);
            pq.add(5, 10);
            pq.add(6, 80);
            pq.add(7, 50);
            pq.add(8, 20);
            pq.add(9, 60);
            pq.add(10, 100);
            return pq;
        }

        @Test
        void changePriorityBetweenAdds_returnsElementsInCorrectOrder() {
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            pq.changePriority(5, 5); // 10->5
            pq.changePriority(4, 85); // 40->85
            pq.add(11, 205);
            pq.add(12, 35);
            pq.changePriority(7, 15);
            pq.changePriority(9, 25);
            pq.add(13, 95);
            pq.add(14, 45);

            List<Integer> output = removeAll(pq);
            assertThat(output).containsExactly(largeCorrectOrdering);
            //assertThat(pq).isValid();
        }

        @Test
        void addPerformanceTest() {
            System.out.println("starting addPerformanceTest()....");
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            double[] prioritiesArray = new double[N];
            int[] valuesArray = new int[N];
            HashSet<Integer> vals = new HashSet<Integer>();

            for (int i = 0; i < N; i++) {
                int x = (int) Math.round(Math.random() * (10000 * N));
                double priority = Math.floor(1000 * Math.random()) / 1000.0;

                while (vals.contains(x)) {
                    x = (int) Math.round(Math.random() * (10000 * N));
                }
                vals.add(x);
                prioritiesArray[i] = priority;
                valuesArray[i] = x;
            }

            long tot = 0;
            for (int i = 0; i < N; ++i) {
                if (i % 1000 == 0) {

                    // j adds, then average
                    for (int j = 1; j <= K; ++j) {
                        long t1 = System.currentTimeMillis();
                        pq.add(valuesArray[i], prioritiesArray[i]);
                        long t2 = System.currentTimeMillis();
                        long time = t2 - t1;
                        tot += time;
                        pq.add(valuesArray[i], 0.00000001);
                    }

                    System.out.print("(" + i + ", " + (double) (tot) / (double) K + "), ");

                } else {
                    pq.add(valuesArray[i], prioritiesArray[i]);
                }
            }

        }

        @Test
        void removePerformanceTest() {
            System.out.println("starting removePerformanceTest()....");
            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            double[] prioritiesArray = new double[N];
            int[] valuesArray = new int[N];
            HashSet<Integer> vals = new HashSet<Integer>();
            for (int i = 0; i < N; ++i) {
                int x = (int) Math.round(Math.random() * (10000 * N));
                double priority = Math.floor(1000 * Math.random()) / 1000.0;

                while (vals.contains(x)) {
                    x = (int) Math.round(Math.random() * (10000 * N));
                }
                vals.add(x);
                prioritiesArray[i] = priority;
                valuesArray[i] = x;
            }

            for (int i = 0; i < N; ++i) {
                pq.add(valuesArray[i], prioritiesArray[i]);
            }

            for (int i = 0; i < N; ++i) {
                if (i % 1000 == 0) {

                    long tot = 0;

                    for (int j=0; j<K; ++j) {
                        long t1 = System.currentTimeMillis();
                        int x = pq.removeMin();
                        long t2 = System.currentTimeMillis();
                        long time = t2 - t1;
                        tot += time;
                        pq.add(x, 0.000000001);
                    }
                    System.out.print("(" + (N - i) + ", " + (double) tot / (double) K + "), ");

                } else {
                    pq.removeMin();
                }
            }
        }

        @Test
        void changePriorityPerformanceTest() {
            System.out.println("starting changePriorityPerformanceTest....");

            ExtrinsicMinPQ<Integer> pq = setUpMinPQ();
            double[] prioritiesArray = new double[N];
            double[] secondPrioritiesArray = new double[N];
            int[] valuesArray = new int[N];
            HashSet<Integer> vals = new HashSet<Integer>();
            for (int i = 0; i < N; ++i) {
                int x = (int) Math.round(Math.random() * (10000 * N));
                double priority = Math.floor(1000 * Math.random()) / 1000.0;
                double priority2 = Math.floor(1000 * Math.random()) / 1000.0;

                while (vals.contains(x)) {
                    x = (int) Math.round(Math.random() * (10000 * N));
                }
                vals.add(x);
                prioritiesArray[i] = priority;
                secondPrioritiesArray[i] = priority2;
                valuesArray[i] = x;
            }

            for (int i = 0; i < N; ++i) {
                pq.add(valuesArray[i], prioritiesArray[i]);
                if (i % 1000 == 0) {

                    long tot = 0;
                    for (int j=0; j<K; ++j) {
                        double oldPriority = prioritiesArray[i];
                        long t1 = System.currentTimeMillis();
                        pq.changePriority(valuesArray[i], secondPrioritiesArray[i]);
                        long t2 = System.currentTimeMillis();
                        long time = t2 - t1;
                        pq.changePriority(valuesArray[i], oldPriority);
                        tot += time;
                    }
                    System.out.print("(" + (N - i) + ", " + (double) tot / (double) K + "), ");
                }
            }
        }

    }

    /**
     * Removes all items from given priority queue, and returns them in the order removed.
     * <p>
     * This is not a "unit" that's great for unit testing since it involves calling too many
     * operations; it sacrifices some ease of debugging in favor of test brevity and thoroughness.
     */
    protected <T extends Comparable<T>> List<T> removeAll(ExtrinsicMinPQ<T> pq) {
        return IntStream.range(0, pq.size()).mapToObj(i -> pq.removeMin()).collect(Collectors.toList());
    }
}
