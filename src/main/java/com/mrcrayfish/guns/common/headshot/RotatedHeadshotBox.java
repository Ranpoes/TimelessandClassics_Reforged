package com.mrcrayfish.guns.common.headshot;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RotatedHeadshotBox<T extends LivingEntity> extends BasicHeadshotBox<T>
{
    protected double headZOffset;
    protected boolean rotatePitch;
    protected boolean rotateYaw;

    public RotatedHeadshotBox(double headSize, double headYOffset, double headZOffset, boolean rotatePitch, boolean rotateYaw)
    {
        super(headSize, headSize, headYOffset);
        this.headZOffset = headZOffset;
        this.rotatePitch = rotatePitch;
        this.rotateYaw = rotateYaw;
    }

    public RotatedHeadshotBox(double headWidth, double headHeight, double headYOffset, double headZOffset, boolean rotatePitch, boolean rotateYaw)
    {
        super(headWidth, headHeight, headYOffset);
        this.headZOffset = headZOffset;
        this.rotatePitch = rotatePitch;
        this.rotateYaw = rotateYaw;
    }

    @Nullable
    @Override
    public AxisAlignedBB getHeadshotBox(T entity)
    {
        AxisAlignedBB headBox = super.getHeadshotBox(entity);
        if(headBox != null)
        {
            headBox = headBox.move(Vector3d.directionFromRotation(this.rotatePitch ? entity.xRot : 0.0F, this.rotateYaw ? entity.yBodyRot : 0.0F).normalize().scale(this.headZOffset * 0.0625));
            return headBox;
        }
        return null;
    }
}
