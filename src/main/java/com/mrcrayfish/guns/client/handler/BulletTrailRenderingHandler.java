package com.mrcrayfish.guns.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.guns.client.BulletTrail;
import com.mrcrayfish.guns.client.GunRenderType;
import com.mrcrayfish.guns.client.util.RenderUtil;
import com.mrcrayfish.guns.util.OptifineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BulletTrailRenderingHandler
{
    private static BulletTrailRenderingHandler instance;

    public static BulletTrailRenderingHandler get()
    {
        if(instance == null)
        {
            instance = new BulletTrailRenderingHandler();
        }
        return instance;
    }

    private Map<Integer, BulletTrail> bullets = new HashMap<>();

    private BulletTrailRenderingHandler() {}

    /**
     * Adds a bullet trail to render into the world
     *
     * @param trail the bullet trail get
     */
    public void add(BulletTrail trail)
    {
        // Prevents trails being added when not in a world
        World world = Minecraft.getInstance().level;
        if(world != null)
        {
            this.bullets.put(trail.getEntityId(), trail);
        }
    }

    /**
     * Removes the bullet for the given entity id.
     *
     * @param entityId the entity id of the bullet
     */
    public void remove(int entityId)
    {
        this.bullets.remove(entityId);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        World world = Minecraft.getInstance().level;
        if(world != null)
        {
            if(event.phase == TickEvent.Phase.END)
            {
                this.bullets.values().forEach(BulletTrail::tick);
                this.bullets.values().removeIf(BulletTrail::isDead);
            }
        }
        else if(!this.bullets.isEmpty())
        {
            this.bullets.clear();
        }
    }

    public void render(MatrixStack stack, float partialSticks)
    {
        for(BulletTrail bulletTrail : this.bullets.values())
        {
            this.renderBulletTrail(bulletTrail, stack, partialSticks);
        }
    }

    @SubscribeEvent
    public void onRespawn(ClientPlayerNetworkEvent.RespawnEvent event)
    {
        this.bullets.clear();
    }

    @SubscribeEvent
    public void onLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        this.bullets.clear();
    }

    /**
     * @param bulletTrail
     * @param matrixStack
     * @param partialTicks
     */
    private void renderBulletTrail(BulletTrail bulletTrail, MatrixStack matrixStack, float partialTicks)
    {
        if(OptifineHelper.isShadersEnabled())
            return;

        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        if(entity == null || bulletTrail.isDead())
            return;

        matrixStack.pushPose();

        Vector3d view = mc.gameRenderer.getMainCamera().getPosition();
        Vector3d position = bulletTrail.getPosition();
        Vector3d motion = bulletTrail.getMotion();
        double bulletX = position.x + motion.x * partialTicks;
        double bulletY = position.y + motion.y * partialTicks;
        double bulletZ = position.z + motion.z * partialTicks;
        matrixStack.translate(bulletX - view.x(), bulletY - view.y(), bulletZ - view.z());

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(bulletTrail.getYaw()));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-bulletTrail.getPitch() + 90));

        Vector3d motionVec = new Vector3d(motion.x, motion.y, motion.z);
        float trailLength = (float) ((motionVec.length() / 3.0F) * bulletTrail.getTrailLengthMultiplier());
        float red = (float) (bulletTrail.getTrailColor() >> 16 & 255) / 255.0F;
        float green = (float) (bulletTrail.getTrailColor() >> 8 & 255) / 255.0F;
        float blue = (float) (bulletTrail.getTrailColor() & 255) / 255.0F;
        float alpha = 0.3F;

        // Prevents the trail length from being longer than the distance to shooter
        Entity shooter = bulletTrail.getShooter();
        if(shooter != null)
        {
            trailLength = (float) Math.min(trailLength, shooter.getEyePosition(partialTicks).distanceTo(new Vector3d(bulletX,bulletY, bulletZ)));
        }

        Matrix4f matrix4f = matrixStack.last().pose();
        IRenderTypeBuffer.Impl renderTypeBuffer = mc.renderBuffers().bufferSource();

        if(bulletTrail.isTrailVisible())
        {
            RenderType bulletType = GunRenderType.getBulletTrail();
            IVertexBuilder builder = renderTypeBuffer.getBuffer(bulletType);
            builder.vertex(matrix4f, 0, 0, -0.035F).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, 0, 0, 0.035F).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, 0, -trailLength, 0.035F).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, 0, -trailLength, -0.035F).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -0.035F, 0, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, 0.035F, 0, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, 0.035F, -trailLength, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -0.035F, -trailLength, 0).color(red, green, blue, alpha).endVertex();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(bulletType);
        }

        if(!bulletTrail.getItem().isEmpty())
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees((bulletTrail.getAge() + partialTicks) * (float) 50));
            matrixStack.scale(0.275F, 0.275F, 0.275F);

            int combinedLight = WorldRenderer.getLightColor(entity.level, new BlockPos(entity.position()));
            ItemStack stack = bulletTrail.getItem();
            RenderUtil.renderModel(stack, ItemCameraTransforms.TransformType.NONE, matrixStack, renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, null, null);
        }

        matrixStack.popPose();
    }
}
