package de.fzi.cooperate.sequence.message;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.UMLPackage;
import org.jdal.swing.SpringUtilities;

import de.fzi.cooperate.sequence.ModelUtilities;
import de.fzi.cooperate.sequence.NamedElementCellRenderer;

/**
 * Provides a dialog for moving a {@link Message} to an {@link InteractionOperand}
 */
public class MessageMoveDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_MOVE_DIALOG_TITLE = "Move To Interaction Operand";
    private static final String MESSAGE_MOVE_DIALOG_COMBFRAG = "Combined Fragment";
    private static final String MESSAGE_MOVE_DIALOG_INTEROP = "Interaction Operand";
    private static final String MESSAGE_MOVE_DIALOG_CONSTRAINT = "Constraint";
    private static final String MESSAGE_MOVE_DIALOG_ACCEPT = "Ok";
    private static final String MESSAGE_MOVE_DIALOG_CANCEL = "Cancel";

    // Variables declaration
    private JLabel lblComFrag;
    private JLabel lblInterOp;
    private JLabel lblConstraint;
    private JLabel lblConstraintValue;

    protected JComboBox<CombinedFragment> cmbCombFrag;
    protected JComboBox<InteractionOperand> cmbInterOp;

    protected JButton btnAccept;
    private JButton btnCancel;
    private JPanel contentPane;

    protected final Interaction interaction;
    // End of variables declaration

    /**
     * Constructor for a dialog for creating a new {@link MESSAGE_MOVEerand}.
     * 
     * @param cf
     *            the {@link CombinedFragment} holding the {@link MESSAGE_MOVEerand} to be edited,
     *            not <code>null</code>
     * @param operation
     *            the {@link DialogOps} enum representing wether if a {@link MESSAGE_MOVEerand} will
     *            be created or edited, not <code>null</code>
     */
    public MessageMoveDialog(final Interaction interaction, final Message msg) {
        this.interaction = interaction;
        this.setWindowProperties();
        this.initialize(msg);
        this.setVisible(true);
    }

    private void initialize(final Message msg) {
        final CombinedFragment[] combinedFrags = this.interaction.getFragments().stream()
                .filter(frag -> frag.eClass().equals(UMLPackage.eINSTANCE.getCombinedFragment()))
                .toArray(size -> new CombinedFragment[size]);
        final InteractionOperand[] interactionOps = combinedFrags[0].getOperands()
                .toArray(new InteractionOperand[combinedFrags[0].getOperands().size()]);

        // Initialize the swing components
        this.initializeComponents(combinedFrags, interactionOps);

        // Set the labels for the components
        this.setLabels();

        // Add the components to the content pane
        this.addComponents();

        // Layout
        this.setLayout();

        // Action listeners for the buttons
        this.setActionListeners(msg);

        this.setRenderers();
    }

    /**
     * Sets the {@link JFrame}'s properties.
     * 
     * @param title
     *            the title for the {@link JFrame}, not <code>null</code>
     */
    private void setWindowProperties() {
        this.setTitle(MESSAGE_MOVE_DIALOG_TITLE);
        this.setSize(new Dimension(400, 150));
        this.setLocation(ModelUtilities.getScreenCenterPosition(this));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }

    /**
     * Initializes the swing components for the dialog.
     */
    private void initializeComponents(final CombinedFragment[] combinedFrags,
            final InteractionOperand[] interactionOps) {
        this.lblComFrag = new JLabel(MESSAGE_MOVE_DIALOG_COMBFRAG);
        this.lblInterOp = new JLabel(MESSAGE_MOVE_DIALOG_INTEROP);
        this.lblConstraint = new JLabel(MESSAGE_MOVE_DIALOG_CONSTRAINT);
        this.lblConstraintValue = new JLabel(
                ((LiteralString) interactionOps[0].getGuard().getSpecification()).getValue());
        this.cmbCombFrag = new JComboBox<CombinedFragment>(combinedFrags);
        this.cmbInterOp = new JComboBox<InteractionOperand>(interactionOps);
        this.btnAccept = new JButton(MESSAGE_MOVE_DIALOG_ACCEPT);
        this.btnCancel = new JButton(MESSAGE_MOVE_DIALOG_CANCEL);
        this.contentPane = (JPanel) this.getContentPane();
    }

    /**
     * Adds the swing components to the {@link JFrame}'s content pane.
     */
    private void addComponents() {
        this.contentPane.add(this.lblComFrag);
        this.contentPane.add(this.cmbCombFrag);
        this.contentPane.add(this.lblInterOp);
        this.contentPane.add(this.cmbInterOp);
        this.contentPane.add(this.lblConstraint);
        this.contentPane.add(this.lblConstraintValue);
        this.contentPane.add(this.btnAccept);
        this.contentPane.add(this.btnCancel);
    }

    /**
     * Sets the labels for the components.
     */
    private void setLabels() {
        this.lblComFrag.setLabelFor(this.cmbCombFrag);
        this.lblInterOp.setLabelFor(this.cmbInterOp);
        this.lblConstraint.setLabelFor(this.lblConstraintValue);
    }

    /**
     * Initializes and sets the layout.
     */
    private void setLayout() {
        final SpringLayout layout = new SpringLayout();
        this.contentPane.setLayout(layout);
        SpringUtilities.makeCompactGrid(this.contentPane, 4, 2, // rows, cols
                6, 3, // initX, initY
                6, 3); // xPad, yPad
    }

    /**
     * Sets the action listeners for the buttons.
     */
    private void setActionListeners(final Message msg) {
        this.cmbCombFrag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MessageMoveDialog.this.updateInteractionOperands(
                        (CombinedFragment) MessageMoveDialog.this.cmbCombFrag.getSelectedItem());
            }
        });
        this.cmbInterOp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MessageMoveDialog.this.updateConstraintValue(
                        (InteractionOperand) MessageMoveDialog.this.cmbInterOp.getSelectedItem());
            }
        });
        this.btnAccept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final TransactionalEditingDomain domain = TransactionUtil
                        .getEditingDomain(MessageMoveDialog.this.interaction);
                domain.getCommandStack().execute(new RecordingCommand(domain) {

                    @Override
                    protected void doExecute() {
                        MessageOperations.moveMessageToInterOp(msg,
                                (InteractionOperand) MessageMoveDialog.this.cmbInterOp.getSelectedItem());
                    }
                });
                SwingUtilities.getWindowAncestor((JButton) e.getSource()).dispose();
            }
        });
        this.btnCancel.addActionListener(ModelUtilities.getCancelActionListener());
    }

    private void setRenderers() {
        this.cmbCombFrag.setRenderer(new NamedElementCellRenderer());
        this.cmbInterOp.setRenderer(new NamedElementCellRenderer());
    }

    private void updateInteractionOperands(final CombinedFragment cf) {
        final InteractionOperand[] interOps = cf.getOperands().toArray(new InteractionOperand[cf.getOperands().size()]);
        final DefaultComboBoxModel<InteractionOperand> model = new DefaultComboBoxModel<InteractionOperand>(interOps);
        this.cmbInterOp.setModel(model);
        this.updateConstraintValue(interOps[0]);
    }

    private void updateConstraintValue(final InteractionOperand iop) {
        this.lblConstraintValue.setText(((LiteralString) iop.getGuard().getSpecification()).getValue());
    }

}
