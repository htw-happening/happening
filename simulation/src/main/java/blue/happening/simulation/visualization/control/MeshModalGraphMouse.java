package blue.happening.simulation.visualization.control;

import java.awt.ItemSelectable;

import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;


public class MeshModalGraphMouse<V, E> extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable {

    public MeshModalGraphMouse() {
        this(1.1F, 0.9090909F);
    }

    private MeshModalGraphMouse(float in, float out) {
        super(in, out);
    }

    @Override
    protected void loadPlugins() {
        this.pickingPlugin = new PickingGraphMousePlugin();
        this.setMode(Mode.TRANSFORMING);
    }
}
