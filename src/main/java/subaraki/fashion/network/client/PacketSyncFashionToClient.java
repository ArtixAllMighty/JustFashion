package subaraki.fashion.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lib.util.ClientReferences;
import lib.util.networking.IPacketBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.network.NetworkHandler;

public class PacketSyncFashionToClient implements IPacketBase {

    public ResourceLocation[] ids = new ResourceLocation[6];
    public boolean isActive;
    public List<String> toKeep = new ArrayList<String>();

    public PacketSyncFashionToClient() {

    }

    public PacketSyncFashionToClient(ResourceLocation[] ids, List<String> keepLayers, boolean isActive) {

        this.ids = ids;
        this.isActive = isActive;
        this.toKeep = keepLayers;
    }

    public PacketSyncFashionToClient(PacketBuffer buf) {

        decode(buf);
    }

    @Override
    public void decode(PacketBuffer buf) {

        isActive = buf.readBoolean();

        ids = new ResourceLocation[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = new ResourceLocation(buf.readString(256));

        int size = buf.readInt();

        if (size > 0)
            for (int i = 0; i < size; i++)
                toKeep.add(buf.readString(128));
    }

    @Override
    public void encode(PacketBuffer buf) {

        buf.writeBoolean(isActive);

        for (ResourceLocation resLoc : ids)
            if (resLoc != null)
                buf.writeString(resLoc.toString());
            else
                buf.writeString("missing");

        buf.writeInt(toKeep.size());

        if (!toKeep.isEmpty())
            for (String s : toKeep)
                buf.writeString(s);

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            FashionData.get(ClientReferences.getClientPlayer()).ifPresent(data -> {

                for (EnumFashionSlot slot : EnumFashionSlot.values())
                    data.updateFashionSlot(ids[slot.ordinal()], slot);

                data.setRenderFashion(isActive);

                try {
                    List<LayerRenderer<?, ?>> list = subaraki.fashion.client.ClientReferences.tryList();

                    if (!list.isEmpty())
                        for (LayerRenderer<?, ?> layer : list)
                            for (String classname : toKeep)
                                if (layer.getClass().getSimpleName().equals(classname))
                                    data.keepLayers.add(layer);

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSyncFashionToClient.class, PacketSyncFashionToClient::encode, PacketSyncFashionToClient::new,
                PacketSyncFashionToClient::handle);
    }
}
