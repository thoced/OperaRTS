package opera.controllers;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import opera.modeles.PathTrip;

public abstract class ShipBaseController extends AbstractControl {

    protected boolean selected = false;

    protected Spatial selectSpatial = null;

    protected PathTrip<Vector3f> pathTrip = new PathTrip<Vector3f>();

    public PathTrip<Vector3f> getPathTrip() {
        return pathTrip;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
