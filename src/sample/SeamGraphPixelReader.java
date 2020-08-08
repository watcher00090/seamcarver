package sample;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class SeamGraphPixelReader implements PixelReader {
    DijkstraSeamFinderOptimized seamFinder;

    public SeamGraphPixelReader(DijkstraSeamFinderOptimized seamFinder) {
        this.seamFinder = seamFinder;
    }

    @Override
    public PixelFormat getPixelFormat() {
        return null;
    }

    @Override
    public int getArgb(int i, int i1) {
        return 0;
    }

    @Override
    public Color getColor(int i, int i1) {
        return null;
    }

    @Override
    public <T extends Buffer> void getPixels(int i, int i1, int i2, int i3, WritablePixelFormat<T> writablePixelFormat, T t, int i4) {

    }

    @Override
    public void getPixels(int i, int i1, int i2, int i3, WritablePixelFormat<ByteBuffer> writablePixelFormat, byte[] bytes, int i4, int i5) {

    }

    @Override
    public void getPixels(int i, int i1, int i2, int i3, WritablePixelFormat<IntBuffer> writablePixelFormat, int[] ints, int i4, int i5) {

    }
}
