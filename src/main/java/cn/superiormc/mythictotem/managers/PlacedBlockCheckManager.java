package cn.superiormc.mythictotem.managers;

public class PlacedBlockCheckManager {

    private final int row;

    private final int column;

    private final TotemManager totemManager;

    public PlacedBlockCheckManager(TotemManager totemManager, int row, int column){
        this.totemManager = totemManager;
        this.row = row;
        this.column = column;
    }

    public int GetRow(){
        return this.row;
    }

    public int GetColumn(){
        return this.column;
    }

    public TotemManager GetTotemManager() {
        return this.totemManager;
    }

}
