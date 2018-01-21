package opera.appstates;

import opera.controllers.ShipBaseController;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import opera.modeles.SpatialModels;

import java.util.ArrayList;
import java.util.List;

/* ShipManager
   AppState qui gère les vaisseaux (ajout, suppression, sélection, ordre)
 */

public class ShipManager extends BaseAppState {

    private SimpleApplication simpleApplication;

    private List<ShipBaseController> listShipOwner;

    private List<ShipBaseController> listShipEnnemy;

    @Override
    protected void initialize(Application app) {

        simpleApplication = (SimpleApplication)app;
        listShipOwner = new ArrayList<ShipBaseController>();
        listShipEnnemy = new ArrayList<ShipBaseController>();
        // preparation des spatials
        SpatialModels.getInstance().prepareSpatials(simpleApplication.getAssetManager());
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



    @Override
    public void update(float tpf) {
        super.update(tpf);
    }
}
