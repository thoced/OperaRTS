package opera.appstates;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
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

public class ShipManagerAppState extends BaseAppState implements ActionListener {

    private SimpleApplication simpleApplication;

    private List<ShipBaseController> listShipOwner;

    private List<ShipBaseController> listShipEnnemy;

    private Node rootNodeShipOwner;

    private Node rootNodeShipEnnemy;

    private SelectedShips selectedShips = new SelectedShips();

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
     *
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

        if(name.equals("MOUSERIGHT") && isPressed){
            if(selectedShips != null && selectShip().size() > 0){
                moveShips(selectedShips);
            }
        }
    }

    /**
     *
     * @param selectedShips - liste des vaisseaux selectionnés
     */
    private void moveShips(SelectedShips selectedShips) {
        Vector2f screen2d = simpleApplication.getInputManager().getCursorPosition();
        Vector3f screen3d = simpleApplication.getCamera().getWorldCoordinates(screen2d,0);
        Vector3f dir = simpleApplication.getCamera().getWorldCoordinates(screen2d,1f).clone();
        // placement d'un quad temporaire permettant d'effectuer la première recherche en profondeur sur l'axe du vai sseau
        ShipBaseController ship = (ShipBaseController) selectedShips.get(0);
        Plane plane = new Plane();
        plane.setOriginNormal(ship.getSpatial().getLocalTranslation(),Vector3f.UNIT_Y);
        Ray r = new Ray(screen3d,dir);

        Vector3f contactPoint = new Vector3f();
        if(r.intersectsWherePlane(plane,contactPoint))
        {
            Spatial path = SpatialModels.getInstance().getSpatialMap().get(SpatialModels.MODEL_PATH).clone();
            path.setLocalTranslation(contactPoint);
            rootNodeShipOwner.attachChild(path);

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
