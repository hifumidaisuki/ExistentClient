package zyx.existent.event.events;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.event.Event;

public class EventModelUpdate extends Event {
    private final EntityPlayer player;
    private final ModelPlayer model;

    public EventModelUpdate(EntityPlayer player, ModelPlayer model) {
        this.player = player;
        this.model = model;
    }

    public final EntityPlayer getPlayer() {
        return this.player;
    }

    public final ModelPlayer getModel() {
        return this.model;
    }
}
