package com.jme.example.controllers;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import javafx.scene.control.Control;

public class ShipController extends AbstractControl {

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
