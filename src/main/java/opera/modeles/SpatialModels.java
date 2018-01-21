package opera.modeles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

import java.util.HashMap;

public class SpatialModels{

    private static SpatialModels instance;

    private AssetManager assetManager;

    private HashMap<String,Spatial> spatialMap;

    private  SpatialModels() {

        spatialMap = new HashMap<String, Spatial>();

    }

    public static SpatialModels getInstance(){
        if(instance == null){
            instance = new SpatialModels();
        }
        return instance;

    }

    public void prepareSpatials(AssetManager assetManager){
        this.assetManager = assetManager;

        Spatial s = this.assetManager.loadModel("Modeles/spaceship01/vaisseau01.j3o");
        spatialMap.put("SHIP01",s);

    }

}
