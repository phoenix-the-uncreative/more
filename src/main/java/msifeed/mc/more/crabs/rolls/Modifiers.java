package msifeed.mc.more.crabs.rolls;

import msifeed.mc.more.crabs.character.*;
import msifeed.mc.more.crabs.character.Character;

import java.util.*;

public class Modifiers {
    private static final Set<Trauma> SKILL_DISABLER_TRAUMAS = Collections.unmodifiableSet(EnumSet.of(Trauma.DEBUFF_AMNESIA, Trauma.DEBUFF_BURN));
    public int damage = 0;
    public int roll = 0;
    public int metaRoll = 0;
    public EnumMap<Ability, Integer> abilities = new EnumMap<>(Ability.class);
    public EnumMap<Ability, Integer> metaAbilities = new EnumMap<>(Ability.class);
    public Set<Skill> disabledSkills = new HashSet<>();

    public Modifiers() {
    }

    public Modifiers(Modifiers m) {
        damage = m.damage;
        roll = m.roll;
        metaRoll = m.metaRoll;
        abilities.putAll(m.abilities);
        metaAbilities.putAll(m.metaAbilities);
        disabledSkills.addAll(m.disabledSkills);
    }

    public void updateModifiers(Character character) {
        metaAbilities.clear();
        disabledSkills.clear();
        metaRoll = 0;

        updateForTraumas(character);
        updateForLimbs(character);
    }

    public void updateForTraumas(Character character) {
        for (Map.Entry<Trauma, Integer> entry : character.traumas.entrySet()) {
            Trauma trauma = entry.getKey();
            Integer traumaLevel = entry.getValue();

            updateMetaAbilities(trauma, traumaLevel);

            boolean shouldDisableSkills = SKILL_DISABLER_TRAUMAS.contains(trauma) && traumaLevel > 0;
            if (shouldDisableSkills) {
                disableSkills(trauma == Trauma.DEBUFF_BURN, character);

                if (traumaLevel > 1) {
                    metaRoll -= traumaLevel - 1;
                }
            }
        }
    }

    public void updateForLimbs(Character character) {
        for (Map.Entry<Limb, Integer> entry : character.limbs.entrySet()) {
            Limb limb = entry.getKey();
            Integer amount = entry.getValue();

            int multiplier = 2 - amount;
            if (multiplier > 0) {
                int toAdd = -(3 * multiplier);
                metaAbilities.compute(limb.affectedAbility, (k, v) -> v == null ? toAdd : v + toAdd);
            }
        }
    }

    private void updateMetaAbilities(Trauma trauma, Integer traumaLevel) {
        String debuff = trauma.name().replace("DEBUFF_", "");
        try {
            Ability debuffedAbility = Ability.valueOf(debuff);
            int toAdd = -(traumaLevel * 2);
            metaAbilities.compute(debuffedAbility, (k, v) -> v == null ? toAdd : v + toAdd);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    private static boolean shouldDisableSkill(Skill skill, boolean disableVesselAbilities) {
        EnumSet<Ability> vesselAbilities = EnumSet.of(Ability.STR, Ability.END, Ability.PER, Ability.REF);

        return vesselAbilities.contains(skill.stat) == disableVesselAbilities;
    }

    private void disableSkills(boolean disableVesselAbilities, Character character) {
        for (Skill characterSkill : character.skills) {
            if (shouldDisableSkill(characterSkill, disableVesselAbilities)) {
                disabledSkills.add(characterSkill);
            }
        }
    }


    public boolean isZeroed() {
        return damage == 0 && roll == 0 && !hasAbilityMods();
    }

    public boolean hasAbilityMods() {
        return abilities.values().stream().anyMatch(i -> i != 0) || metaAbilities.values().stream().anyMatch(i -> i != 0);
    }

    public int toAbility(Ability ability) {
        int abilitiesMod = customToAbility(ability);
        int metaMod = metaToAbility(ability);

        return abilitiesMod + metaMod;
    }

    public int metaToAbility(Ability ability) {
        return metaAbilities.getOrDefault(ability, 0);
    }

    public int customToAbility(Ability ability) {
        return abilities.getOrDefault(ability, 0);
    }

    public boolean isSkillDisabled(Skill skill) {
        return disabledSkills
                .stream()
                .anyMatch(disabledSkill -> skill.name.equals(disabledSkill.name) && skill.stat == disabledSkill.stat);
    }
}
