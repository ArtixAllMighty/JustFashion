package subaraki.fashion.client.render.layer;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import lib.modelloader.ModelHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.ForgeHooksClient;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;

@SuppressWarnings("deprecation")
public class LayerAestheticHeldItem extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private TransformType cam_right = ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    private TransformType cam_left = ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

    public LayerAestheticHeldItem(PlayerRenderer renderer) {

        super(renderer);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        FashionData.get(player).ifPresent(fashionData -> {

            ItemStack stackHeldItem = player.getHeldItemMainhand();
            ItemStack stackOffHand = player.getHeldItemOffhand();

            boolean renderedOffHand = false;
            boolean renderedHand = false;

            if (!fashionData.getRenderingPart(EnumFashionSlot.WEAPON).toString().contains("missing")) {
                if (stackHeldItem.getItem() instanceof SwordItem) {
                    renderAesthetic(stackHeldItem, player, HandSide.RIGHT, fashionData, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                            EnumFashionSlot.WEAPON);
                    renderedHand = true;
                }

                if (stackOffHand.getItem() instanceof SwordItem) {
                    renderAesthetic(stackOffHand, player, HandSide.LEFT, fashionData, cam_left, EnumFashionSlot.WEAPON);
                    renderedOffHand = true;
                }
            }

            if (!fashionData.getRenderingPart(EnumFashionSlot.SHIELD).toString().contains("missing")) {
                if (stackHeldItem.getItem() instanceof ShieldItem || stackHeldItem.getItem().getUseAction(stackHeldItem) == UseAction.BLOCK) {
                    renderAesthetic(stackHeldItem, player, HandSide.RIGHT, fashionData, cam_right, EnumFashionSlot.SHIELD);
                    renderedHand = true;
                }

                if (stackOffHand.getItem() instanceof ShieldItem || stackOffHand.getItem().getUseAction(stackOffHand) == UseAction.BLOCK) {
                    renderAesthetic(stackOffHand, player, HandSide.LEFT, fashionData, cam_left, EnumFashionSlot.SHIELD);
                    renderedOffHand = true;
                }
            }

            if (!renderedHand)
                renderHeldItem(player, stackHeldItem, cam_right, HandSide.RIGHT);
            if (!renderedOffHand)
                renderHeldItem(player, stackOffHand, cam_left, HandSide.LEFT);

        });
    }

    private static final Direction[] facings = { null, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN };

    private static void renderModel(IBakedModel model, VertexFormat fmt) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, fmt);

        for (Direction facing : facings) {
            for (BakedQuad bakedquad : model.getQuads(null, facing, new Random())) {
                worldrenderer.addVertexData(bakedquad.getVertexData());
            }
        }
        tessellator.draw();
    }

    private void renderAesthetic(ItemStack stack, AbstractClientPlayerEntity player, HandSide handSide, FashionData data, TransformType cam, EnumFashionSlot slot) {

        GlStateManager.pushMatrix();
        if (player.shouldRenderSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
        }

        this.translateToHand(handSide);

        GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        boolean flag = handSide == HandSide.LEFT;
        GlStateManager.translatef((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);

        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        ModelHandle handleModel = null;

        IBakedModel buffer = null;

        if (slot == EnumFashionSlot.WEAPON) {
            handleModel = ResourcePackReader.getAestheticWeapon(data.getRenderingPart(slot));

            if (ResourcePackReader.isItem(data.getRenderingPart(slot)))
                buffer = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(handleModel.getModel(), "inventory"));

        } else if (slot == EnumFashionSlot.SHIELD) {

            if (stack.getItem() instanceof ShieldItem || stack.getItem().getUseAction(stack) == UseAction.BLOCK) {
                boolean isBlocking = player.isHandActive() && player.getActiveItemStack() == stack;
                handleModel = ResourcePackReader.getAestheticShield(data.getRenderingPart(slot), isBlocking);
                buffer = handleModel.get();
            }
        }

        if (handleModel != null) {
            if (buffer == null)
                buffer = handleModel.get();
            IBakedModel rotatedModel = ForgeHooksClient.handleCameraTransforms(buffer, cam, flag);
            GlStateManager.translatef((float) ((flag ? -1 : 1) / 16.0F) + (flag ? -0.0625f * 7 : -0.0625f * 9), -0.0625f * 8, -0.0625f * 8);

            renderModel(rotatedModel, handleModel.getVertexFormat());
        }
        GlStateManager.popMatrix();
    }

    // vanilla rendering
    private void renderHeldItem(AbstractClientPlayerEntity player, ItemStack stack, ItemCameraTransforms.TransformType cam, HandSide handSide) {

        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();

            if (player.shouldRenderSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            // Forge: moved this call down, fixes incorrect offset while sneaking.
            this.translateToHand(handSide);
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == HandSide.LEFT;
            GlStateManager.translatef((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, player, cam, flag);
            GlStateManager.popMatrix();
        }
    }

    protected void translateToHand(HandSide p_191361_1_) {

        ((IHasArm) this.getEntityModel()).postRenderArm(0.0625F, p_191361_1_);
    }

    @Override
    public boolean shouldCombineTextures() {

        return false;
    }
}