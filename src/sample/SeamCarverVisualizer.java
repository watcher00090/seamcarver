package sample;

import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Uses SeamCarver to remove number of rows or columns specified.
 * Shows the sequence of seams being removed.
 */
public class SeamCarverVisualizer {
    protected static final String IMAGE_NAME = "small image 1";
    protected static final int NUM_PIXELS = 172800;
    private static final Utils.Orientation ORIENTATION = Utils.Orientation.HORIZONTAL;
    JFrame frame;

    public void visualizeHorizontalCarve(SeamCarver sc, int N) {
        for (int i = 0; i < N; i++) {
            List<Integer> minSeam = sc.findHorizontalSeam();
            Picture p = sc.picture();
            paintHorizontalSeam(p, minSeam);
            show(p);
            sc.removeHorizontalSeam(minSeam);
        }

        show(sc.picture());
    }

    public void visualizeVerticalCarve(SeamCarver sc, int N) {
        for (int i = 0; i < N; i++) {
            List<Integer> minSeam = sc.findVerticalSeam();
            Picture p = sc.picture();
            paintVerticalSeam(p, minSeam);
            show(p);
            sc.removeVerticalSeam(minSeam);
        }

        show(sc.picture());
        sc.picture().save("output.png");
    }

    private void paintHorizontalSeam(Picture p, List<Integer> seam) {
        for (int i = 0; i < seam.size(); i++) {
            p.set(i, seam.get(i), new Color(255, 0, 0));
        }
    }

    private void paintVerticalSeam(Picture p, List<Integer> seam) {
        for (int i = 0; i < seam.size(); i++) {
            p.set(seam.get(i), i, new Color(255, 0, 0));
        }
    }

    public void show(Picture img) {
        if (frame == null) {
            frame = new JFrame();

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            menuBar.add(menu);
            JMenuItem menuItem1 = new JMenuItem(" Save...   ");
            menuItem1.addActionListener(img);
            menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            menu.add(menuItem1);
            frame.setJMenuBar(menuBar);

            frame.setContentPane(img.getJLabel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setTitle("Output");
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
         }

         // draw
         frame.setContentPane(img.getJLabel());
         frame.revalidate();
         frame.repaint();
    }

    public static void main(String[] args) {
        Picture samplePicture = PictureUtils.loadPicture(IMAGE_NAME);
        SeamCarver sc = new SeamCarver(samplePicture,
            new DualGradientEnergyFunction(),
            new DijkstraSeamFinder());

        SeamCarverVisualizer scv = new SeamCarverVisualizer();
        if (ORIENTATION == Utils.Orientation.HORIZONTAL) {
            scv.visualizeHorizontalCarve(sc, NUM_PIXELS);
        } else {
            scv.visualizeVerticalCarve(sc, NUM_PIXELS);
        }
    }
}
