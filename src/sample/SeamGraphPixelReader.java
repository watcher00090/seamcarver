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
        System.out.println("Got here!");
    }

    @Override
    public PixelFormat getPixelFormat() {
        return PixelFormat.getIntArgbInstance();
    }

    @Override
    public int getArgb(int x, int y) {
        return seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x,y);
    }

    @Override
    public Color getColor(int x, int y) {
        return seamFinder.verticalSeamGraph.rgbfetcher.getColor(x,y);
    }

    @Override
    public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> writablePixelFormat, T buf, int scanLineStride) {
        switch (writablePixelFormat.getType()) {
            case BYTE_BGRA: {
                byte[] buffer = (byte[]) buf.array();
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = scanLineStride * j + i * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF);
                        buffer[index + 3] = 1;
                    }
                }
                break;
            } case BYTE_BGRA_PRE: {
                byte[] buffer = (byte[]) buf.array();
                for (int j = y; j < h; j++) {
                    for (int i = 0; i < w; i++) {
                        int index = scanLineStride * j + i * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);  // blue
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF); // red
                        buffer[index + 3] = 1;                           // alpha
                    }
                }
                break;
                /* Alternatively,
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * i + j * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);  // blue
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF); // red
                    }
                }
                break;
                 */
            } case BYTE_RGB: {
                byte[] buffer = (byte[]) buf.array();
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = scanLineStride * j + i * 3;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index + 0] = (byte) ((rgb >> 16) & 0xFF); // red
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 0) & 0xFF); // blue
                    }
                }
                break;
            } case BYTE_INDEXED: {
                System.out.println("Error, called getPixels()_bytebuffer with a WritablePixelReader of type BYTE_INDEXED ....");
                System.exit(1);
                break;
            } case INT_ARGB: {
                int[] buffer = (int[]) buf.array();
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = scanLineStride * j + i * 3;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index] = rgb;
                    }
                }
                break;
            } case INT_ARGB_PRE: {
                int[] buffer = (int[]) buf.array();
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = scanLineStride * j + i * 3;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index] = rgb;
                    }
                }
                break;
            } default: {
                System.out.println("Error, default case was reached during a call to getPixels()_writableBuffer<T>");
                System.exit(1);
            }
        }
    }

    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> writablePixelFormat, byte[] buffer, int offset, int scanLineStride) {
        switch (writablePixelFormat.getType()) {
            case BYTE_BGRA:
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * j + i * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF);
                        buffer[index + 3] = 1;
                    }
                }
                break;
            case BYTE_BGRA_PRE:
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * j + i * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);  // blue
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF); // red
                        buffer[index + 3] = 1;                           // alpha
                    }
                }
                break;
                /* Alternatively,
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * i + j * 4;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                        buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);  // blue
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF); // red
                    }
                }
                break;
                 */
            case BYTE_RGB:
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * j + i * 3;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                        buffer[index + 0] = (byte) ((rgb >> 16) & 0xFF); // red
                        buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                        buffer[index + 2] = (byte) ((rgb >> 0) & 0xFF); // blue
                    }
                }
                break;
            case BYTE_INDEXED:
                System.out.println("Error, called getPixels()_bytebuffer with a WritablePixelReader of type BYTE_INDEXED ....");
                System.exit(1);
                break;
            case INT_ARGB:
                System.out.println("Error, called getPixels()_bytebuffer with a WritablePixelReader of type INT_ARGB ....");
                System.exit(1);
                break;
            case INT_ARGB_PRE:
                System.out.println("Error, called getPixels()_bytebuffer with a WritablePixelReader of type INT_ARGB_PRE ....");
                System.exit(1);
                break;

        }
    }

    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> writablePixelFormat, int[] buffer, int offset, int scanLineStride) {
        switch (writablePixelFormat.getType()) {
            case BYTE_BGRA:
                System.out.println("Error, calling getPixels_IntBuffer with a writablePixelFormat of type BYTE_BGRA");
                System.exit(1);
                break;
            case BYTE_BGRA_PRE:
                System.out.println("Error, calling getPixels_IntBuffer with a writablePixelFormat of type BYTE_BGRA_PRE");
                System.exit(1);
                break;
            /* Alternatively,
            for (int i = 0; i < w; i++) {
                for (int j = y; j < h; j++) {
                    int index = offset + scanLineStride * i + j * 4;
                    int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x+i,y+j);
                    buffer[index + 0] = (byte) ((rgb >> 0) & 0xFF);  // blue
                    buffer[index + 1] = (byte) ((rgb >> 8) & 0xFF);  // green
                    buffer[index + 2] = (byte) ((rgb >> 16) & 0xFF); // red
                }
            }
            break;
             */
            case BYTE_RGB:
                System.out.println("Error, calling getPixels_IntBuffer with a writablePixelFormat of type BYTE_RGB");
                System.exit(1);
                break;
            case BYTE_INDEXED:
                System.out.println("Error, called getPixels()_bytebuffer with a WritablePixelReader of type BYTE_INDEXED ....");
                System.exit(1);
                break;
            case INT_ARGB:
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * j + i;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index] = rgb;
                    }
                }
                break;
            case INT_ARGB_PRE:
                for (int i = 0; i < w; i++) {
                    for (int j = y; j < h; j++) {
                        int index = offset + scanLineStride * j + i;
                        int rgb = seamFinder.verticalSeamGraph.rgbfetcher.getRGB(x + i, y + j);
                        buffer[index] = rgb;
                    }
                }
                break;
            default:
                System.out.println("Error, reached default case during a call to getPixels()_IntBuffer...exiting...");
                System.exit(1);

        }
    }


}
