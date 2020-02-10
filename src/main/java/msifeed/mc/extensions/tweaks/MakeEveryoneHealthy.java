package msifeed.mc.extensions.tweaks;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.aorta.core.character.Character;
import msifeed.mc.aorta.core.utils.CharacterAttribute;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public enum MakeEveryoneHealthy {
    INSTANCE;

    public static void apply() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.setHealth(event.player.getMaxHealth());
            final FoodStats stats = event.player.getFoodStats();
            if (stats.getFoodLevel() != 10)
                stats.addStats(10 - stats.getFoodLevel(), 0);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTakeDamage(LivingAttackEvent event) {
        if (event.entity instanceof EntityPlayer)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onSetTarget(LivingSetAttackTargetEvent event) {
        if (event.target instanceof EntityPlayer && event.entity instanceof EntityLiving) {
            ((EntityLiving) event.entity).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event) {
        calculateHealth(event.player);
    }

    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        calculateHealth(event.player);
    }

    private void calculateHealth(EntityPlayer player) {
        final Character c = CharacterAttribute.require(player);
        player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(c.countMaxHP());
    }
}
