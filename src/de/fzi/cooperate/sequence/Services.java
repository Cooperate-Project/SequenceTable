package de.fzi.cooperate.sequence;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageKind;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * The services class used by VSM.
 */
public class Services {

    /**
     * Determines the {@link Message}s relevant to a specific {@link Lifeline}.
     * 
     * @param lifeline
     *            the {@link Lifeline} for which the messages should be determined, not
     *            <code>null</code>
     * @return a {@link List} containing the determined {@link Message}s, can be empty when there
     *         are no {@link Message}s yet
     */
    public List<Message> getMessages(final Lifeline lifeline) {
        final EList<Element> frags = lifeline.getInteraction().allOwnedElements();
        return frags.stream().filter(t -> t.eClass().equals(UMLPackage.eINSTANCE.getMessageOccurrenceSpecification()))
                .map(MessageOccurrenceSpecification.class::cast)
                .filter(t -> (t.isSend() && t.getCovered() == lifeline) || (t.isReceive() && t.getCovered() == lifeline
                        && t.getMessage().getMessageSort().equals(MessageSort.ASYNCH_SIGNAL_LITERAL)))
                .map(mso -> mso.getMessage()).collect(Collectors.toList());
    }

    /**
     * Returns the name of a {@link Message}.
     * 
     * @param msg
     *            the {@link Message}
     * @return the name of the {@link Message}'s operation
     */
    public String getMessageName(final Message msg) {
        if (msg.getMessageSort().equals(MessageSort.ASYNCH_SIGNAL_LITERAL)
                && msg.getMessageKind().equals(MessageKind.LOST_LITERAL)) {
            return msg.getName();
        }
        return msg.getSignature().getName();
    }

    /**
     * Gets the parameters for a {@link Message} of type {@link ParameterDirectionKind}
     * 
     * @param msg
     *            the {@link Message}, not <code>null</code>
     * @param paramDirecKind
     *            the type of parameter, not <code>null</code>
     * @return
     */
    public String getParametersWithType(final Message msg, final ParameterDirectionKind paramDirecKind) {
        final Operation op = (Operation) msg.getSignature();
        final List<Parameter> parameters = op.getOwnedParameters().stream()
                .filter(param -> param.getDirection().equals(paramDirecKind)).collect(Collectors.toList());
        String paramStr = "";
        for (int i = 0; i < parameters.size(); i++) {
            final Parameter p = parameters.get(i);
            paramStr += p.getName() + " : " + p.getType().getName();
            if (!((i + 1) >= parameters.size())) {
                paramStr += ", ";
            }
        }
        return paramStr;
    }

    /**
     * Uses getParametersWithType to return the parameters of a {@link Message} as String
     * 
     * @param msg
     *            the message to get the parameters of
     * @return the parameters as a string consisting out of the names and types of the parameters
     */
    public String getInParameters(final Message msg) {
        return getParametersWithType(msg, ParameterDirectionKind.IN_LITERAL);
    }

    /**
     * Uses getParametersWithType to return the return parameter of a {@link Message} as String
     * 
     * @param msg
     *            the message to get the parameters of
     * @return the parameter as a string consisting out of the names and type of the parameter
     */
    public String getReturn(final Message msg) {
        return getParametersWithType(msg, ParameterDirectionKind.RETURN_LITERAL);
    }
}
