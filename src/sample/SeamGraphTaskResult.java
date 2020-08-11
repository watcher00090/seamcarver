package sample;

import java.util.List;

public class SeamGraphTaskResult {
    public byte[] imageData;
    public int numHorizVertices;
    public int numVertVertices;
    public List<Integer> seam;

    public SeamGraphTaskResult() {
        this.imageData = null;
        this.numHorizVertices = 0;
        this.numVertVertices = 0;
        this.seam = null;
    }

    public SeamGraphTaskResult(byte[] imageData, int numHorizVertices, int numVertVertices) {
        this.imageData = new byte[numHorizVertices * numVertVertices * 3];
        for (int i=0;i<imageData.length;++i) {
            this.imageData[i] = imageData[i];
        }
        this.numHorizVertices = numHorizVertices;
        this.numVertVertices = numVertVertices;
    }
}