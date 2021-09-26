package Classes;

import EnumClasses.MapDirection;
import Interfaces.IWorldMap;

import java.util.*;

public class RectangularMap implements IWorldMap {
    public final int width;
    public final int height;
    public final int jungleTopY;
    public final int jungleBottomY;
    public final int jungleLeftX;
    public final int jungleRightX;
    public final int moveEnergy;
    public final int startEnergy;
    public final int plantEnergy;
    public final int jungleRatio;

    public HashMap<Vector2d, TreeMap<Integer, LinkedList<Animal>>> animals;
    public HashMap<Vector2d, Grass> plants;


    public RectangularMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, int jungleRatio) {
        this.width = width;
        this.height = height;
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        this.jungleBottomY = height/2 - (height*jungleRatio)/200;
        this.jungleTopY = height/2 + (height*jungleRatio)/200;
        this.jungleLeftX = width/2 - (width*jungleRatio)/200;
        this.jungleRightX = width/2 + (width*jungleRatio)/200;
        this.animals = new HashMap<>();
        this.plants = new HashMap<>();

    }




    @Override
    public boolean placeAnimal(Animal animal, Vector2d position) {
        if (!isOccupiedByPlant(position)) {
            if (animals.get(position) == null) {
                animals.put(position, new TreeMap<Integer, LinkedList<Animal>>());
                animals.get(position).put(animal.getEnergy(), new LinkedList<Animal>());
            } else {
                if(animals.get(position).get(animal.getEnergy()) == null){
                    animals.get(position).put(animal.getEnergy(), new LinkedList<Animal>());
                }
            }
            animals.get(position).get(animal.getEnergy()).add(animal);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAnimal(Animal animal, Vector2d position) {
        if (animals.get(position) == null) {
            return false;
        } else {
            animals.get(position).remove(animal.getEnergy(), animal);
            return true;
        }
    }
    @Override
    public boolean placeGrass(Grass grass, Vector2d position) {
        if (!isOccupiedByAnimal(position) && !isOccupiedByPlant(position)) {
            plants.put(position, grass);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean removeGrass(Grass grass, Vector2d position) {
        if (plants.get(position) == null) {
            return false;
        } else {
            this.plants.remove(position);
            return true;
        }
    }
    @Override
    public void removeDeadAnimals() {
        for (TreeMap<Integer, LinkedList<Animal>> group : animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for(Animal animal : animalsWithSameEnergy){
                    if( animal.isAlive()){
                        if (animal.getEnergy() <= 0) {
                            animal.setAlive(false);
                            animal.setPosition(new Vector2d(width,height));
                        }
                        else{
                            animal.lifeLength++;
                        }
                    }
                }
            }
        }
    }
    @Override
    public void move() {
        for (TreeMap<Integer, LinkedList<Animal>> group : animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for(Animal animal : animalsWithSameEnergy){
                    if(animal.isAlive()){
                        animal.move();
                        update();
                    }
                }
            }
        }
    }
    public void update(){
        HashMap<Vector2d, TreeMap<Integer, LinkedList<Animal>>> newAnimals = new HashMap<>();
        for (TreeMap<Integer, LinkedList<Animal>> group : animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for(Animal animal : animalsWithSameEnergy){
                    if (newAnimals.get(animal.getPosition()) == null) {
                        newAnimals.put(animal.getPosition(), new TreeMap<Integer, LinkedList<Animal>>());
                        newAnimals.get(animal.getPosition()).put(animal.getEnergy(), new LinkedList<Animal>());
                    } else {
                        if(newAnimals.get(animal.getPosition()).get(animal.getEnergy()) == null){
                            newAnimals.get(animal.getPosition()).put(animal.getEnergy(), new LinkedList<Animal>());
                        }
                    }
                    newAnimals.get(animal.getPosition()).get(animal.getEnergy()).add(animal);
                }
            }
        }
        animals = newAnimals;
    }


    @Override
    public void eating() {
        for (Iterator<Grass> iterator = plants.values().iterator(); iterator.hasNext();) {
            Grass grass = iterator.next();
            TreeMap<Integer, LinkedList<Animal>> group = this.animals.get(grass.getPosition());

            if (group != null) {
                int lastEnergy = group.get(group.firstKey()).element().getEnergy();
                LinkedList<Animal> animalsWithSameEnergy = group.get(group.firstKey());
                LinkedList<Animal> newAnimalList = new LinkedList<>();
                int n = animalsWithSameEnergy.size();

                for(Animal animal : animalsWithSameEnergy){
                    animal.setEnergy(animal.getEnergy() + plantEnergy/n);
                    newAnimalList.add(animal);
                }

                animals.get(newAnimalList.get(0).getPosition()).put(newAnimalList.get(0).getEnergy(), newAnimalList);
                animals.get(grass.getPosition()).remove(lastEnergy);
                iterator.remove();
            }
        }
    }

    @Override
    public void copulation() {
        LinkedList<Animal> animalsToPlace = new LinkedList<>();
        for (TreeMap<Integer, LinkedList<Animal>> group : animals.values()) {
            LinkedList<Animal> animalsWithHighestEnergy = group.get(group.lastKey());

            if (animalsWithHighestEnergy.size() >= 2) {
                Animal parent1 = animalsWithHighestEnergy.get(0);
                Animal parent2 = animalsWithHighestEnergy.get(1);
                if (parent1.getEnergy() > startEnergy / 2 && parent2.getEnergy() > startEnergy / 2) {
                    Vector2d childrenPosition = generateChildrenPosition(parent1.getPosition());
                    Animal children = new Animal(childrenPosition, childrenPosition, this, parent1.getEnergy() / 4 + parent2.getEnergy() / 4, parent1.getGenes().generateChildrenGenes(parent2.getGenes()), MapDirection.generateDirection());
                    animalsToPlace.add(children);
                    parent1.setEnergy(3 * parent1.getEnergy() / 4);
                    parent2.setEnergy(3 * parent2.getEnergy() / 4);
                    parent1.children.add(children);
                    parent2.children.add(children);
                }
            } else {
                if (group.size() > 1) {
                    LinkedList<Animal> animalsWithSecondHighestEnergy = group.entrySet().stream()
                            .skip(group.size() - 2)
                            .map(map -> map.getValue()).findFirst().get();
                    Animal parent1 = animalsWithHighestEnergy.get(0);
                    Animal parent2 = animalsWithSecondHighestEnergy.get(0);
                    if (parent1.getEnergy() > startEnergy / 2 && parent2.getEnergy() > startEnergy / 2) {
                        Vector2d childrenPosition = generateChildrenPosition(parent1.getPosition());
                        Animal children = new Animal(childrenPosition, childrenPosition, this, parent1.getEnergy() / 4 + parent2.getEnergy() / 4, parent1.getGenes().generateChildrenGenes(parent2.getGenes()), MapDirection.generateDirection());
                        animalsToPlace.add(children);
                        parent1.setEnergy(3 * parent1.getEnergy() / 4);
                        parent2.setEnergy(3 * parent2.getEnergy() / 4);
                        parent1.children.add(children);
                        parent2.children.add(children);
                    }

                }

            }
        }
        for (Animal animal : animalsToPlace) {
            placeAnimal(animal, animal.getPosition());
        }

        update();

    }
    @Override
    public void addGrass() {
        Vector2d pos = generatePlantPosition(true);
        if (pos != null){
            placeGrass(new Grass(pos,this), pos);
        }
        pos = generatePlantPosition(false);
        if (pos != null){
            placeGrass(new Grass(pos,this), pos);
        }
    }

    @Override
    public Vector2d generateChildrenPosition(Vector2d position) {
        int ndir = (int) (Math.random() * 8);
        MapDirection dir = MapDirection.numberToDirection(ndir);
        MapDirection idir = dir;
        Vector2d iposition = position;
        position = iposition.add(dir.toUnitVector());
        while(isOccupiedByAnimal(position)||isOccupiedByPlant(position)){
            dir = dir.next();
            position = iposition.add(dir.toUnitVector());
            if(dir==idir){
                break;
            }
        }
        while (isOccupiedByPlant(position)){
            dir = dir.next();
            position = iposition.add(dir.toUnitVector());
        }
        return position;

    }

    @Override
    public Vector2d generateVector(boolean jungle) {

        if (jungle) {
            return new Vector2d((int) (Math.random() * (jungleRightX - jungleLeftX )) +jungleLeftX, (int) (Math.random() * (jungleTopY - jungleBottomY)) + jungleBottomY);
        } else {
            int a = (int) (Math.random() * 4);
            switch (a) {
                case 0 -> {
                    return new Vector2d((int) (Math.random() * (width - jungleLeftX) + jungleLeftX), (int) (Math.random() * (jungleBottomY)));
                }
                case 1 -> {
                    return new Vector2d((int) (Math.random() * (width - jungleRightX) + jungleRightX), (int) (Math.random() * (height - jungleBottomY + jungleBottomY)));
                }
                case 2 -> {
                    return new Vector2d((int) (Math.random() * (jungleRightX)), (int) (Math.random() * (height - jungleTopY) + jungleTopY));
                }
                case 3 -> {
                    return new Vector2d(((int) (Math.random() * (jungleLeftX))), (int) (Math.random() * (jungleTopY)));
                }
            }
        }
        return null;
    }

    @Override
    public Vector2d generatePlantPosition(boolean jungle) {
        Vector2d pos = generateVector(jungle);
        Vector2d ipos = new Vector2d(pos.x,pos.y);
        if(jungle){
            while (isOccupiedByPlant(pos) || isOccupiedByAnimal(pos)) {
                pos = pos.add(MapDirection.E.toUnitVector());
                if(! isInJungle(pos)){
                    pos = new Vector2d(jungleLeftX, pos.y+1);
                }
                if(pos.y==jungleTopY){
                    pos = new Vector2d(jungleLeftX, jungleBottomY);
                }
                if(pos.x==ipos.x && pos.y==ipos.y){
                    return null;
                }
            }
        }
        else{
            while (isOccupiedByPlant(pos) || isOccupiedByAnimal(pos)) {
                pos = pos.add(MapDirection.E.toUnitVector());
                pos = pos.maintainOnMap(this);
                if(isInJungle(pos)){
                    pos = new Vector2d(jungleRightX, pos.y);
                }
                if(pos.x==0){
                    pos = new Vector2d(pos.x,pos.y+1);
                    pos = pos.maintainOnMap(this);
                }
                if(pos.x==ipos.x && pos.y==ipos.y){
                    return null;
                }
            }
        }
        return pos;
    }

    public boolean isAnyAlive() {
        for (TreeMap<Integer, LinkedList<Animal>> group : animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for (Animal animal : animalsWithSameEnergy) {
                    if(animal.isAlive()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInJungle(Vector2d pos){
        if(pos.x>=jungleLeftX && pos.x<jungleRightX && pos.y>=jungleBottomY && pos.y<jungleTopY){
            return true;
        }
        return false;
    }

    @Override
    public boolean isOccupiedByPlant(Vector2d position) {
        return plants.get(position) != null;
    }

    @Override
    public boolean isOccupiedByAnimal(Vector2d position) {
        return animals.get(position)!=null;
    }




}



