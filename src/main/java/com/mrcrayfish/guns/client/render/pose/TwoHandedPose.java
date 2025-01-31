package com.mrcrayfish.guns.client.render.pose;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.client.handler.ReloadHandler;
import com.mrcrayfish.guns.client.util.RenderUtil;
import com.mrcrayfish.guns.common.GripType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class TwoHandedPose extends WeaponPose
{
    @Override
    protected AimPose getUpPose()
    {
        AimPose upPose = new AimPose();
        upPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(60F, 0F, 10F)).setRightArm(new LimbPose().setRotationAngleX(-120F).setRotationAngleY(-55F).setRotationPointX(-5).setRotationPointY(3).setRotationPointZ(0)).setLeftArm(new LimbPose().setRotationAngleX(-160F).setRotationAngleY(-20F).setRotationAngleZ(-30F).setRotationPointY(2).setRotationPointZ(-1));
        upPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(40F, 0F, 30F)).setItemTranslate(new Vector3f(-1, 0, 0)).setRightArm(new LimbPose().setRotationAngleX(-140F).setRotationAngleY(-55F).setRotationPointX(-5).setRotationPointY(3).setRotationPointZ(0)).setLeftArm(new LimbPose().setRotationAngleX(-170F).setRotationAngleY(-20F).setRotationAngleZ(-35F).setRotationPointY(1).setRotationPointZ(0));
        return upPose;
    }

    @Override
    protected AimPose getForwardPose()
    {
        AimPose forwardPose = new AimPose();
        forwardPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(30F, -11F, 0F)).setRightArm(new LimbPose().setRotationAngleX(-60F).setRotationAngleY(-55F).setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2).setRotationPointZ(1)).setLeftArm(new LimbPose().setRotationAngleX(-65F).setRotationAngleY(-10F).setRotationAngleZ(5F).setRotationPointY(2).setRotationPointZ(-1));
        forwardPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(5F, -21F, 0F)).setRightArm(new LimbPose().setRotationAngleX(-85F).setRotationAngleY(-65F).setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2)).setLeftArm(new LimbPose().setRotationAngleX(-90F).setRotationAngleY(-15F).setRotationAngleZ(0F).setRotationPointY(2).setRotationPointZ(0));
        return forwardPose;
    }

    @Override
    protected AimPose getDownPose()
    {
        AimPose downPose = new AimPose();
        downPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(-15F, -5F, 0F)).setItemTranslate(new Vector3f(0, -0.5F, 0.5F)).setRightArm(new LimbPose().setRotationAngleX(-30F).setRotationAngleY(-65F).setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2)).setLeftArm(new LimbPose().setRotationAngleX(-5F).setRotationAngleY(-20F).setRotationAngleZ(20F).setRotationPointY(5).setRotationPointZ(0));
        downPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(-20F, -5F, -10F)).setItemTranslate(new Vector3f(0, -0.5F, 1F)).setRightArm(new LimbPose().setRotationAngleX(-30F).setRotationAngleY(-65F).setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(1)).setLeftArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(-20F).setRotationAngleZ(30F).setRotationPointY(5).setRotationPointZ(0));
        return downPose;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyPlayerModelRotation(PlayerEntity player, PlayerModel model, Hand hand, float aimProgress)
    {
        if(Config.CLIENT.display.oldAnimations.get())
        {
            Minecraft mc = Minecraft.getInstance();
            boolean right = mc.options.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            ModelRenderer mainArm = right ? model.rightArm : model.leftArm;
            ModelRenderer secondaryArm = right ? model.leftArm : model.rightArm;
            mainArm.xRot = model.head.xRot;
            mainArm.yRot = model.head.yRot;
            mainArm.zRot = model.head.zRot;
            secondaryArm.xRot = model.head.xRot;
            secondaryArm.yRot = model.head.yRot;
            secondaryArm.zRot = model.head.zRot;
            mainArm.xRot = (float) Math.toRadians(-55F + aimProgress * -30F);
            mainArm.yRot = (float) Math.toRadians((-45F + aimProgress * -20F) * (right ? 1F : -1F));
            secondaryArm.xRot = (float) Math.toRadians(-42F + aimProgress * -48F);
            secondaryArm.yRot = (float) Math.toRadians((-15F + aimProgress * 5F) * (right ? 1F : -1F));
        }
        else
        {
            super.applyPlayerModelRotation(player, model, hand, aimProgress);
            float angle = this.getPlayerPitch(player);
            model.head.xRot = (float) Math.toRadians(angle > 0.0 ? angle * 70F : angle * 90F);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyPlayerPreRender(PlayerEntity player, Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
    {
        if(Config.CLIENT.display.oldAnimations.get())
        {
            boolean right = Minecraft.getInstance().options.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            player.yBodyRotO = player.yRotO + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
            player.yBodyRot = player.yRot + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
        }
        else
        {
            super.applyPlayerPreRender(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyHeldItemTransforms(PlayerEntity player, Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
    {
        if(Config.CLIENT.display.oldAnimations.get())
        {
            if(hand == Hand.MAIN_HAND)
            {
                boolean right = Minecraft.getInstance().options.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
                matrixStack.translate(0, 0, 0.05);
                float invertRealProgress = 1.0F - aimProgress;
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees((25F * invertRealProgress) * (right ? 1F : -1F)));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees((30F * invertRealProgress + aimProgress * -20F) * (right ? 1F : -1F)));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(25F * invertRealProgress + aimProgress * 5F));
            }
        }
        else
        {
            super.applyHeldItemTransforms(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    public void renderFirstPersonArms(ClientPlayerEntity player, HandSide hand, ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, float partialTicks)
    {
        matrixStack.translate(0, 0, -1);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));

        matrixStack.pushPose();

        float reloadProgress = ReloadHandler.get().getReloadProgress(partialTicks);
        matrixStack.translate(reloadProgress * 0.5, -reloadProgress, -reloadProgress * 0.5);

        int side = hand.getOpposite() == HandSide.RIGHT ? 1 : -1;
        matrixStack.translate(6 * side * 0.0625, -0.585, -0.5);

        if(Minecraft.getInstance().player.getModelName().equals("slim") && hand.getOpposite() == HandSide.LEFT)
        {
            matrixStack.translate(0.03125F * -side, 0, 0);
        }

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(80F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(15F * -side));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(15F * -side));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-35F));
        matrixStack.scale(0.5F, 0.5F, 0.5F);

        RenderUtil.renderFirstPersonArm(player, hand.getOpposite(), matrixStack, buffer, light);

        matrixStack.popPose();

        double centerOffset = 2.5;
        if(Minecraft.getInstance().player.getModelName().equals("slim"))
        {
            centerOffset += hand == HandSide.RIGHT ? 0.2 : 0.8;
        }
        centerOffset = hand == HandSide.RIGHT ? -centerOffset : centerOffset;
        matrixStack.translate(centerOffset * 0.0625, -0.4, -0.975);

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(80F));
        matrixStack.scale(0.5F, 0.5F, 0.5F);

        RenderUtil.renderFirstPersonArm(player, hand, matrixStack, buffer, light);
    }

    @Override
    public boolean applyOffhandTransforms(PlayerEntity player, PlayerModel model, ItemStack stack, MatrixStack matrixStack, float partialTicks)
    {
        return GripType.applyBackTransforms(player, matrixStack);
    }
}
