package opera.controllers;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import opera.modeles.SpatialModels;

public class ShipController extends ShipBaseController {

    private boolean doMove = false;

    private Vector3f finalDestination = Vector3f.ZERO;

    public Vector3f getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Vector3f finalDestination) {
        this.finalDestination = finalDestination;
        doMove = true;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if(selected) {
            selectSpatial = SpatialModels.getInstance().getSpatialMap().get(SpatialModels.MODEL_SELECT).clone();
            BillboardControl bc = new BillboardControl();
            bc.setAlignment(BillboardControl.Alignment.Camera);
            selectSpatial.addControl(bc);
            selectSpatial.setLocalScale(3.5f);
            selectSpatial.setLocalTranslation(this.getSpatial().getWorldTranslation());
            ((Node) this.getSpatial()).attachChild(selectSpatial);
        }else{
            if(selectSpatial != null) {
                ((Node) this.getSpatial()).detachChild(selectSpatial);
            }
        }

    }

    @Override
    protected void controlUpdate(float tpf) {
        if(doMove) {

            Vector3f p = this.getSpatial().getLocalTranslation();
            p.interpolateLocal(finalDestination, tpf);
            this.getSpatial().setLocalTranslation(p);

            if(this.getSpatial().getLocalTranslation().distance(finalDestination) < 0.5f)
                doMove = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
