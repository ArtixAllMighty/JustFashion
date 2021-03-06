package subaraki.fashion.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import lib.util.networking.IPacketBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.network.ClientReferencesPacket;
import subaraki.fashion.network.NetworkHandler;

public class PacketSyncFashionToTrackedPlayers implements IPacketBase {

    public ResourceLocation[] ids;
    public boolean isActive;
    public UUID sender;
    public List<String> layers = new ArrayList<String>();

    public PacketSyncFashionToTrackedPlayers() {

    }

    public PacketSyncFashionToTrackedPlayers(ResourceLocation[] ids, boolean isActive, UUID sender, List<String> layers) {

        this.ids = ids;
        this.sender = sender;
        this.isActive = isActive;
        this.layers = layers;

    }

    public PacketSyncFashionToTrackedPlayers(PacketBuffer buf) {

        decode(buf);

    }

    @Override
    public void encode(PacketBuffer buf) {

        for (ResourceLocation resLoc : ids)
            if (resLoc != null)
                buf.writeString(resLoc.toString());
            else
                buf.writeString("missing");

        buf.writeBoolean(isActive);
        buf.writeUniqueId(sender);

        buf.writeInt(layers == null ? 0 : layers.isEmpty() ? 0 : layers.size());

        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                buf.writeString(layer);
            }
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            ClientReferencesPacket.handle(ids, isActive, sender, layers);

        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void decode(PacketBuffer buf) {

        ids = new ResourceLocation[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = new ResourceLocation(buf.readString(256));

        isActive = buf.readBoolean();

        sender = buf.readUniqueId();

        int size = buf.readInt();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                layers.add(buf.readString(128));
        }
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSyncFashionToTrackedPlayers.class, PacketSyncFashionToTrackedPlayers::encode,
                PacketSyncFashionToTrackedPlayers::new, PacketSyncFashionToTrackedPlayers::handle);
    }

}
