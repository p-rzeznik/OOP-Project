package Classes;

import Interfaces.IMapElement;
import Interfaces.IWorldMap;

public class Grass implements IMapElement {
    private final Vector2d position;
    private IWorldMap map;



    public Grass(Vector2d position, IWorldMap map) {
        this.position = position;
        this.map = map;
    }

    public Vector2d getPosition() {
        return position;
    }

}
