package net.povstalec.sgjourney.client.render.level;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;

public class AbydosSkyRenderer extends SGJourneySkyRenderer
{

	public AbydosSkyRenderer()
	{
		super(28843L, 1500);
	}
	
	protected void renderEcliptic(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f projectionMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
	{
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, rain);
		stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        stack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
        
        this.renderStars(level, partialTicks, rain, stack, projectionMatrix, setupFog);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        Matrix4f lastMatrix = stack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        
        this.createCelestialObject(bufferbuilder, lastMatrix, GALAXY_LOCATION, 
				0.40F, 100.0F, (float) Math.toRadians(-80), (float) Math.toRadians(120), new float[] {0.0F, 0.0F, 1.0F, 1.0F});
        
        this.renderSun(bufferbuilder, lastMatrix, 50.0F);
        
        this.renderMoon(bufferbuilder, lastMatrix, 20.0F, level.getMoonPhase(), (float) Math.toRadians(-120), (float) Math.toRadians(170));
        this.renderMoon(bufferbuilder, lastMatrix, 25.0F, level.getMoonPhase(), (float) Math.toRadians(80), (float) Math.toRadians(152.5));
        this.renderMoon(bufferbuilder, lastMatrix, 35.0F, level.getMoonPhase(), (float) Math.toRadians(-65), (float) Math.toRadians(150));
        stack.popPose();
	}
	
	public void renderSky(ClientLevel level, float partialTicks, PoseStack stack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog)
	{
		setupFog.run();
		
		if(this.isFoggy(camera))
			return;
		
		Vec3 skyColor = level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
		float skyX = (float)skyColor.x;
        float skyY = (float)skyColor.y;
        float skyZ = (float)skyColor.z;
        FogRenderer.levelFogColor();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.depthMask(false);
		RenderSystem.setShaderColor(skyX, skyY, skyZ, 1.0F);
		ShaderInstance shaderinstance = RenderSystem.getShader();
		this.skyBuffer.bind();
		this.skyBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shaderinstance);
		VertexBuffer.unbind();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		this.renderSunrise(level, partialTicks, stack, projectionMatrix, setupFog, bufferbuilder);
		
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		float rain = 1.0F - level.getRainLevel(partialTicks);
		
		this.renderEcliptic(level, partialTicks, stack, projectionMatrix, setupFog, bufferbuilder, rain);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double height = this.minecraft.player.getEyePosition(partialTicks).y - level.getLevelData().getHorizonHeight(level);
        if(height < 0.0D)
        {
        	stack.pushPose();
        	stack.translate(0.0F, 12.0F, 0.0F);
        	this.darkBuffer.bind();
        	this.darkBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shaderinstance);
        	VertexBuffer.unbind();
        	stack.popPose();
        }
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
	}
}
