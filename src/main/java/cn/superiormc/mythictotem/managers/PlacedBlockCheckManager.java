package cn.superiormc.mythictotem.managers;

public class PlacedBlockCheckManager {

    private final int row;

    private final int column;

    private final int layer;

    private final TotemManager totemManager;

    public PlacedBlockCheckManager(TotemManager totemManager, int row, int column){
        this.totemManager = totemManager;
        this.row = row;
        this.column = column;
        this.layer = 1;
    }

    public PlacedBlockCheckManager(TotemManager totemManager, int row, int column, int layer){
        this.totemManager = totemManager;
        this.row = row;
        this.column = column;
        this.layer = layer;
    }

    public int GetRow(){
        return this.row;
    }

    public int GetColumn(){
        return this.column;
    }

    public int GetLayer(){
        return this.layer;
    }

    public TotemManager GetTotemManager() {
        return this.totemManager;
    }

}
