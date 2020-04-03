package ChatSystem.Frontend;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AEEvent implements ActionListener {

	private JTextField tf;
	private JTextArea area;
	private JFrame frame;

	public void initUI() {
		frame = new JFrame();
		frame.setLayout(new GridBagLayout());
		area = new JTextArea(30, 80);
		area.setEditable(false);
		area.setFocusable(false);
		tf = new JTextField();
		JButton b = new JButton("click me");
		b.addActionListener(this);

		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		frame.add(scrollPane, gbc);
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		frame.add(tf, gbc);
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		frame.add(b, gbc);
		frame.getRootPane().setDefaultButton(b);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
		tf.requestFocusInWindow();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		area.append(tf.getText() + "\n");
		tf.setText("");
	}

}