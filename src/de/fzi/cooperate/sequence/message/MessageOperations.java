package de.fzi.cooperate.sequence.message;

import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageKind;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OccurrenceSpecification;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

import de.fzi.cooperate.sequence.InsertPoint;
import de.fzi.cooperate.sequence.ModelUtilities;

public class MessageOperations {

    /**
     * Creates a new message and the corresponding message occurrence specifications in the current
     * interaction containment.
     * <p>
     * If the chosen {@link MessageSort} literal is sync or asynch, the return message, message
     * occurrence specifications and action execution specification will get generated too.
     *
     * @param interaction
     *            the current {@link Interaction} object, not <code>null</code>
     * @param name
     *            the name for the new {@link Message} object, chosen by the user, not
     *            <code>null</code>
     * @param origLifeline
     *            the {@link Lifeline} object from where the new message originates, not
     *            <code>null</code>
     * @param destLifeline
     *            the {@link Lifeline} object at which the new message ends, not <code>null</code>
     * @param sort
     *            the {@link MessageSort} literal which represents the type of the new message, not
     *            <code>null</code>
     * @param kind
     *            the {@link MessageKind} literal which represents the kind of the new message, not
     *            <code>null</code>
     * @param insert
     *            the {@link MessageOccurrenceSpecification} object at which the new message should
     *            be inserted, not <code></code>
     * @param insertPoint
     *            the {@link InsertPoint} enum which indicates whether the new message should be
     *            inserted after or before the selected message on the originating {@link Lifeline}
     *            object, not <code>null</code>
     * @param params
     *            the {@link Map} object containing pairs of {@link String} objects representing the
     *            chosen parameters by the user, where the key is the name of the parameter and the
     *            value represents the type, is <code>null</code> if the user doesn't want any
     *            parameters
     * @param returns
     *            the {@link String} array containing the return parameter name and type as
     *            {@link String} objects, is <code>null</code> if the user doesn't want a return
     *            parameter, has always length 2 when not null
     */
    public static void createMessage(final Interaction interaction, final String name, final Lifeline origLifeline,
            final Lifeline destLifeline, final MessageSort sort, final MessageKind kind,
            final MessageOccurrenceSpecification insert, final InsertPoint insertPoint,
            final Map<String, String> params, final String[] returns) {

        // Local variable declaration
        final EList<InteractionFragment> interFragments = interaction.getFragments();
        final ActionExecutionSpecification acExSpInsert = ModelUtilities.isExecutionStartOrEnd(origLifeline, insert);

        // TODO: Cases for lost/found messages and create/delete messages

        // Create the new message pair in the interaction containment
        final Message callMessage = interaction.createMessage(name + "_Call");
        final Message returnMessage = interaction.createMessage(name + "_Return");

        // Set messageSort properties for the new messages
        callMessage.setMessageSort(sort);
        returnMessage.setMessageSort(MessageSort.REPLY_LITERAL);

        // acExSpInsert is not null if the insert message occurrence specification is a start or
        // finish of an action execution specification on the originating lifeline
        if (acExSpInsert != null) {

            // Count variable for inserting the newly created elements
            int b = 0;

            // The position (b count variable) changes depending if the insert message occurence
            // specification is a send event or a receive event and depending on the insertPoint
            // enum value
            if (insert.isSend()) {
                if (insertPoint.equals(InsertPoint.AFTER)) {
                    b = interFragments.indexOf(insert.getMessage().getReceiveEvent()) + 1;
                } else {
                    b = interFragments.indexOf(insert);
                }
            } else {
                if (insertPoint.equals(InsertPoint.AFTER)) {
                    b = interFragments.indexOf(insert) + 1;
                } else {
                    b = interFragments.indexOf(insert.getMessage().getSendEvent());
                }
            }

            // Create the necessary message occurrence specifications for the new messages (2 for
            // each message, send and receive event)
            final MessageOccurrenceSpecification callSendEventOccur = createMOS(name + "CallSend", origLifeline,
                    callMessage);
            final MessageOccurrenceSpecification callRecvEventOccur = createMOS(name + "CallRecv", destLifeline,
                    callMessage);
            final MessageOccurrenceSpecification returnSendEventOccur = createMOS(name + "ReturnSend", destLifeline,
                    returnMessage);
            final MessageOccurrenceSpecification returnRecvEventOccur = createMOS(name + "ReturnRecv", origLifeline,
                    returnMessage);

            // Set the send event and receive event properties of the new messages
            callMessage.setSendEvent(callSendEventOccur);
            callMessage.setReceiveEvent(callRecvEventOccur);
            returnMessage.setSendEvent(returnSendEventOccur);
            returnMessage.setReceiveEvent(returnRecvEventOccur);

            // Declaration of an action execution and the corresponding execution occurrences if the
            // new message pair doesn't get inserted on an existing action execution
            ActionExecutionSpecification origAcExSp = null;
            ExecutionOccurrenceSpecification origExOcSt = null;
            ExecutionOccurrenceSpecification origExOcE = null;

            // If the new message pair is not inserted on an existing action execution
            // specification, a new action execution and corresponding execution occurrences need to
            // be created
            if ((insert.isSend() && insertPoint == InsertPoint.AFTER)
                    || (insert.isReceive() && insertPoint == InsertPoint.BEFORE)) {

                // Count the existing action executions
                final int countAcEx = ModelUtilities.getActionExecutionCount(origLifeline);

                // Create execution occurrence specifications for the new action execution
                // specification
                origExOcSt = createEOS(origLifeline, origAcExSp, countAcEx, "St");
                origExOcE = createEOS(origLifeline, origAcExSp, countAcEx, "E");

                // Create new action execution specification on the originating lifeline
                origAcExSp = createAES(origLifeline, origExOcSt, origExOcE, countAcEx);

            }

            // Count the existing action executions
            final int destCountAcEx = ModelUtilities.getActionExecutionCount(destLifeline);

            // Create new action execution specification on the destination lifeline
            final ActionExecutionSpecification destAcExSp = createAES(destLifeline, callRecvEventOccur,
                    returnSendEventOccur, destCountAcEx);

            // Add the new fragments to the interaction containment depending on if there was a new
            // action execution created or not
            if ((insert.isSend() && insertPoint == InsertPoint.AFTER)
                    || (insert.isReceive() && insertPoint == InsertPoint.BEFORE)) {
                interFragments.add(b, origExOcSt);
                interFragments.add(b + 1, origAcExSp);
                interFragments.add(b + 2, callSendEventOccur);
                interFragments.add(b + 3, destAcExSp);
                interFragments.add(b + 4, callRecvEventOccur);
                interFragments.add(b + 5, returnSendEventOccur);
                interFragments.add(b + 6, returnRecvEventOccur);
                interFragments.add(b + 7, origExOcE);
            } else {
                interFragments.add(b, callSendEventOccur);
                interFragments.add(b + 1, destAcExSp);
                interFragments.add(b + 2, callRecvEventOccur);
                interFragments.add(b + 3, returnSendEventOccur);
                interFragments.add(b + 4, returnRecvEventOccur);
            }

        } else {
            int a = interFragments.indexOf(insert);
            if (insert.isReceive()) {
                a++;
            }

            // Create the necessary message occurrence specifications for the new messages (2 for
            // each message, send and receive event)
            final MessageOccurrenceSpecification callSendEventOccur = createMOS(name + "CallSend", origLifeline,
                    callMessage);
            final MessageOccurrenceSpecification callRecvEventOccur = createMOS(name + "CallRecv", destLifeline,
                    callMessage);
            final MessageOccurrenceSpecification returnSendEventOccur = createMOS(name + "ReturnSend", destLifeline,
                    returnMessage);
            final MessageOccurrenceSpecification returnRecvEventOccur = createMOS(name + "ReturnRecv", origLifeline,
                    returnMessage);

            // Set the send event and receive event properties of the new messages
            callMessage.setSendEvent(callSendEventOccur);
            callMessage.setReceiveEvent(callRecvEventOccur);
            returnMessage.setSendEvent(returnSendEventOccur);
            returnMessage.setReceiveEvent(returnRecvEventOccur);

            // Count the existing action executions
            final int destCountAcEx = ModelUtilities.getActionExecutionCount(destLifeline);

            // Create new action execution specification on the destination lifeline
            final ActionExecutionSpecification acExSp = createAES(destLifeline, callRecvEventOccur,
                    returnSendEventOccur, destCountAcEx);

            // Add fragments to the interaction containment
            interFragments.add(a, callSendEventOccur);
            interFragments.add(a + 1, acExSp);
            interFragments.add(a + 2, callRecvEventOccur);
            interFragments.add(a + 3, returnSendEventOccur);
            interFragments.add(a + 4, returnRecvEventOccur);

        }

        createParametersAndReturn(interaction, name, destLifeline, params, returns, callMessage, returnMessage);

    }

    private static void createParametersAndReturn(final Interaction interaction, final String name,
            final Lifeline destLifeline, final Map<String, String> params, final String[] returns,
            final Message callMessage, final Message returnMessage) {
        // Create and set new signature/operation in destination class)
        final org.eclipse.uml2.uml.Class p = (org.eclipse.uml2.uml.Class) interaction.getModel()
                .getPackagedElement(destLifeline.getRepresents().getType().getName());
        final Operation o = p.createOwnedOperation(name, null, null);

        // Get the model, its associated resource set and the resource for the primitive types
        final Model currentModel = interaction.getModel();
        org.eclipse.uml2.uml.Package package_ = null;
        final Resource resource = currentModel.eResource().getResourceSet()
                .getResource(URI.createURI("pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml"), false);
        package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(),
                UMLPackage.Literals.PACKAGE);

        // Create parameters for the operation, if there are parameters in the params map
        if (params != null && params.size() > 0) {

            // The map contains pairs of strings where the key represents the name of the parameter
            // and the value represents the type of the parameter
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                if (ModelUtilities.isPrimitiveType(entry.getValue())) {

                    // Primitive type parameter
                    o.createOwnedParameter(entry.getKey(),
                            (Type) package_.getModel().getPackagedElement(entry.getValue()));
                } else {

                    // Class type parameter
                    o.createOwnedParameter(entry.getKey(),
                            (Class) interaction.getModel().getPackagedElement(entry.getValue()));
                }
            }
        }

        // Create return parameter, if there is one in the returns array
        if (returns != null && returns.length == 2) {
            Parameter returnParam;
            if (ModelUtilities.isPrimitiveType(returns[1])) {
                // Primitive type parameter
                returnParam = o.createOwnedParameter(returns[0],
                        (Type) package_.getModel().getPackagedElement(returns[1]));
            } else {
                // Class type parameter
                returnParam = o.createOwnedParameter(returns[0],
                        (Class) interaction.getModel().getPackagedElement(returns[1]));
            }
            returnParam.setDirection(ParameterDirectionKind.RETURN_LITERAL);
        }

        // Set the signature property of the messages
        callMessage.setSignature(o);
        returnMessage.setSignature(o);
    }

    public static void createFirstMessage(final Interaction interaction, final String name, final Lifeline origLifeline,
            final Lifeline destLifeline, final MessageSort sort, final MessageKind kind,
            final Map<String, String> params, final String[] returns) {

        final EList<InteractionFragment> interFragments = interaction.getFragments();
        // Create the new message pair in the interaction containment
        final Message callMessage = interaction.createMessage(name + "_Call");
        final Message returnMessage = interaction.createMessage(name + "_Return");

        // Set messageSort properties for the new messages
        callMessage.setMessageSort(sort);
        returnMessage.setMessageSort(MessageSort.REPLY_LITERAL);

        // Create the necessary message occurrence specifications for the new messages (2 for each
        // message, send and receive event)
        final MessageOccurrenceSpecification callSendEventOccur = createMOS(name + "CallSend", origLifeline,
                callMessage);
        final MessageOccurrenceSpecification callRecvEventOccur = createMOS(name + "CallRecv", destLifeline,
                callMessage);
        final MessageOccurrenceSpecification returnSendEventOccur = createMOS(name + "ReturnSend", destLifeline,
                returnMessage);
        final MessageOccurrenceSpecification returnRecvEventOccur = createMOS(name + "ReturnRecv", origLifeline,
                returnMessage);

        // Set the send event and receive event properties of the new messages
        callMessage.setSendEvent(callSendEventOccur);
        callMessage.setReceiveEvent(callRecvEventOccur);
        returnMessage.setSendEvent(returnSendEventOccur);
        returnMessage.setReceiveEvent(returnRecvEventOccur);

        // Count the existing action executions
        final int destCountAcEx = ModelUtilities.getActionExecutionCount(destLifeline);

        // Create new action execution specification on the destination lifeline
        final ActionExecutionSpecification acExSp = createAES(destLifeline, callRecvEventOccur, returnSendEventOccur,
                destCountAcEx);

        final int a = interFragments.indexOf(origLifeline.getCoveredBys().stream()
                .filter(frag -> frag.eClass().equals(UMLPackage.eINSTANCE.getActionExecutionSpecification()))
                .collect(Collectors.toList()).get(0)) + 1;

        // Add fragments to the interaction containment
        interFragments.add(a, callSendEventOccur);
        interFragments.add(a + 1, acExSp);
        interFragments.add(a + 2, callRecvEventOccur);
        interFragments.add(a + 3, returnSendEventOccur);
        interFragments.add(a + 4, returnRecvEventOccur);

        createParametersAndReturn(interaction, name, destLifeline, params, returns, callMessage, returnMessage);
    }

    /**
     * Edits an existing {@link Message} and the corresponding
     * {@link MessageOccurrenceSpecification}s in the current {@link Interaction} containment.
     *
     * @param interaction
     *            the current {@link Interaction} object, not <code>null</code>
     * @param name
     *            the name for the new {@link Message} object, chosen by the user, not
     *            <code>null</code>
     * @param origLifeline
     *            the {@link Lifeline} object from where the new message originates, not
     *            <code>null</code>
     * @param destLifeline
     *            the {@link Lifeline} object at which the new message ends, not <code>null</code>
     * @param sort
     *            the {@link MessageSort} literal which represents the type of the new message, not
     *            <code>null</code>
     * @param kind
     *            the {@link MessageKind} literal which represents the kind of the new message, not
     *            <code>null</code>
     * @param insert
     *            the {@link MessageOccurrenceSpecification} object at which the new message should
     *            be inserted, not <code>null</code>
     * @param editMessage
     *            the {@link Message} which should be edited, not <code>null</code>
     */
    public static void editMessage(final String name, final MessageOccurrenceSpecification insertMos,
            final Message editMessage) {

        // If the name variable differs from the message's current name, change to the new name
        editMessage.setName(name);

        if (insertMos.getMessage() != editMessage) {
            final EList<InteractionFragment> fragments = editMessage.getInteraction().getFragments();
            final int[] indexes = getMessageIntervall(editMessage);
            final int start = indexes[0];
            final int end = indexes[1];
            final int insertIndex = insertMos.isSend() ? fragments.indexOf(insertMos)
                    : fragments.indexOf(insertMos) + 1;
            for (int i = 0; i <= end - start; i++) {
                if (start > insertIndex) {
                    fragments.add(insertIndex - 1 + i, fragments.remove(start + i));
                    // fragments.move(insertIndex-1+i, fragments.get(start));
                } else {
                    fragments.add(insertIndex - 1, fragments.remove(start));
                    // fragments.move(insertIndex-1, fragments.get(start));
                }
            }
        }
    }

    /**
     * Deletes a {@link Message} pair from its {@link Interaction}'s containment.
     * 
     * @param delMessage
     *            the "root" {@link Message} of the pair
     */
    public static void deleteMessagePair(final Message delMessage) {
        // Local variable declaration
        final Interaction inter = delMessage.getInteraction();

        final EList<InteractionFragment> interFragments = inter.getFragments();
        final int indexes[] = getMessageIntervall(delMessage);
        final int startIndex = indexes[0];
        final int endIndex = indexes[1];
        for (int i = startIndex; i <= endIndex; i++) {
            final InteractionFragment frag = interFragments.remove(startIndex);
            if (frag.eClass().equals(UMLPackage.eINSTANCE.getMessageOccurrenceSpecification())) {
                if (((MessageOccurrenceSpecification) frag).getMessage() != null) {
                    EcoreUtil.remove(((MessageOccurrenceSpecification) frag).getMessage());
                }
            }
        }
    }

    /**
     * Deletes a single {@link Message} object and its associated
     * {@link MessageOccurrenceSpecification} objects and {@link Operation} object.
     * 
     * @param msg
     *            the {@link Message} object that should be deleted, not <code>null</code>
     */
    public static void deleteMessage(final Message msg) {

        // Local variable declarations
        // Interaction inter = msg.getInteraction();
        // EList<InteractionFragment> interFragments = inter.getFragments();
        final MessageOccurrenceSpecification sendEvent = (MessageOccurrenceSpecification) msg.getSendEvent();
        final MessageOccurrenceSpecification receiveEvent = (MessageOccurrenceSpecification) msg.getReceiveEvent();

        // System.out.println(msg.getName());
        // ActionExecutionSpecification aesCheck =
        // ModelUtilities.isExecutionStart(receiveEvent.getCovered(), receiveEvent);
        // System.out.println(msg.getMessageSort() + " " + aesCheck);
        // if (!msg.getMessageSort().equals(MessageSort.REPLY_LITERAL) && aesCheck != null) {
        // ExecutionOccurrenceSpecification eos = createEOS(receiveEvent.getCovered(), aesCheck,
        // ModelUtilities.getActionExecutionCount(receiveEvent.getCovered()), "St");
        // aesCheck.setStart(eos);
        // interFragments.add(interFragments.indexOf(aesCheck), eos);
        // System.out.println("Created EOS Start");
        // }
        // aesCheck = ModelUtilities.isExecutionEnd(sendEvent.getCovered(), sendEvent);
        // if (msg.getMessageSort().equals(MessageSort.REPLY_LITERAL) && aesCheck != null) {
        // ExecutionOccurrenceSpecification eos = createEOS(sendEvent.getCovered(), aesCheck,
        // ModelUtilities.getActionExecutionCount(sendEvent.getCovered()), "E");
        // aesCheck.setStart(eos);
        // interFragments.add(interFragments.indexOf(sendEvent), eos);
        // System.out.println("Created EOS END");
        // }

        // Remove the message occurrence specifications from the interaction containment
        EcoreUtil.remove(sendEvent);
        EcoreUtil.remove(receiveEvent);

        // Remove the message from the interaction containment
        EcoreUtil.remove(msg);
    }

    public static void moveMessageToInterOp(final Message msg, final InteractionOperand interOp) {
        final Interaction inter = msg.getInteraction();
        final EList<InteractionFragment> fragments = inter.getFragments();
        final EList<InteractionFragment> interOpFragments = interOp.getFragments();
        final int indexes[] = getMessageIntervall(msg);
        final int startIndex = indexes[0];
        final int endIndex = indexes[1];
        for (int i = startIndex; i <= endIndex; i++) {
            interOpFragments.add(fragments.get(startIndex));
        }
    }

    /**
     * Creates and returns a new {@link MessageOccurrenceSpecification} object and sets the required
     * properties.
     * 
     * @param name
     *            the name for the new {@link MessageOccurrenceSpecification} object, not
     *            <code>null</code>
     * @param covers
     *            the {@link Lifeline} object which the new {@link MessageOccurrenceSpecification}
     *            object covers, not <code>null</code>
     * @param msg
     *            the {@link Message} object which the new {@link MessageOccurrenceSpecification}
     *            object is part of, not <code>null</code>
     * @return the created {@link MessageOccurrenceSpecification} object
     */
    private static MessageOccurrenceSpecification createMOS(final String name, final Lifeline covers,
            final Message msg) {

        // Local variable declarations
        final MessageOccurrenceSpecification mos = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();

        // Set the required properties
        mos.setName(name);
        mos.getCovereds().add(covers);
        mos.setMessage(msg);

        return mos;
    }

    /**
     * Creates and returns a new {@link ActionExecutionSpecification} object and sets the required
     * properties.
     * 
     * @param lifeline
     *            the {@link Lifeline} object which is covered by the new
     *            {@link ActionExecutionSpecification} object, not <code>null</code>
     * @param start
     *            the {@link OccurrenceSpecification} object which represents the start of the new
     *            {@link ActionExecutionSpecification} object, not <code>null</code>
     * @param finish
     *            the {@link OccurrenceSpecification} object which represents the end of the new
     *            {@link ActionExecutionSpecification} object, not <code>null</code>
     * @param count
     *            the amount of {@link ActionExecutionSpecification} objects already existing on the
     *            {@link Lifeline} object, not <code>null</code>
     * @return the new {@link ActionExecutionSpecification} object
     */
    private static ActionExecutionSpecification createAES(final Lifeline lifeline, final OccurrenceSpecification start,
            final OccurrenceSpecification finish, final int count) {

        // Create new action execution specification
        final ActionExecutionSpecification acExSp = UMLFactory.eINSTANCE.createActionExecutionSpecification();

        // Set the necessary properties
        acExSp.getCovereds().add(lifeline);
        acExSp.setName(lifeline.getName() + "AcExSp" + count);
        acExSp.setStart(start);
        acExSp.setFinish(finish);

        return acExSp;
    }

    /**
     * Creates and returns a new {@link ExecutionOccurrenceSpecification} and sets the required
     * properties.
     * 
     * @param lifeline
     *            the {@link Lifeline} object which is covered by the new
     *            {@link ExecutionOccurrenceSpecification}, not <code>null</code>
     * @param acExSp
     *            the {@link ActionExecutionSpecification} associated with the new
     *            {@link ExecutionOccurrenceSpecification}, not <code>null</code>
     * @param count
     *            the count of {@link ActionExecutionSpecification}s on the lifeline, for naming,
     *            not <code>null</code>
     * @param opt
     *            a string for adding an additional bit to the name (i.e.: "Start" or "End"), can be
     *            empty
     * @return the new {@link ExecutionOccurrenceSpecification}, not <code>null</code>
     */
    private static ExecutionOccurrenceSpecification createEOS(final Lifeline lifeline,
            final ActionExecutionSpecification acExSp, final int count, final String opt) {
        final ExecutionOccurrenceSpecification exOcSp = UMLFactory.eINSTANCE.createExecutionOccurrenceSpecification();
        exOcSp.setCovered(lifeline);
        exOcSp.setName(lifeline.getName() + "ExOc" + opt + count);
        exOcSp.setExecution(acExSp);
        return exOcSp;
    }

    /**
     * Gets the intervall of {@link InteractionFragment}s between the start and end of a message
     * pair
     * 
     * @param msg
     *            the {@link Message} to start at
     * @return the intervall (two values, start and end) of indexes
     */
    private static int[] getMessageIntervall(final Message msg) {
        final EList<InteractionFragment> frags = msg.getInteraction().getFragments();
        final MessageOccurrenceSpecification startEvent = (MessageOccurrenceSpecification) msg.getSendEvent();
        final MessageOccurrenceSpecification startReceiveEvent = (MessageOccurrenceSpecification) msg.getReceiveEvent();
        MessageOccurrenceSpecification endEvent = null;
        final ActionExecutionSpecification acex = ModelUtilities.isExecutionStart(startReceiveEvent.getCovered(),
                startReceiveEvent);
        if (acex != null) {
            endEvent = (MessageOccurrenceSpecification) ((MessageOccurrenceSpecification) acex.getFinish()).getMessage()
                    .getReceiveEvent();
        }
        return new int[] { frags.indexOf(startEvent), frags.indexOf(endEvent) };
    }

}
