package blue.happening.mesh;

public abstract class Layer {

    private ILayerCallback layerCallback;

    public final void registerLayerCallback(ILayerCallback layerCallback) {
        this.layerCallback = layerCallback;
    }

    public final ILayerCallback getLayerCallback() {
        return layerCallback;
    }
}
