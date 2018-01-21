package opera;

import opera.controllers.ShipController;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.ss.editor.extension.loader.SceneLoader;


import java.util.ArrayList;
import java.util.List;


/**
 * The game application class.
 */
public class GameApplication extends SimpleApplication implements ActionListener,AnalogListener {

    private Spatial shipBase;

    private List<ShipController> listShip = new ArrayList<ShipController>();

    private DirectionalLight sun;



    @Override
    public void simpleInitApp() {



        FilterPostProcessor fp = new FilterPostProcessor(this.getAssetManager());

        SceneLoader.install(this,fp);

        Spatial map = assetManager.loadModel("Scenes/operaScene01.j3s");
        map.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(map);

        shipBase = assetManager.loadModel("Modeles/spaceship01/vaisseau01.j3o");

        Spatial sky = SkyFactory.createSky(assetManager, "textures/Environnement/Space01/tycho_cyl_glow.png", SkyFactory.EnvMapType.EquirectMap);
        rootNode.attachChild(sky);
        //rootNode.getChild(0).setCullHint(Spatial.CullHint.Always);


        sun = new DirectionalLight();
        sun.setColor(ColorRGBA.LightGray);
        sun.setDirection(new Vector3f(-.5f,-.2f,-.5f).normalizeLocal());
        // this.rootNode.addLight(sun);

        this.getFlyByCamera().setMoveSpeed(32f);

        this.getInputManager().addMapping("toggleFullScreen",new KeyTrigger(KeyInput.KEY_F));
        this.getInputManager().addMapping("toggleResolution",new KeyTrigger(KeyInput.KEY_R));
        this.getInputManager().addMapping("moveAvatar",new KeyTrigger(KeyInput.KEY_M));
        this.getInputManager().addMapping("mouseClic",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));


        this.getInputManager().addListener(this,"toggleFullScreen");
        this.getInputManager().addListener(this,"toggleResolution");
        this.getInputManager().addListener(this,"moveAvatar");
        this.getInputManager().addListener(this,"mouseClic");


                this.createShip(new Vector3f(0,0,0));




       this.createLightProbe(this.rootNode,this.getStateManager());

        this.getInputManager().setCursorVisible(true);
        this.getFlyByCamera().setDragToRotate(true);


    }

    private void createShip(Vector3f pos){
        Spatial ship = shipBase.clone();
        ship.setLocalTranslation(pos);
        ShipController shipController = new ShipController();
        ship.addControl(shipController);
        listShip.add(shipController);
        rootNode.attachChild(ship);
    }

    private int frame = 0;
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        frame++;
        if(frame == 2){
            final LightProbe probe = LightProbeFactory.makeProbe(stateManager.getState(EnvironmentCamera.class), rootNode, EnvMapUtils.GenerationType.Fast, new JobProgressAdapter<LightProbe>() {

                @Override
                public void done(LightProbe result) {

                }
            });

            ((BoundingSphere) probe.getBounds()).setRadius(2048);
            rootNode.addLight(probe);
        }

    }

    private void createLightProbe(Node rootNode, AppStateManager stateManager) {

        EnvironmentCamera environmentCamera = new EnvironmentCamera(256, Vector3f.ZERO);
        environmentCamera.setPosition(Vector3f.ZERO);
        environmentCamera.setEnabled(true);
        stateManager.attach(environmentCamera);
    }

    private void createShadow()
        {
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


    @Override
    public void onAnalog(String name, float value, float tpf) {

        if(name.equals("mouseClic")){
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();



            Vector3f pos = click3d.add(dir.normalize().mult(128f));

            for(ShipController ship : listShip){
                ship.setFinalDestination(pos);
            }


   }
    }
}