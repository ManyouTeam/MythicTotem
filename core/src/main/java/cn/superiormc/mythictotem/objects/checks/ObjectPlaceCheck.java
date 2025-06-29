package cn.superiormc.mythictotem.objects.checks;

import cn.superiormc.mythictotem.objects.ObjectTotem;
import org.jetbrains.annotations.NotNull;

public class ObjectPlaceCheck {

    private final int row;

    private final int column;

    private final int layer;

    private final ObjectTotem totem;

    public ObjectPlaceCheck(ObjectTotem totemManager, int row, int column){
        this.totem = totemManager;
        this.row = row;
        this.column = column;
        this.layer = 1;
    }

    public ObjectPlaceCheck(ObjectTotem totemManager, int row, int column, int layer){
        this.totem = totemManager;
        this.row = row;
        this.column = column;
        this.layer = layer;
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }

    public int getLayer(){
        return this.layer;
    }

    @NotNull
    public ObjectTotem getTotem() {
        return this.totem;
    }

}
