package de.fzi.cooperate.sequence;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Class providing various utility functions for uml elements, enums and more
 *
 */
public class ModelUtilities {

    private ModelUtilities() {
    };

    /**
     * Returns a default action listener for a cancel button.
     *
     * @return the action listener with the dispose functionality, not <code>null</code>
     */
    public static ActionListener getCancelActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                // Disposes the button's window
                SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
            }
        };
    }

    /**
     * Returns the centered position for a given window.
     *
     * @param frame
     *            the window for which the center position should be calculated, not
     *            <code>null</code>
     * @return the center position for the window as {@link Point} object, not <code>null</code>
     */
    public static Point getScreenCenterPosition(final Window frame) {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        final int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        return new Point(x, y);
    }

    /**
     * Returns a {@link Lifeline} array with all the selected in a given {@link Lifeline} array.
     *
     * @param allLifelines
     *            the {@link Lifeline} array containing "all" {@link Lifeline}s, not
     *            <code>null</code>
     * @param selections
     *            the int array containing the indices of the selected {@link Lifeline} in the
     *            allLifelines array, not <code>null</code>
     * @return the {@link Lifeline} array containing all the selected {@link Lifeline}s, not
     *         <code>null</code>
     */
    public static Lifeline[] getSelectedLifelines(final Lifeline[] allLifelines, final int[] selections) {
        final Lifeline[] lifelines = new Lifeline[selections.length];
        for (int i = 0; i < selections.length; i++) {
            lifelines[i] = allLifelines[selections[i]];
        }
        return lifelines;
    }

    /**
     * Generic version of getSelectedLifelines.
     */
    public static <T> T[] getSelectedElements(final Class<T> arrayType, final T[] allElements, final int[] selections) {
        @SuppressWarnings("unchecked")
        final T[] selElements = (T[]) Array.newInstance(arrayType, selections.length);
        for (int i = 0; i < selections.length; i++) {
            selElements[i] = allElements[selections[i]];
        }
        return selElements;
    }

    /**
     * Returns a list of {@link Message}s that are relevant to a given lifeline.
     *
     * @param messages
     *            the {@link EList} containing all existing {@link Message}s, not <code>null</code>
     * @param lifeline
     *            the {@link Lifeline} for which the relevant {@link Message}s should get
     *            calculated, not <code>null</code>
     * @return the list containing the {@link Message}s that have a
     *         {@link MessageOccurrenceSpecification} that is associated with the {@link Lifeline},
     *         not <code>null</code>
     */
    public static List<Message> getRelevantMessages(final EList<Message> messages, final Lifeline lifeline) {
        final List<Message> filteredMessages = new ArrayList<Message>();
        for (final Message m : messages) {
            final MessageOccurrenceSpecification smos = (MessageOccurrenceSpecification) m.getSendEvent();
            final MessageOccurrenceSpecification rmos = (MessageOccurrenceSpecification) m.getReceiveEvent();
            if ((smos != null && smos.getCovered() == lifeline) || (rmos != null && rmos.getCovered() == lifeline)) {
                filteredMessages.add(m);
            }
        }
        return filteredMessages;
    }

    /**
     * Splits a string containing parameter name / type pairs into a {@link Map}.
     *
     * @param paramString
     *            the string containing the pairs, not <code>null</code>
     * @return the map containing the parameter name / type pairs, where the key represents the name
     *         of the parameter and the value the type of the parameter, not <code>null</code>
     */
    public static LinkedHashMap<String, String> getParameters(final String paramString) {
        final LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        final String[] paramPairs = paramString.split(";");
        for (final String p : paramPairs) {
            final String[] s = p.split(":");
            if (s.length == 2) {
                parameters.put(s[0], s[1]);
            } else {
                return null;
            }
        }
        return parameters;
    }

    /**
     * Returns a string array with the name and type of a return parameter for a given string.
     *
     * @param returnString
     *            the string containing the return parameter name / type pair, not <code>null</code>
     * @return the string array, not <code>null</code>
     */
    public static String[] getReturn(final String returnString) {
        return returnString.split(":");
    }

    /**
     * Returns true if the given string corresponds to a known primitive type.
     *
     * @param check
     *            the string to be checked, not <code>null</code>
     * @return true if the string corresponds to a known primitive type, else false, not
     *         <code>null</code>
     */
    public static boolean isPrimitiveType(final String check) {
        switch (check) {
        case "String":
            return true;
        case "Boolean":
            return true;
        case "Integer":
            return true;
        // TODO: list of types not complete!
        default:
            return false;
        }
    }

    /**
     * Checks if a given {@link MessageOccurrenceSpecification} is a start or finish point on any
     * {@link ActionExecutionSpecification} for a given {@link Lifeline}.
     *
     * @param l
     *            the {@link Lifeline} to check for, not <code>null</code>
     * @param mos
     *            the {@link MessageOccurrenceSpecification} to check for, not <code>null</code>
     * @return the {@link ActionExecutionSpecification} if the
     *         {@link MessageOccurrenceSpecification} is a start or finish point, <code>null</code>
     *         else
     */
    public static ActionExecutionSpecification isExecutionStartOrEnd(final Lifeline l,
            final MessageOccurrenceSpecification mos) {
        final EList<InteractionFragment> lineFragments = l.getCoveredBys();
        for (final InteractionFragment f : lineFragments) {
            if (f.eClass() == UMLPackage.eINSTANCE.getActionExecutionSpecification()) {
                if (((ActionExecutionSpecification) f).getStart() == mos
                        || ((ActionExecutionSpecification) f).getFinish() == mos) {
                    return (ActionExecutionSpecification) f;
                }
            }
        }
        return null;
    }

    /**
     * Same as isExecutionStartOrEnd but only for start.
     */
    public static ActionExecutionSpecification isExecutionStart(final Lifeline l,
            final MessageOccurrenceSpecification mos) {
        final EList<InteractionFragment> lineFragments = l.getCoveredBys();
        for (final InteractionFragment f : lineFragments) {
            if (f.eClass().equals(UMLPackage.eINSTANCE.getActionExecutionSpecification())) {
                if (((ActionExecutionSpecification) f).getStart() == mos) {
                    return (ActionExecutionSpecification) f;
                }
            }
        }
        return null;
    }

    /**
     * Same as isExecutionStartOrEnd but only for end.
     */
    public static ActionExecutionSpecification isExecutionEnd(final Lifeline l,
            final MessageOccurrenceSpecification mos) {
        final EList<InteractionFragment> lineFragments = l.getCoveredBys();
        for (final InteractionFragment f : lineFragments) {
            if (f.eClass().equals(UMLPackage.eINSTANCE.getActionExecutionSpecification())) {
                if (((ActionExecutionSpecification) f).getFinish() == mos) {
                    return (ActionExecutionSpecification) f;
                }
            }
        }
        return null;
    }

    /**
     * Counts the amount of {@link ActionExecutionSpecification}s for a given {@link Lifeline}.
     * 
     * @param lifeline
     *            the {@link Lifeline} for which the amound of {@link ActionExecutionSpecification}s
     *            should be calculated, not <code>null</code>
     * @return the amount of {@link ActionExecutionSpecification}s, not <code>null</code>
     */
    public static int getActionExecutionCount(final Lifeline lifeline) {
        return lifeline.getCoveredBys().stream()
                .filter(frag -> frag == UMLPackage.eINSTANCE.getActionExecutionSpecification())
                .collect(Collectors.toList()).size();
    }
}
