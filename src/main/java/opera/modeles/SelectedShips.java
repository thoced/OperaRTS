package opera.modeles;

import java.util.ArrayList;
import java.util.ListIterator;

import opera.controllers.ShipBaseController;

/**
 * @author Thoced
 * @see class dérivée de ArrayList, contient la liste des vaisseaux sélectionné
 * @param <ShipBaseController>
 */
public class SelectedShips<Ship> extends ArrayList<ShipBaseController> {

    @Override
    public boolean add(ShipBaseController shipBaseController) {
        shipBaseController.setSelected(true);

     return super.add(shipBaseController);

    }

    @Override
    public void clear() {
        ListIterator<ShipBaseController> list = this.listIterator();
        while(list.hasNext()){
            list.next().setSelected(false);
        }
        super.clear();
    }
}
