package sample;

public class SeamGraphTaskResult {
    public byte[] imageData;
    public int numHorizVertices;
    public int numVertVertices;

    public SeamGraphTaskResult() {
        this.imageData = null;
        this.numHorizVertices = 0;
        this.numVertVertices = 0;
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