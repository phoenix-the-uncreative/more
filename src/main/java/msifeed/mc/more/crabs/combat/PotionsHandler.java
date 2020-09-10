package msifeed.mc.more.crabs.combat;

import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.more.crabs.action.ActionTag;
import msifeed.mc.more.crabs.action.effects.Buff;
import msifeed.mc.more.crabs.action.effects.Effect;
import msifeed.mc.more.crabs.action.parser.BuffJsonAdapter;
import msifeed.mc.more.crabs.utils.ActionAttribute;
import msifeed.mc.more.crabs.utils.CombatAttribute;
import msifeed.mc.more.crabs.utils.GetUtils;
import msifeed.mc.sys.config.ConfigBuilder;
import msifeed.mc.sys.config.JsonConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.*;

public enum PotionsHandler {
    INSTANCE;

    private final TypeToken<HashMap<Integer, ArrayList<PotionRule>>> potionRulesType = new TypeToken<HashMap<Integer, ArrayList<PotionRule>>>() {
    };
    private final JsonConfig<HashMap<Integer, ArrayList<PotionRule>>> rulesConfig = ConfigBuilder.of(potionRulesType, "potion_rules.json")
            .addAdapter(Buff.class, new BuffJsonAdapter())
            .sync()
            .create();

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving.worldObj.isRemote)
            return;

        final CombatContext com = CombatAttribute.get(event.entityLiving).orElse(null);
        if (com == null)
            return;

        if (com.phase == CombatContext.Phase.DEFEND)
            handleDefender(event.entityLiving, com);
    }

    private void handleDefender(EntityLivingBase entity, CombatContext com) {
        final boolean offenceActionHasApply = GetUtils.entityLiving(entity, com.offender)
                .flatMap(CombatAttribute::get)
                .filter(c -> c.phase == CombatContext.Phase.ATTACK)
                .map(c -> c.action != null && c.action.hasAnyTag(ActionTag.apply))
                .orElse(false);
        if (!offenceActionHasApply)
            return;

        final ActionContext act = ActionAttribute.get(entity).orElse(new ActionContext());
        act.buffsToReceive.addAll(convertEntityEffects(entity));
        ActionAttribute.INSTANCE.set(entity, act);
    }

    public static List<Buff> convertEntityEffects(EntityLivingBase entity) {
        final Collection<PotionEffect> effects = entity.getActivePotionEffects();
        if (effects.isEmpty())
            return Collections.emptyList();

        final HashMap<Integer, ArrayList<PotionRule>> ruleLists = INSTANCE.rulesConfig.get();
        final List<Integer> toRemove = new ArrayList<>(effects.size());
        final List<Buff> convertedBuffs = new ArrayList<>(effects.size());

        for (PotionEffect e : effects) {
            final ArrayList<PotionRule> rules = ruleLists.get(e.getPotionID());
            if (rules == null)
                continue;
            for (PotionRule pr : rules) {
                // Subtract 1 from maxAmplifier so in config starting value was 1
                if (e.getDuration() <= pr.maxDuration && e.getAmplifier() <= pr.maxAmplifier - 1) {
                    for (Buff b : pr.buffs) {
                        convertedBuffs.add(b);
                        toRemove.add(e.getPotionID());
                    }
                    break;
                }
            }
        }

        for (int id : toRemove)
            entity.removePotionEffect(id);

        return convertedBuffs;
    }

    public static List<Effect> convertPassiveEffects(EntityLivingBase entity) {
        final Collection<PotionEffect> potions = entity.getActivePotionEffects();
        if (potions.isEmpty())
            return Collections.emptyList();

        final HashMap<Integer, ArrayList<PotionRule>> ruleLists = INSTANCE.rulesConfig.get();
        final List<Effect> effects = new ArrayList<>(potions.size());

        for (PotionEffect e : potions) {
            final ArrayList<PotionRule> rules = ruleLists.get(e.getPotionID());
            if (rules == null)
                continue;
            for (PotionRule pr : rules) {
                // Subtract 1 from maxAmplifier so in config starting value was 1
                if (e.getDuration() <= pr.maxDuration && e.getAmplifier() <= pr.maxAmplifier - 1) {
                    for (Buff b : pr.buffs)
                        effects.add(b.effect);
                    break;
                }
            }
        }

        return effects;
    }

    public static List<Buff> convertItemStack(EntityLivingBase entity, ItemStack stack) {
        final List<PotionEffect> effects = Items.potionitem.getEffects(stack);
        if (effects == null || effects.isEmpty())
            return Collections.emptyList();

        final HashMap<Integer, ArrayList<PotionRule>> ruleLists = INSTANCE.rulesConfig.get();
        final List<Buff> convertedBuffs = new ArrayList<>();

        for (PotionEffect e : effects) {
            final ArrayList<PotionRule> rules = ruleLists.get(e.getPotionID());
            if (rules == null)
                continue;
            for (PotionRule pr : rules) {
                // Subtract 1 from maxAmplifier so in config starting value was 1
                if (e.getDuration() <= pr.maxDuration && e.getAmplifier() <= pr.maxAmplifier - 1) {
                    convertedBuffs.addAll(pr.buffs);
                    break;
                }
            }
        }

        return convertedBuffs;
    }

    public static class PotionRule {
        public int maxDuration;
        public int maxAmplifier;
        public List<Buff> buffs;
    }
}
