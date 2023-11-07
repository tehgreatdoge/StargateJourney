package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.FluidTankRenderer;
import net.povstalec.sgjourney.common.menu.CrystallizerMenu;

public class CrystallizerScreen extends AbstractContainerScreen<CrystallizerMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/crystallizer_gui.png");
	private FluidTankRenderer renderer;
	
    public CrystallizerScreen(CrystallizerMenu menu, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
    }
	
	@Override
	public void init()
	{
		super.init();
		assignFluidRenderer();
	}
	
	private void assignFluidRenderer()
	{
		this.renderer = new FluidTankRenderer(64000, true, 16, 54);
	}

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);
        
        //this.renderEnergy(pPoseStack, x + 8, y + 62);
    }

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float delta)
    {
    	int x = (width - imageWidth) / 2;
    	int y = (height - imageHeight) / 2;
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        renderTooltip(stack, mouseX, mouseY);
        
        //this.energyTooltip(pPoseStack, 8, 62, mouseX, mouseY);
        renderer.render(stack, x + 12, y + 20, menu.getFluid());
	}
    
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) 
	{
		this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    //this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    /*protected void renderEnergy(PoseStack stack, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(160 * percentage);
    	this.blit(stack, x, y, 0, 168, actual, 6);
    }*/
    
    /*protected void energyTooltip(PoseStack stack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 160, 6, (double) mouseX, (double) mouseY))
	    {
	    	renderTooltip(stack, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE")).withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
	    }
    }*/
}