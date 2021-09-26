package Classes;

import EnumClasses.MapDirection;
import Interfaces.IMapElement;

import java.util.ArrayList;
import java.util.List;

public class Animal  implements IMapElement {
    private Vector2d position;
    private final Vector2d initialPosition;
    private final RectangularMap map;
    private MapDirection direction;
    private int energy;
    private final Genes genes;
    private boolean alive = true;
    public List<Animal> children = new ArrayList<>();
    public int lifeLength =0;
    public boolean visited = false;



    public Animal(Vector2d position, Vector2d initialPosition, RectangularMap map, int energy, Genes genes, MapDirection direction) {
        this.position = position;
        this.initialPosition = initialPosition;
        this.map = map;
        this.energy = energy;
        this.genes = genes;
        this.direction = direction;
    }


    public Genes getGenes() {
        return genes;
    }

    public Vector2d getInitialPosition() {
        return initialPosition;
    }

    public Vector2d getPosition() {
        return position;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int getEnergy() {
        return energy;
    }

    public int getDescendants(){
        int res = 0;
        for(Animal animal :children){
            if(!animal.visited){
                animal.visited = true;
                res = 1 + animal.getDescendants();
            }
        }
        return res;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }



    public void move(){
        int turn = this.getGenes().getRandomGene();
        while (turn > 0) {
            this.direction = this.direction.next();
            turn--;
        }
        this.position = this.position.add(this.direction.toUnitVector());
        this.position = this.position.maintainOnMap((RectangularMap) map);
        this.setEnergy(this.getEnergy() - map.moveEnergy);
    }







}