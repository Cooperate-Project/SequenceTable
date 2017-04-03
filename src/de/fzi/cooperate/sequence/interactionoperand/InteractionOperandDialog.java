package de.fzi.cooperate.sequence.interactionoperand;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperand;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.ModelUtilities;

/**
 * Class for providing a dialog window for creating and editing {@link InteractionOperand}s.
 */
public class InteractionOperandDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String INTERACTIONOP_DIALOG_TITLE = "Add New InteractionOperand";
    private static final String INTERACTIONOP_DIALOG_CONSTRAINT = "Constraint";
    private static final String INTERACTIONOP_DIALOG_ACCEPT = "Ok";
    private static final String INTERACTIONOP_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblConstraint;

    protected JTextField txtConstraint;
    protected JButton btnAccept;
    private JButton btnCancel;
    private JPanel contentPane;

    protected final Interaction interaction;
    protected final CombinedFragment comFragment;
    // End of variables declaration

    /**
     * Constructor for a dialog for creating a new {@link InteractionOperand}.
     *
     * @param cf
     *            the {@link CombinedFragment} holding the {@link InteractionOperand} to be edited,
     *            not <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether if a {@link InteractionOperand}
     *            will be created or edited, not <code>null</code>
     */
    public InteractionOperandDialog(final CombinedFragment cf) {
        this.interaction = cf.getEnclosingInteraction();
        this.comFragment = cf;
        this.setWindowProperties();
        this.initialize();
        this.setVisible(true);
    }

    private void initialize() {

        // Initialize the swing components
        this.initializeComponents();

        // Set the labels for the components
        this.setLabels();

        // Add the components to the content pane
        this.addComponents();

        // Layout
        this.setLayout();

        // Action listeners for the buttons
        this.setActionListeners();
    }

    /**
     * Sets the {@link JFrame}'s properties.
     *
     * @param title
     *            the title for the {@link JFrame}, not <code>null</code>
     */
    private void setWindowProperties() {
        this.setTitle(INTERACTIONOP_DIALOG_TITLE);
        this.setSize(new Dimension(400, 90));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Initializes the swing components for the dialog.
     */
    private void initializeComponents() {
        this.lblConstraint = new JLabel(INTERACTIONOP_DIALOG_CONSTRAINT);
        this.txtConstraint = new JTextField();
        this.btnAccept = new JButton(INTERACTIONOP_DIALOG_ACCEPT);
        this.btnCancel = new JButton(INTERACTIONOP_DIALOG_CANCEL);
        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Adds the swing components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblConstraint);
        this.contentPane.add(this.txtConstraint);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Sets the labels for the components.
     */
    private void setLabels() {
        this.lblConstraint.setLabelFor(this.txtConstraint);
    }

    /**
     * Initializes and sets the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 2, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Sets the action listeners for the buttons.
     */
    private void setActionListeners() {
        this.btnAccept.addActionListener(new InteractionOperandCreateListener(this));
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
    }
}