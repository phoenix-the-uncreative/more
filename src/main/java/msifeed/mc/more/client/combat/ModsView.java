package msifeed.mc.more.client.combat;

import msifeed.mc.mellow.layout.GridLayout;
import msifeed.mc.mellow.layout.ListLayout;
import msifeed.mc.mellow.utils.SizePolicy;
import msifeed.mc.mellow.widgets.Widget;
import msifeed.mc.mellow.widgets.text.Label;
import msifeed.mc.mellow.widgets.text.TextInput;
import msifeed.mc.more.crabs.character.Ability;
import msifeed.mc.more.crabs.meta.MetaInfo;
import msifeed.mc.more.crabs.meta.MetaRpc;
import msifeed.mc.more.crabs.rolls.Modifiers;
import msifeed.mc.more.crabs.utils.MetaAttribute;
import net.minecraft.entity.EntityLivingBase;

class ModsView extends Widget {
    private final EntityLivingBase entity;

    ModsView(EntityLivingBase entity) {
        this.entity = entity;

        getSizeHint().x = 110;
        setSizePolicy(SizePolicy.Policy.MINIMUM, SizePolicy.Policy.PREFERRED);
        setLayout(new GridLayout());

        refill();
    }

    private void refill() {
        MetaAttribute.get(entity).ifPresent(meta -> {
            final Modifiers mods = meta.modifiers;

            addChild(new Label("Roll:"));
            final TextInput modInput = new TextInput();
            modInput.getSizeHint().x = 16;
            if (mods.roll != 0)
                modInput.setText(Integer.toString(mods.roll));
            modInput.setFilter(s -> s.length() < 5 && TextInput.isSignedInt(s));
            modInput.setCallback(s -> updateRollMod(meta, s));
            addChild(modInput);

            for (final Ability a : Ability.values())
                addAbilityMod(meta, a);
        });
    }

    private void addAbilityMod(MetaInfo meta, Ability a) {
        final Widget pair = new Widget();
        pair.setLayout(ListLayout.HORIZONTAL);
        addChild(pair);

        final Label label = new Label(a.toString() + ":");
        label.getSizeHint().x = 16;
        pair.addChild(label);

        final TextInput input = new TextInput();
        input.getSizeHint().x = 16;

        final int modValue = meta.modifiers.features.getOrDefault(a, 0);
        if (modValue != 0)
            input.setText(Integer.toString(modValue));
        input.setFilter(s -> s.length() < 5 && TextInput.isSignedInt(s));
        input.setCallback(s -> updateFeatMods(meta, a, s));
        pair.addChild(input);
    }

    private void updateRollMod(MetaInfo meta, String s) {
        meta.modifiers.roll = (s.isEmpty() || s.equals("-")) ? 0 : Integer.parseInt(s);
        MetaRpc.updateMeta(entity.getEntityId(), meta);
    }

    private void updateFeatMods(MetaInfo meta, Ability a, String s) {
        if (s.isEmpty() || s.equals("-"))
            meta.modifiers.features.remove(a);
        else
            meta.modifiers.features.put(a, Integer.parseInt(s));
        MetaRpc.updateMeta(entity.getEntityId(), meta);
    }
}