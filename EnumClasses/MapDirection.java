package EnumClasses;

import Classes.Vector2d;

public enum MapDirection {
    N, NE, E, SE, S, SW, W, NW;

    @Override
    public String toString() {
        switch (this){
            case N -> {
                return "Północ";
            }
            case NE -> {
                return  "Północny-Wschód";
            }
            case E -> {
                return "Wschód";
            }
            case SE -> {
                return "Południowy-Wschód";
            }
            case S -> {
                return "Południe";
            }
            case SW -> {
                return "Południowy-Zachód";
            }
            case W -> {
                return "Zachód";
            }
            case NW -> {
                return "Północny-Zachód";
            }
            default -> {
                throw new RuntimeException();
            }
        }
    }

    public int directionToNumber(){
        return this.ordinal();
    }

    public static MapDirection numberToDirection(int n){
        for(MapDirection value :MapDirection.values()){
            if(value.ordinal()==n){
                return value;
            }
        }
        return null;
    }

    public MapDirection next(){
        switch (this){
            case N -> {
                return NE;
            }
            case NE -> {
                return  E;
            }
            case E -> {
                return SE;
            }
            case SE -> {
                return S;
            }
            case S -> {
                return SW;
            }
            case SW -> {
                return W;
            }
            case W -> {
                return NW;
            }
            case NW -> {
                return N;
            }
            default -> {
                throw new RuntimeException();
            }
        }
    }
    public MapDirection previous(){
        switch (this){
            case N -> {
                return NW;
            }
            case NE -> {
                return N;
            }
            case E -> {
                return NE;
            }
            case SE -> {
                return E;
            }
            case S -> {
                return SE;
            }
            case SW -> {
                return S;
            }
            case W -> {
                return SW;
            }
            case NW -> {
                return W;
            }
            default -> {
                throw new RuntimeException();
            }
        }
    }

    public Vector2d toUnitVector(){
        switch (this){
            case N -> {
                return new Vector2d(0,1);
            }
            case NE -> {
                return new Vector2d(1,1);
            }
            case E -> {
                return new Vector2d(1,0);
            }
            case SE -> {
                return new Vector2d(1,-1);
            }
            case S -> {
                return new Vector2d(0,-1);
            }
            case SW -> {
                return new Vector2d(-1,-1);
            }
            case W -> {
                return new Vector2d(-1,0);
            }
            case NW -> {
                return new Vector2d(-1,1);
            }
            default -> {
                throw new RuntimeException();
            }
        }
    }
    public static MapDirection generateDirection(){
        return MapDirection.values()[(int) (Math.random()* MapDirection.values().length)];
    }
}
