package com.mrcrayfish.guns.init;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.entity.GrenadeEntity;
import com.mrcrayfish.guns.entity.MissileEntity;
import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.entity.ThrowableStunGrenadeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<ProjectileEntity>> PROJECTILE = registerProjectile("projectile", ProjectileEntity::new);
    public static final RegistryObject<EntityType<GrenadeEntity>> GRENADE = registerBasic("grenade", GrenadeEntity::new);
    public static final RegistryObject<EntityType<MissileEntity>> MISSILE = registerBasic("missile", MissileEntity::new);
    public static final RegistryObject<EntityType<ThrowableGrenadeEntity>> THROWABLE_GRENADE = registerBasic("throwable_grenade", ThrowableGrenadeEntity::new);
    public static final RegistryObject<EntityType<ThrowableStunGrenadeEntity>> THROWABLE_STUN_GRENADE = registerBasic("throwable_stun_grenade", ThrowableStunGrenadeEntity::new);

    private static <T extends Entity> RegistryObject<EntityType<T>> registerBasic(String id, BiFunction<EntityType<T>, World, T> function)
    {
        EntityType<T> type = EntityType.Builder.of(function::apply, EntityClassification.MISC).sized(0.25F, 0.25F).setTrackingRange(100).setUpdateInterval(1).noSummon().fireImmune().setShouldReceiveVelocityUpdates(true).build(id);
        return REGISTER.register(id, () -> type);
    }

    /**
     * Entity registration that prevents the entity from being sent and tracked by clients. Projectiles
     * are rendered separately from Minecraft's entity rendering system and their logic is handled
     * exclusively by the server, why send them to the client. Projectiles also have very short time
     * in the world and are spawned many times a tick. There is no reason to send unnecessary packets
     * when it can be avoided to drastically improve the performance of the game.
     *
     * @param id       the id of the projectile
     * @param function the factory to spawn the projectile for the server
     * @param <T>      an entity that is a projectile entity
     * @return A registry object containing the new entity type
     */
    private static <T extends ProjectileEntity> RegistryObject<EntityType<T>> registerProjectile(String id, BiFunction<EntityType<T>, World, T> function)
    {
        EntityType<T> type = EntityType.Builder.of(function::apply, EntityClassification.MISC)
            .sized(0.25F, 0.25F)
            .setTrackingRange(0)
            .noSummon()
            .fireImmune()
            .setShouldReceiveVelocityUpdates(false)
            .setCustomClientFactory((spawnEntity, world) -> null)
            .build(id);
        return REGISTER.register(id, () -> type);
    }
}
