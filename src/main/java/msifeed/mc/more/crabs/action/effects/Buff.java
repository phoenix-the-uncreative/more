package msifeed.mc.more.crabs.action.effects;

import msifeed.mc.more.crabs.combat.ActionContext;
import msifeed.mc.more.crabs.combat.FighterInfo;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;

import static msifeed.mc.more.crabs.action.effects.DynamicEffect.EffectArgs.*;

public final class Buff extends DynamicEffect {
    private int pause;
    private int steps;
    private RepeatMode repeatMode;
    private Effect effect;

    @Override
    public String name() {
        return "buff";
    }

    @Override
    public String toString() {
        return name() + ':' + pause + ':' + steps + ':' + repeatMode + ':' + effect.toString();
    }

    public boolean shouldApply(Stage stage, ActionContext target, ActionContext other) {
        if (other == null)
            return effect.shouldApply(stage, target, null);
        else
            return true;
    }

    @Override
    public void apply(FighterInfo target, FighterInfo other) {
        if (other == null) {
            // Apply from buffs
            if (active())
                effect.apply(target, null);
            step();
        } else {
            // Apply from action effects
            target.act.buffsToReceive.add(this);
        }
    }

    private boolean active() {
        return started() && !ended();
    }

    private boolean started() {
        return pause <= 0;
    }

    public boolean ended() {
        return steps <= 0;
    }

    private void step() {
        if (started())
            steps--;
        else
            pause--;
    }

    @Override
    public boolean equals(Effect other) {
        return other instanceof Buff && effect.equals(((Buff) other).effect);
    }

    /**
     * [pause before activation]:[number of time the effect is applied]:[repeat mode]:[effect]
     */
    @Override
    public EffectArgs[] args() {
        return new EffectArgs[]{INT, INT, STRING, EFFECT};
    }

    @Override
    public DynamicEffect produce(Object[] args) {
        final Buff b = new Buff();
        b.pause = (int) args[0];
        b.steps = (int) args[1];
        b.repeatMode = RepeatMode.valueOf(((String) args[2]).toLowerCase());
        b.effect = (Effect) args[3];
        return b;
    }

    public static void mergeBuff(ArrayList<Buff> current, Buff buff) {
        final Buff cur = current.stream()
                .filter(b -> b.effect.equals(buff.effect))
                .findAny().orElse(null);
        if (cur != null) {
            switch (buff.repeatMode) {
                case extend:
                    cur.steps += buff.steps;
                    return;
                case replace:
                    cur.pause = buff.pause;
                    cur.steps = buff.steps;
                    return;
            }
        }
        // If there are no current buffs with this effect or it have stack mode
        current.add(buff);
    }

    private enum RepeatMode {
        extend, replace, stack
    }

    public static class OnRole extends DynamicEffect {
        private ActionContext.Role role;
        private Effect effect;

        @Override
        public String name() {
            return "role";
        }

        @Override
        public boolean shouldApply(Stage stage, ActionContext target, ActionContext other) {
            return stage == Stage.ACTION
                    && role == target.role
                    && effect.shouldApply(stage, target, other);
        }

        @Override
        public void apply(FighterInfo target, FighterInfo other) {
            this.effect.apply(target, other);
        }

        @Override
        public boolean equals(Effect other) {
            return other instanceof OnRole && ((OnRole) other).effect.equals(this.effect);
        }

        @Override
        public EffectArgs[] args() {
            return new EffectArgs[]{STRING, EFFECT};
        }

        @Override
        public DynamicEffect produce(Object[] args) {
            final OnRole e = new OnRole();
            e.role = EnumUtils.getEnum(ActionContext.Role.class, (String) args[0]);
            e.effect = (Effect) args[1];
            return e;
        }
    }
}
