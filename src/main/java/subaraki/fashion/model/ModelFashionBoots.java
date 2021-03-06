package subaraki.fashion.model;

import net.minecraft.client.renderer.entity.model.RendererModel;

public class ModelFashionBoots extends ModelFashion {

    public ModelFashionBoots(float modelSize) {

        super(modelSize, 32, 32, false);

        this.bipedRightLeg = new RendererModel(this, 0, 0);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

        this.bipedLeftLeg = new RendererModel(this, 16, 0);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

        this.bipedLeftLegwear = new RendererModel(this, 16, 16);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);

        this.bipedRightLegwear = new RendererModel(this, 0, 16);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);

        this.bipedHead.showModel = false;
        this.bipedHeadwear.showModel = false;
        this.bipedBody.showModel = false;
        this.bipedRightArm.showModel = false;
        this.bipedLeftArm.showModel = false;

        this.bipedBodyWear.showModel = false;
        this.bipedRightArmwear.showModel = false;
        this.bipedLeftArmwear.showModel = false;
    }
}