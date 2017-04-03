package de.fzi.cooperate.sequence;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.StateInvariant;

import de.fzi.cooperate.sequence.combinedfragment.CombinedFragmentDialog;
import de.fzi.cooperate.sequence.combinedfragment.CombinedFragmentEditDialog;
import de.fzi.cooperate.sequence.combinedfragment.CombinedFragmentOperations;
import de.fzi.cooperate.sequence.interactionoperand.InteractionOperandDialog;
import de.fzi.cooperate.sequence.interactionoperand.InteractionOperandEditDialog;
import de.fzi.cooperate.sequence.interactionoperand.InteractionOperandOperations;
import de.fzi.cooperate.sequence.lifeline.LifelineDialog;
import de.fzi.cooperate.sequence.lifeline.LifelineEditDialog;
import de.fzi.cooperate.sequence.lifeline.LifelineOperations;
import de.fzi.cooperate.sequence.message.MessageDialog;
import de.fzi.cooperate.sequence.message.MessageEditDialog;
import de.fzi.cooperate.sequence.message.MessageMoveDialog;
import de.fzi.cooperate.sequence.message.MessageOperations;
import de.fzi.cooperate.sequence.stateinvariant.StateInvariantDialog;
import de.fzi.cooperate.sequence.stateinvariant.StateInvariantEditDialog;
import de.fzi.cooperate.sequence.stateinvariant.StateInvariantOperations;

/**
 * Class representing the java action calls that can be used inside of the sirius editor.
 * <p>
 * Tools inside the sirius editor definition, define a parameters for the action to call with a
 * string (i.e.: "newLifeline") and the context element with their own type.
 */
public class DialogInvoker extends AbstractExternalJavaAction implements IExternalJavaAction {

    private static final String LL_NEW = "newLifeline";
    private static final String LL_EDIT = "editLifeline";
    private static final String LL_DEL = "delLifeline";
    private static final String MSG_NEW = "newMessage";
    private static final String MSG_EDIT = "editMessage";
    private static final String MSG_DEL = "delMessage";
    private static final String MSG_MOVETOIOP = "moveMessageToIop";
    private static final String SI_NEW = "newStateInvariant";
    private static final String SI_EDIT = "editStateInvariant";
    private static final String SI_DEL = "delStateInvariant";
    private static final String CF_NEW = "newCombinedFrag";
    private static final String CF_EDIT = "editCombinedFrag";
    private static final String CF_DEL = "delCombinedFrag";
    private static final String IOP_NEW = "newInterOp";
    private static final String IOP_EDIT = "editInterOp";
    private static final String IOP_DEL = "delInterOp";
    private static final String PARAM_INTERAC = "interaction";
    private static final String PARAM_LL = "lifeline";
    private static final String PARAM_MSG = "message";
    private static final String PARAM_SI = "stateinvariant";
    private static final String PARAM_CF = "combinedfragment";
    private static final String PARAM_IOP = "interactionoperand";

    @Override
    public boolean canExecute(final Collection<? extends EObject> args) {
        return true;
    }

    @Override
    public void execute(final Collection<? extends EObject> args, final Map<String, Object> umlObjects) {
        final String action = (String) umlObjects.get("action");
        Interaction interaction;
        Lifeline lifeline;
        StateInvariant stateInvariant;
        CombinedFragment combinedFrag;
        Message msg;
        InteractionOperand interOp;

        switch (action) {
        case LL_NEW:
            interaction = (Interaction) umlObjects.get(PARAM_INTERAC);
            new LifelineDialog(interaction);
            break;
        case LL_EDIT:
            lifeline = (Lifeline) umlObjects.get(PARAM_LL);
            new LifelineEditDialog(lifeline.getInteraction(), lifeline);
            break;
        case LL_DEL:
            lifeline = (Lifeline) umlObjects.get(PARAM_LL);
            LifelineOperations.deleteLifeline(lifeline.getInteraction(), lifeline);
            break;
        case MSG_NEW:
            interaction = (Interaction) umlObjects.get(PARAM_INTERAC);
            new MessageDialog(interaction);
            break;
        case MSG_EDIT:
            msg = (Message) umlObjects.get(PARAM_MSG);
            new MessageEditDialog(msg.getInteraction(), msg);
            break;
        case MSG_DEL:
            msg = (Message) umlObjects.get(PARAM_MSG);
            MessageOperations.deleteMessagePair(msg);
            break;
        case MSG_MOVETOIOP:
            msg = (Message) umlObjects.get(PARAM_MSG);
            new MessageMoveDialog(msg.getInteraction(), msg);
            break;
        case SI_NEW:
            interaction = (Interaction) umlObjects.get(PARAM_INTERAC);
            new StateInvariantDialog(interaction);
            break;
        case SI_EDIT:
            stateInvariant = (StateInvariant) umlObjects.get(PARAM_SI);
            new StateInvariantEditDialog(stateInvariant.getEnclosingInteraction(), stateInvariant);
            break;
        case SI_DEL:
            stateInvariant = (StateInvariant) umlObjects.get(PARAM_SI);
            StateInvariantOperations.deleteStateInvariant(stateInvariant);
            break;
        case CF_NEW:
            interaction = (Interaction) umlObjects.get(PARAM_INTERAC);
            new CombinedFragmentDialog(interaction);
            break;
        case CF_EDIT:
            combinedFrag = (CombinedFragment) umlObjects.get(PARAM_CF);
            new CombinedFragmentEditDialog(combinedFrag.getEnclosingInteraction(), combinedFrag);
            break;
        case CF_DEL:
            combinedFrag = (CombinedFragment) umlObjects.get(PARAM_CF);
            CombinedFragmentOperations.deleteCombinedFragment(combinedFrag);
            break;
        case IOP_NEW:
            combinedFrag = (CombinedFragment) umlObjects.get(PARAM_CF);
            new InteractionOperandDialog(combinedFrag);
            break;
        case IOP_EDIT:
            interOp = (InteractionOperand) umlObjects.get(PARAM_IOP);
            new InteractionOperandEditDialog(interOp);
            break;
        case IOP_DEL:
            interOp = (InteractionOperand) umlObjects.get(PARAM_IOP);
            InteractionOperandOperations.deleteInteractionOperand(interOp);
            break;
        default:
            break;
        }
        /* Old DialogInvoker stuff */
        // final String elementType = (String) umlObjects.get("type");
        // final String operationType = (String) umlObjects.get("op");
        // // Lifeline dialog branch
        // if (elementType.equals("'LL'")) {
        // lifeline = (Lifeline) umlObjects.get("lifeline");
        // if (operationType.equals("'edit'")) {
        // new LifelineEditDialog(lifeline.getInteraction(), lifeline);
        // } else if (operationType.contentEquals("'del'")) {
        // /* Deleting lifelines currently breaks the model. If the start and finish occurrences of
        // action execution
        // specifications get removed then they need to be replaced with execution occurrence
        // specifications */
        // Interaction lInter = lifeline.getInteraction();
        // LifelineOperations.deleteLifeline(lInter, lifeline);
        // } else {
        //
        // interaction = (Interaction) umlObjects.get("interaction");
        // new LifelineDialog(interaction);
        // }
        // }
        //
        // // Message dialog branch
        // if (elementType.equals("'Msg'")) {
        // msg = (Message) umlObjects.get("message");
        // if (operationType.equals("'edit'")) {
        // new MessageEditDialog(msg.getInteraction(), msg);
        // } else if (operationType.equals("'del'")) {
        // MessageOperations.deleteMessagePair(msg);
        // } else if (operationType.equals("'create'")) {
        // interaction = (Interaction) umlObjects.get("interaction");
        // new MessageDialog(interaction);
        // } else if (operationType.equals("'move'")) {
        // new MessageMoveDialog(msg.getInteraction(), msg);
        // }
        // }
        //
        // // State invariant dialog branch
        // if (elementType.equals("'SI'")) {
        // stateInvariant = (StateInvariant) umlObjects.get("stateinvariant");
        // if (operationType.equals("'edit'")) {
        // new StateInvariantEditDialog(stateInvariant.getEnclosingInteraction(), stateInvariant);
        // } else if (operationType.equals("'del'")) {
        // StateInvariantOperations.deleteStateInvariant(stateInvariant);
        // } else {
        // interaction = (Interaction) umlObjects.get("interaction");
        // new StateInvariantDialog(interaction);
        // }
        // }
        //
        // // Combined fragment dialog branch
        // if (elementType.equals("'CF'")) {
        // combinedFrag = (CombinedFragment) umlObjects.get("combinedfragment");
        // if (operationType.equals("'edit'")) {
        // new CombinedFragmentEditDialog(combinedFrag.getEnclosingInteraction(), combinedFrag);
        // } else if (operationType.equals("'del'")) {
        // CombinedFragmentOperations.deleteCombinedFragment(combinedFrag);
        // } else {
        // interaction = (Interaction) umlObjects.get("interaction");
        // new CombinedFragmentDialog(interaction);
        // }
        // }
        //
        // // Interaction operand dialog branch
        // if (elementType.equals("'IO'")) {
        // interOp = (InteractionOperand) umlObjects.get("interactionoperand");
        // if (operationType.equals("'edit'")) {
        // new InteractionOperandEditDialog(interOp);
        // } else if (operationType.equals("'del'")) {
        // InteractionOperandOperations.deleteInteractionOperand(interOp);
        // } else {
        // combinedFrag = (CombinedFragment) umlObjects.get("combinedfragment");
        // new InteractionOperandDialog(combinedFrag);
        // }
        // }

    }

}
