package cn.superiormc.mythictotem.libreforge;

import cn.superiormc.mythictotem.api.TotemActivedEvent;
import com.willfp.libreforge.*;
import com.willfp.libreforge.triggers.Trigger;
import com.willfp.libreforge.triggers.TriggerData;
import com.willfp.libreforge.triggers.TriggerParameter;
import com.willfp.libreforge.triggers.Triggers;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TriggerTotemActived extends Trigger {

    public static void load() {
        Triggers.INSTANCE.register(new TriggerTotemActived());
    }

    public TriggerTotemActived() {
        super("totem_actived");
    }


    @NotNull
    @Override
    public Set<TriggerParameter> getParameters() {
        Set<TriggerParameter> data = new HashSet<>();
        data.add(TriggerParameter.PLAYER);
        data.add(TriggerParameter.BLOCK);
        data.add(TriggerParameter.LOCATION);
        data.add(TriggerParameter.TEXT);
        return data;
    }

    @EventHandler
    public void handle(TotemActivedEvent event) {
        Player player = event.GetPlayer();
        Location location = event.GetLocation();
        Block block = location.getBlock();
        String text = event.GetTotemID();
        ProvidedHolder holder = new ProvidedHolder() {
            @Override
            public boolean isShowingAnyNotMet(@NotNull Player player) {
                return false;
            }

            @Override
            public boolean isShowingAnyNotMet(@NotNull Dispatcher<?> dispatcher) {
                return false;
            }

            @NotNull
            @Override
            public List<String> getNotMetLines(@NotNull Player player) {
                return null;
            }

            @NotNull
            @Override
            public List<String> getNotMetLines(@NotNull Dispatcher<?> dispatcher) {
                return null;
            }

            @NotNull
            @Override
            public Holder getHolder() {
                return null;
            }

            @Nullable
            @Override
            public Object getProvider() {
                return null;
            }

            @NotNull
            @Override
            public Holder component1() {
                return null;
            }

            @Nullable
            @Override
            public Object component2() {
                return null;
            }
        };
        TriggerData data = new TriggerData(holder,
                DispatcherKt.toDispatcher(player),
                player,
                null,
                block,
                null,
                location,
                null,
                null,
                null,
                text,
                1,
                player);
        this.dispatch(DispatcherKt.toDispatcher(player), data, null);
    }
}
