package Interfaces;

import Classes.Animal;
import Classes.Grass;
import Classes.Vector2d;
import EnumClasses.MapDirection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo
 *
 */

public interface IWorldMap {
    int width = 0;
    int height = 0;
    int moveEnergy = 0;
    int startEnergy = 0;
    int plantEnergy = 0;
    int jungleRatio = 0;

    public HashMap<Vector2d, TreeMap<Integer, LinkedList<Animal>>> animals = null;
    public HashMap<Vector2d, Grass> plants = null;




    boolean placeAnimal(Animal animal, Vector2d position);

    boolean removeAnimal(Animal animal, Vector2d position);

    boolean placeGrass(Grass grass, Vector2d position);

    boolean removeGrass(Grass grass, Vector2d position);

    void removeDeadAnimals();

    void move();

    void eating();

    void copulation();

    void addGrass();

    Vector2d generateChildrenPosition(Vector2d position);

    Vector2d generateVector(boolean jungle);

    Vector2d generatePlantPosition(boolean jungle);

    boolean isOccupiedByPlant(Vector2d position);

    boolean isOccupiedByAnimal(Vector2d position);




}