package com.jme.example;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingSphere;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.ss.editor.extension.loader.SceneLoader;


import java.util.ArrayList;
import java.util.List;


/**
 * The game application class.
 */
public class GameApplication extends SimpleApplication implements ActionListener {

    private Spatial avatarBase;

    private DirectionalLight sun;



    @Override
    public void simpleInitApp() {


        FilterPostProcessor fp = new FilterPostProcessor(this.getAssetManager());

        SceneLoader.install(this,fp);




        Spatial map = assetManager.loadModel("Scenes/operaScene01.j3s");
        map.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(map);


        sun = new DirectionalLight();
        sun.setColor(ColorRGBA.LightGray);
        sun.setDirection(new Vector3f(-.5f,-.2f,-.5f).normalizeLocal());
        // this.rootNode.addLight(sun);

        this.getFlyByCamera().setMoveSpeed(32f);

        this.getInputManager().addMapping("toggleFullScreen",new KeyTrigger(KeyInput.KEY_F));
        this.getInputManager().addMapping("toggleResolution",new KeyTrigger(KeyInput.KEY_R));
        this.getInputManager().addMapping("moveAvatar",new KeyTrigger(KeyInput.KEY_M));


        this.getInputManager().addListener(this,"toggleFullScreen");
        this.getInputManager().addListener(this,"toggleResolution");
        this.getInputManager().addListener(this,"moveAvatar");

        this.createLightProbe(this.rootNode);


    }

    private void createLightProbe(Node rootNode){

        EnvironmentCamera environmentCamera = new EnvironmentCamera();
        environmentCamera.setPosition(Vector3f.ZERO);
        environmentCamera.setSize(1024);
        environmentCamera.setEnabled(true);

        LightProbe probe = LightProbeFactory.makeProbe(environmentCamera,rootNode);
    }

    private void createShadow(){
        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer  dlsf = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        //dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        //  fpp.addFilter(dlsf);
        viewPort.addProcessor(dlsf);
    }

    @Override
    public void onAction(String s, boolean b, float v) {

        if(s.equals("toggleFullScreen") && b){
            AppSettings newSetting = new AppSettings(true);
            newSetting.copyFrom(this.settings);

            if(this.settings.isFullscreen()){
                newSetting.setFullscreen(false);
            }else{
                newSetting.setFullscreen(true);
            }

            this.setSettings(newSetting);
            this.restart();

        }

        if(s.equals("toggleResolution") && b){
            AppSettings newSetting = new AppSettings(true);
            newSetting.copyFrom(this.settings);

            if(this.settings.getWidth() != 1920) {
                newSetting.setResolution(1920, 1080);
            }else{
                newSetting.setResolution(1024,768);
            }

            this.setSettings(newSetting);
            this.restart();



        }


    }
}