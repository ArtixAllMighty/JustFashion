package subaraki.fashion.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class FancyButton extends Button {

    private boolean isActive;

    public String name = "unknown layer";

    /**
     * Constructor with default empty name. used mainly for the toggler of viewing
     * fashion
     */
    public FancyButton(int x, int y, IPressable press) {

        super(x, y, 8, 8, "_", press);
    }

    /**
     * Create a Button of 15x15, round toggle texture. Name mainly used only for
     * hover over purposes.
     */
    public FancyButton(int x, int y, String name, IPressable press) {

        this(x, y, press);
        this.name = name;
    }

    /**
     * The switch button to enable or disable fashion is the only one to not have a
     * name
     */
    public boolean isSwitch() {

        return name.equals("_");
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float particleTicks) {

        Minecraft mc = Minecraft.getInstance();

        if (this.visible) {
            // this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x +
            // this.width / 2 && mouseY < this.y + this.height / 2;

            mc.getTextureManager().bindTexture(WIDGETS_LOCATION);

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            this.blit((this.x) * 2, (this.y) * 2, isActive ? 208 : 224, 0, 15, 15);
            GlStateManager.popMatrix();
        }
    }

    /** Wether or not this button has been toggled */
    public boolean isActive() {

        return isActive;
    }

    /**
     * Wether to set this button toggled on or off. It is adviced to use
     * {@link #toggle() toggle} instead
     */
    public FancyButton setActive(boolean flag) {

        isActive = flag;
        return this;
    }

    /** Switch the state of this button to it's inverse. */
    public void toggle() {

        this.isActive = !isActive;
    }
}
