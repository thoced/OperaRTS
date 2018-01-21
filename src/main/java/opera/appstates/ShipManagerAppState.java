package opera.appstates;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javafx.scene.input.MouseButton;
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

    private SelectedShips selectedShips;

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
     *
     * @param name - nom de l'action
     * @param isPressed - si le bouton est pressé
     * @param tpf - temps écoulé entre deux frames
     */
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        if(name.equals("MOUSELEFT") && isPressed){
            selectedShips = selectShip();

        }
    }

    /**
     * @return Vaisseaux sélectionnés
     *
     */
    private SelectedShips selectShip(){
        SelectedShips shipsSelected = new SelectedShips();
        Vector2f screen2d = simpleApplication.getInputManager().getCursorPosition();
        Vector3f screen3d = simpleApplication.getCamera().getWorldCoordinates(screen2d,0);
        Vector3f dir = simpleApplication.getCamera().getWorldCoordinates(screen2d,1f).clone().normalize();
        Ray r = new Ray();
        r.setOrigin(screen3d);
        r.setDirection(dir);
        CollisionResults results = new CollisionResults();
          for(ShipBaseController sc : listShipOwner) {
              if (sc.getSpatial().collideWith(r, results) > 0) {
                  shipsSelected.add(sc);
              }
          }
          return shipsSelected;
    }
}
