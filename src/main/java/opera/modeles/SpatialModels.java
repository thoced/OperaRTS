package opera.modeles;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

import java.util.HashMap;

public class SpatialModels{

    private static SpatialModels instance = null;

    private AssetManager assetManager;

    private HashMap<String,Spatial> spatialMap;

    public static final String MODEL_SHIP_01 = "Modeles/spaceship01/vaisseau01.j3o";
    public static final String MODEL_PATH = "Modeles/utils/path/path.j3o";
    public static final String MODEL_PATH_FINAL = "Modeles/utils/path/pathFinal.j3o";
    public static final String MODEL_SELECT = "Modeles/utils/select/select.j3o";


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
        Spatial s = this.assetManager.loadModel(MODEL_SHIP_01);
        spatialMap.put(MODEL_SHIP_01,s);
        s = this.assetManager.loadModel(MODEL_PATH);
        spatialMap.put(MODEL_PATH,s);
        s = this.assetManager.loadModel(MODEL_PATH_FINAL);
        spatialMap.put(MODEL_PATH_FINAL,s);
        s = this.assetManager.loadModel(MODEL_SELECT);
        spatialMap.put(MODEL_SELECT,s);

    }

    public HashMap<String, Spatial> getSpatialMap() {
        return spatialMap;
    }
}
