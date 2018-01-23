package opera.appstates;

import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Line;
import opera.controllers.ShipBaseController;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import opera.controllers.ShipController;
import opera.modeles.SelectedShips;
import opera.modeles.SpatialModels;
import java.util.ArrayList;
import java.util.List;

/* ShipManager
   AppState qui gère les vaisseaux (ajout, suppression, sélection, ordre)
 */

public class ShipManagerAppState extends BaseAppState implements ActionListener,AnalogListener {

    private SimpleApplication simpleApplication;

    private List<ShipBaseController> listShipOwner;

    private List<ShipBaseController> listShipEnnemy;

    private Node rootNodeShipOwner;

    private Node rootNodeShipEnnemy;

    private SelectedShips selectedShips = new SelectedShips();

    private Vector3f positionDestinatinoXZ = new Vector3f();
    private Vector3f positionDestinatinonY = new Vector3f();
    private boolean  searchDestinationZ = false;

    @Override
    protected void initialize(Application app) {

        simpleApplication = (SimpleApplication)app;
        listShipOwner = new ArrayList<ShipBaseController>();
        listShipEnnemy = new ArrayList<ShipBaseController>();
        // creation des nodes owner et ennemy
        rootNodeShipOwner = new Node();
        rootNodeShipOwner.setCullHint(Spatial.CullHint.Never);
        rootNodeShipEnnemy = new Node();
        rootNodeShipEnnemy.setCullHint(Spatial.CullHint.Never);
        simpleApplication.getRootNode().attachChild(rootNodeShipOwner);
        simpleApplication.getRootNode().attachChild(rootNodeShipEnnemy);
        // preparation des spatials
        SpatialModels.getInstance().prepareSpatials(simpleApplication.getAssetManager());
        createShipTest(new Vector3f(0,0,0));
        // preparation des inputs
        simpleApplication.getInputManager().addMapping("MOUSELEFT",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        simpleApplication.getInputManager().addListener(this,"MOUSELEFT");
        simpleApplication.getInputManager().addMapping("MOUSERIGHT",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        simpleApplication.getInputManager().addListener(this,"MOUSERIGHT");
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    /**
     * @param pos - position du vaisseaux
     */
    private void createShipTest(Vector3f pos){
        Spatial ship = SpatialModels.getInstance().getSpatialMap().get(SpatialModels.MODEL_SHIP_01).clone();
        ship.setLocalTranslation(pos);
        ShipController sc = new ShipController();
        ship.addControl(sc);
        rootNodeShipOwner.attachChild(ship);
        listShipOwner.add(sc);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    /**
     * @param name - nom de l'action
     * @param isPressed - si le bouton est pressé
     * @param tpf - temps écoulé entre deux frames
     */
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        if(name.equals("MOUSELEFT") && isPressed){
            selectedShips.clear();
            selectedShips = selectShip();
        }

        if(name.equals("MOUSERIGHT") && isPressed && !searchDestinationZ){
            if(selectedShips != null && selectShip().size() > 0){
                searchDestinationXZ(selectedShips);
            }
        }

        if(name.equals("MOUSERIGHT") && !isPressed && searchDestinationZ){
            if(selectedShips != null && selectShip().size() > 0){
                searchDestinationY(selectedShips,positionDestinatinoXZ);
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {

    }

    /**
     *
     * @param selectedShips liste des vaisseaux sélectionnés
     * @param destinationXY destination en profondeur sur le plan du vaisseau déja déterminée
     */
    private void searchDestinationY(SelectedShips selectedShips,Vector3f destinationXY) {
        ShipBaseController ship = (ShipBaseController) selectedShips.get(0);
        Plane plane = new Plane();
        Vector3f planeNormal = ship.getSpatial().getWorldTranslation().clone().subtract(destinationXY).normalize();
        plane.setOriginNormal(destinationXY,planeNormal);
        Vector2f screen2d = simpleApplication.getInputManager().getCursorPosition();
        Vector3f start = simpleApplication.getCamera().getLocation().clone();
        Vector3f end = simpleApplication.getCamera().getWorldCoordinates(screen2d,1f).clone();
        Vector3f diff = end.subtract(start);
        diff.normalizeLocal();
        Ray r = new Ray(start,diff);
        if(r.intersectsWherePlane(plane,positionDestinatinonY)){
            positionDestinatinonY.x = destinationXY.x;
            positionDestinatinonY.z = destinationXY.z;
            Spatial path = SpatialModels.getInstance().getSpatialMap().get(SpatialModels.MODEL_PATH_FINAL).clone();
            BillboardControl bc = new BillboardControl();
            bc.setAlignment(BillboardControl.Alignment.Camera);
            path.addControl(bc);
            path.setLocalTranslation(positionDestinatinonY);
            rootNodeShipOwner.attachChild(path);
            Line line = new Line(destinationXY,positionDestinatinonY);
            Geometry geo = new Geometry();
            Material mat = new Material(simpleApplication.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Yellow);
            geo.setMaterial(mat);
            geo.setMesh(line);
            rootNodeShipOwner.attachChild(geo);
        }
        searchDestinationZ = false;
    }

    /**
     *
     * @param selectedShips - liste des vaisseaux selectionnés
     */
    private void searchDestinationXZ(SelectedShips selectedShips) {
        Vector2f screen2d = simpleApplication.getInputManager().getCursorPosition();
        Vector3f start = simpleApplication.getCamera().getLocation().clone();
        Vector3f end = simpleApplication.getCamera().getWorldCoordinates(screen2d,1f).clone();
        Vector3f diff = end.subtract(start);
        diff.normalizeLocal();
        // placement d'un plane temporaire permettant d'effectuer la première recherche en profondeur sur l'axe du vai sseau
        ShipBaseController ship = (ShipBaseController) selectedShips.get(0);
        Plane plane = new Plane();
        plane.setOriginNormal(ship.getSpatial().getLocalTranslation(),Vector3f.UNIT_Y);
        Ray r = new Ray(start,diff);
        if(r.intersectsWherePlane(plane,positionDestinatinoXZ)){
            Spatial path = SpatialModels.getInstance().getSpatialMap().get(SpatialModels.MODEL_PATH).clone();
            path.setLocalTranslation(positionDestinatinoXZ);
            rootNodeShipOwner.attachChild(path);
            Line line = new Line(ship.getSpatial().getWorldTranslation(),positionDestinatinoXZ);
            Geometry geo = new Geometry();
            Material mat = new Material(simpleApplication.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geo.setMaterial(mat);
            geo.setMesh(line);
            rootNodeShipOwner.attachChild(geo);
            searchDestinationZ = true;
        }
    }

    /**
     * @return Vaisseaux sélectionnés
     */
    private SelectedShips selectShip(){
        Vector2f screen2d = simpleApplication.getInputManager().getCursorPosition();
        Vector3f screen3d = simpleApplication.getCamera().getWorldCoordinates(screen2d,0);
        Vector3f dir = simpleApplication.getCamera().getWorldCoordinates(screen2d,1f).clone().normalize();
        Ray r = new Ray();
        r.setOrigin(screen3d);
        r.setDirection(dir);
        CollisionResults results = new CollisionResults();
          for(ShipBaseController sc : listShipOwner) {
              if (sc.getSpatial().collideWith(r, results) > 0) {
                  selectedShips.add(sc);
              }
          }
          return selectedShips;
    }

}
