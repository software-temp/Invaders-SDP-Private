package screen;


public class HealthBar {
    private final float maxHP;
    private float current_HP;
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public HealthBar(int HP, int positionX, int positionY, int width, int height){
        this.maxHP = HP;
        this.current_HP = HP;
        this.x1 = positionX;
        this.y1 = positionY - 3;
        this.x2 = positionX + width;
        this.y2 = this.y1;
    }
    public void setCurrent_HP(int current_HP){
        this.current_HP = current_HP;
    }
    public void setPosition(int positionX, int positionY, int width){
        this.x1 = positionX;
        this.y1 = positionY - 3;
        this.x2 = positionX + width;
        this.y2 = this.y1;
    }

    public float getRatio_HP(){
        return this.current_HP / this.maxHP;
    }

    public int[] getPosition(){
        int [] position = {this.x1,this.y1,this.x2,this.y2};
        return position;
    }
    public int getWidth(){
        return x2 - x1;
    }



}
