package de.fzi.cooperate.sequence.stateinvariant;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.StateInvariant;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.ModelUtilities;
import de.fzi.cooperate.sequence.NamedElementCellRenderer;

/**
 * Class providing a dialog window for creating and editing {@link StateInvariant}s.
 */
public class StateInvariantDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String STATEINVARIANT_DIALOG_TITLE = "Create New StateInvariant";
    private static final String STATEINVARIANT_DIALOG_INVARIANTNAME = "StateInvariant Name";
    private static final String STATEINVARIANT_DIALOG_FORLIFELINE = "For Lifeline";
    private static final String STATEINVARIANT_DIALOG_INVARIANTCONSTRAINT = "Constraint";
    private static final String STATEINVARIANT_DIALOG_ACCEPT = "Ok";
    private static final String STATEINVARIANT_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblName;
    private JLabel lblLifeline;
    private JLabel lblConstraint;

    protected JComboBox<Lifeline> cmbLifeline;
    protected JTextField txtName;
    protected JTextField txtConstraint;
    protected JButton btnAccept;
    private JButton btnCancel;
    private JPanel contentPane;

    protected final Interaction interaction;
    // End of variables declaration

    /**
     * Constructor for a dialog for creating a new {@link StateInvariant}.
     *
     * @param inter
     *            the interaction providing the context for the new {@link StateInvariant}, not
     *            <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether if a {@link StateInvariant} will be
     *            created or edited, not <code>null</code>
     */
    public StateInvariantDialog(final Interaction inter) {
        super();
        this.interaction = inter;
        this.setWindowProperties(STATEINVARIANT_DIALOG_TITLE);
        this.initialize();
        this.setVisible(true);
    }

    /**
     * Sets the {@link JFrame}'s properties.
     *
     * @param title
     *            the title for the {@link JFrame}, not <code>null</code>
     */
    private void setWindowProperties(final String title) {
        this.setTitle(title);
        this.setSize(new Dimension(400, 150));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Initializes the elements and data for the dialog frame.
     */
    private void initialize() {
        // Get all lifelines and their names as string array for the combobox model
        final EList<Lifeline> lifelines = this.interaction.getLifelines();

        // Initialize the swing objects for the dialog
        this.initializeComponents(lifelines.toArray(new Lifeline[lifelines.size()]));

        this.setLabels();

        // Add the components to the contentpane
        this.addComponents();

        this.setLayout();
        this.setActionListeners(lifelines);
        this.setCellRenderers();
    }

    /**
     * Initializes the swing components for the dialog.
     *
     * @param strLifelines
     *            the names of all {@link Lifeline}s as string array, null if there are no lifelines
     */
    private void initializeComponents(final Lifeline[] lifelines) {
        this.lblName = new JLabel(STATEINVARIANT_DIALOG_INVARIANTNAME);
        this.lblLifeline = new JLabel(STATEINVARIANT_DIALOG_FORLIFELINE);
        this.lblConstraint = new JLabel(STATEINVARIANT_DIALOG_INVARIANTCONSTRAINT);
        this.txtName = new JTextField(3);
        this.txtConstraint = new JTextField(3);
        this.cmbLifeline = new JComboBox<Lifeline>(lifelines);
        this.btnAccept = new JButton(STATEINVARIANT_DIALOG_ACCEPT);
        this.btnCancel = new JButton(STATEINVARIANT_DIALOG_CANCEL);
        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Sets the labels for the corresponding components.
     */
    private void setLabels() {
        this.lblName.setLabelFor(this.txtName);
        this.lblLifeline.setLabelFor(this.cmbLifeline);
        this.lblConstraint.setLabelFor(this.txtConstraint);
    }

    /**
     * Adds all components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblName);
        this.contentPane.add(this.txtName);
        this.contentPane.add(this.lblLifeline);
        this.contentPane.add(this.cmbLifeline);
        this.contentPane.add(this.lblConstraint);
        this.contentPane.add(this.txtConstraint);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Initialize and set the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 4, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Set the action listeners for the buttons.
     *
     * @param lifelines
     *            the {@link Lifeline}s on which the {@link StateInvariant} can be placed, not
     *            <code>null</code>
     */
    private void setActionListeners(final EList<Lifeline> lifelines) {
        this.btnAccept.addActionListener(new StateInvariantCreateListener(this));
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
    }

    private void setCellRenderers() {
        this.cmbLifeline.setRenderer(new NamedElementCellRenderer());
    }
}