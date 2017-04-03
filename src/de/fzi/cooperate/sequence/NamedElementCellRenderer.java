package de.fzi.cooperate.sequence;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.eclipse.uml2.uml.NamedElement;

/**
 * Custom CellRenderer for {@link JComboBox}es that work with {@link NamedElement}s
 */
public class NamedElementCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
            final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        this.setText(((NamedElement) value).getName());
        return this;
    }
}
