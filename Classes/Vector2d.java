package Classes;


public class Vector2d {

    public final int x;
    public final int y;



    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("(%d, %d) ", x, y);
    }



    public Vector2d add(Vector2d other){
        return new Vector2d(this.x+ other.x, this.y+ other.y);
    }



    public Vector2d substract(Vector2d other){
        return new Vector2d(this.x - other.x, this.y - other.y);
    }


    public Vector2d maintainOnMap(RectangularMap map){
        return new Vector2d((this.x + map.width)% map.width, (this.y + map.height)%map.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return x == vector2d.x &&
                y == vector2d.y;
    }


    @Override

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
        return hash;
    }

}

