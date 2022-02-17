package chatting;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class test extends JFrame {

	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JPanel panel3 = new JPanel();
	JLabel lable = new JLabel();
	JPanel panel = new JPanel();

	public test() {

		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel1.setBackground(Color.pink);
		panel2.setBackground(Color.yellow);
		panel3.setBackground(Color.pink);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		add(panel2.add(new JLabel("╬хЁГ")));
		add(panel2.add(new JLabel("╢ого©ю")));
//		lable.setHorizontalAlignment(JLabel.);

		setVisible(true);
	}

	public static void main(String[] args) {
		new test();

	}

}
