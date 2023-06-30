package msifeed.mc.more.client.combat;

import msifeed.mc.mellow.layout.GridLayout;
import msifeed.mc.mellow.widgets.Widget;
import msifeed.mc.mellow.widgets.basic.Separator;
import msifeed.mc.mellow.widgets.button.ButtonMultiLabel;
import msifeed.mc.mellow.widgets.text.Label;
import msifeed.mc.mellow.widgets.text.TextInput;
import msifeed.mc.more.crabs.combat.CombatRpc;
import msifeed.mc.more.crabs.combat.ShrimpsAction;
import msifeed.mc.sys.utils.L10n;
import net.minecraft.entity.EntityLivingBase;

public class ShrimpsActionView extends Widget {
    private final EntityLivingBase entity;
    private final ShrimpsAction action = new ShrimpsAction();

    public ShrimpsActionView(EntityLivingBase entity) {
        this.entity = entity;
        setLayout(new GridLayout());
        refill();
    }

    public void refill() {
        addChild(new Label(L10n.tr("more.gui.combat.shrimps.main_action")));
        final TextInput mainActionInput = new TextInput();
        mainActionInput.getSizeHint().x = 50;
        mainActionInput.setCallback(s -> action.mainAction = s);
        addChild(mainActionInput);

        addChild(new Label(L10n.tr("more.gui.combat.shrimps.secondary_action")));
        final TextInput secondaryActionInput = new TextInput();
        secondaryActionInput.getSizeHint().x = 50;
        secondaryActionInput.setCallback(s -> action.secondaryAction = s);
        addChild(secondaryActionInput);

        addChild(new Label(L10n.tr("more.gui.combat.shrimps.swift_action")));
        final TextInput swiftActionInput = new TextInput();
        swiftActionInput.getSizeHint().x = 50;
        swiftActionInput.setCallback(s -> action.swiftAction = s);
        addChild(swiftActionInput);

        addChild(new Label(L10n.tr("more.gui.combat.shrimps.free_action")));
        final TextInput freeActionInput = new TextInput();
        freeActionInput.getSizeHint().x = 50;
        freeActionInput.setCallback(s -> action.freeAction = s);
        addChild(freeActionInput);

        addChild(new Separator());
        addChild(new Separator());

        final ButtonMultiLabel skipTurnButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.skip_turn"));
        skipTurnButton.setClickCallback(() -> skipTurn());
        addChild(skipTurnButton);

        final ButtonMultiLabel doTurnButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.do_turn"));
        doTurnButton.setClickCallback(() -> doTurn());
        addChild(doTurnButton);

        addChild(new Separator());
        addChild(new Separator());

        addChild(new Label(L10n.tr("more.gui.combat.shrimps.reaction")));
        final TextInput reactionInput = new TextInput();
        reactionInput.getSizeHint().x = 50;
        reactionInput.setCallback(s -> action.reaction = s);
        addChild(reactionInput);

        addChild(new Label(L10n.tr("more.gui.combat.shrimps.full_action")));
        final TextInput fullActionInput = new TextInput();
        fullActionInput.getSizeHint().x = 50;
        fullActionInput.setCallback(s -> action.fullAction = s);
        addChild(fullActionInput);

        final ButtonMultiLabel reactionButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.do_reaction"));
        reactionButton.setClickCallback(() -> doReaction());
        addChild(reactionButton);

        final ButtonMultiLabel fullActionButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.do_full_action"));
        fullActionButton.setClickCallback(() -> doFullAction());
        addChild(fullActionButton);

        addChild(new Separator());
        addChild(new Separator());

        final ButtonMultiLabel resetButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.reset"));
        resetButton.setClickCallback(() -> reset());
        addChild(resetButton);

        final ButtonMultiLabel narrativeButton = new ButtonMultiLabel(L10n.tr("more.gui.combat.shrimps.do_narrative_action"));
        narrativeButton.setClickCallback(() -> doNarrativeAction());
        addChild(narrativeButton);
    }

    private void doNarrativeAction() {
        action.type = ShrimpsAction.Type.NARRATIVE;
        CombatRpc.doShrimpsAction(action);
    }

    private void reset() {

    }

    private void doFullAction() {
        action.type = ShrimpsAction.Type.FULL;
        CombatRpc.doShrimpsAction(action);
    }

    private void doReaction() {
                action.type = ShrimpsAction.Type.REACTION;
        CombatRpc.doShrimpsAction(action);
    }

    private void doTurn() {
        action.type = ShrimpsAction.Type.TURN;
        CombatRpc.doShrimpsAction(action);
    }

    private void skipTurn() {
        action.type = ShrimpsAction.Type.SKIP;
        CombatRpc.doShrimpsAction(action);
    }

}
