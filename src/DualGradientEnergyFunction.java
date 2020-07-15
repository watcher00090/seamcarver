import edu.princeton.cs.algs4.Picture;

import java.util.function.BiFunction;

public class DualGradientEnergyFunction implements EnergyFunction {

    public class R_func implements BiFunction<Integer, Integer, Integer> {
        Picture picture;

        public R_func(Picture picture) {
            this.picture = picture;
        }

        public Integer apply(Integer x, Integer y) {
            int rgb = picture.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF;
            return r;
        }
    }

    public class G_func implements BiFunction<Integer, Integer, Integer> {
        Picture picture;

        public G_func(Picture picture) {
            this.picture = picture;
        }

        public Integer apply(Integer x, Integer y) {
            int rgb = picture.getRGB(x, y);
            int g = (rgb >> 8) & 0xFF;
            return g;
        }
    }

    public class B_func implements BiFunction<Integer, Integer, Integer> {
        Picture picture;

        public B_func(Picture picture) {
            this.picture = picture;
        }

        public Integer apply(Integer x, Integer y) {
            int rgb = picture.getRGB(x, y);
            int b = (rgb >> 0) & 0xFF;
            return b;
        }
    }

    private enum DifferenceType {
        CentralDifference, ForwardDifference, BackwardDifference;
    }

    private enum Dir {
        x, y;
    }

    @Override
    public double apply(Picture picture, int x, int y) {

        R_func r_func = new R_func(picture);
        B_func b_func = new B_func(picture);
        G_func g_func = new G_func(picture);
        DifferenceType xDifferenceType = getXDifferenceType(x, y, picture.width(), picture.height());
        DifferenceType yDifferenceType = getYDifferenceType(x, y, picture.width(), picture.height());
        int R_x = computeGrad(x, y, r_func, xDifferenceType, Dir.x);
        int G_x = computeGrad(x, y, g_func, xDifferenceType, Dir.x);
        int B_x = computeGrad(x, y, b_func, xDifferenceType, Dir.x);

        int R_y = computeGrad(x, y, r_func, yDifferenceType, Dir.y);
        int G_y = computeGrad(x, y, g_func, yDifferenceType, Dir.y);
        int B_y = computeGrad(x, y, b_func, yDifferenceType, Dir.y);

        int del_2_x = R_x * R_x + G_x * G_x + B_x * B_x;
        int del_2_y = R_y * R_y + G_y * G_y + B_y * B_y;

        return Math.sqrt(del_2_x + del_2_y);
    }

    private static int computeGrad(int x, int y, BiFunction<Integer, Integer, Integer> func, DifferenceType differenceType, Dir d) {
        if (differenceType == DifferenceType.ForwardDifference && d == Dir.x) {
            return forwardDiff_x(x, y, func);
        } else if (differenceType == DifferenceType.ForwardDifference && d == Dir.y) {
            return forwardDiff_y(x, y, func);
        } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.x) {
            return backwardDiff_x(x, y, func);
        } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.y) {
            return backwardDiff_y(x, y, func);
        } else if (differenceType == DifferenceType.CentralDifference && d == Dir.x) {
            return centralDiff_x(x, y, func);
        } else if (differenceType == DifferenceType.CentralDifference && d == Dir.y) {
            return centralDiff_y(x, y, func);
        } else {
            System.out.println("System error in computeGrad");
            return -1;
        }
    }

    private static DifferenceType getXDifferenceType(int x, int y, int width, int height) {
        if (x == 0) {
            return DifferenceType.ForwardDifference;
        } else if (x == width - 1) {
            return DifferenceType.BackwardDifference;
        } else {
            return DifferenceType.CentralDifference;
        }
    }

    private static DifferenceType getYDifferenceType(int x, int y, int width, int height) {
        if (y == 0) {
            return DifferenceType.ForwardDifference;
        } else if (y == height - 1) {
            return DifferenceType.BackwardDifference;
        } else {
            return DifferenceType.CentralDifference;
        }
    }

    private static int forwardDiff_x(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return -3 * func.apply(x, y) + 4 * func.apply(x + 1, y) - func.apply(x + 2, y);
    }

    private static int forwardDiff_y(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return -3 * func.apply(x, y) + 4 * func.apply(x, y + 1) - func.apply(x, y + 2);
    }

    private static int backwardDiff_x(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return -3 * func.apply(x, y) + 4 * func.apply(x - 1, y) - func.apply(x - 2, y);
    }

    private static int backwardDiff_y(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return -3 * func.apply(x, y) + 4 * func.apply(x, y - 1) - func.apply(x, y - 2);
    }

    private static int centralDiff_x(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return func.apply(x + 1, y) - func.apply(x - 1, y);
    }

    private static int centralDiff_y(int x, int y, BiFunction<Integer, Integer, Integer> func) {
        return func.apply(x, y + 1) - func.apply(x, y - 1);
    }

}
