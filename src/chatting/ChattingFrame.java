package chatting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author 정성현 목적 : 프레임 생성
 *
 */

enum Constant {
	North, Center, South
}

public class ChattingFrame extends JFrame {

	private JPanel top;
	private JPanel center;
	private JPanel bottom;
	private JTextField textBox;
	private JButton send;
	private String msg;

	public ChattingFrame() {
		initObject();
		initSetting();
		addObject();
		initListener();
		setVisible(true);
	}

	private void initObject() {

		top = new JPanel();
		center = new JPanel();
		bottom = new JPanel();
		textBox = new JTextField(30);
		send = new JButton("전송");

	}

	private void initSetting() {
		setTitle("채팅프로그램");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		top.setBackground(Color.yellow);
		top.setPreferredSize(new Dimension(400, 30));

		center.setBackground(Color.pink);

		bottom.setBackground(Color.orange);
		bottom.setLayout(new FlowLayout());
		bottom.setPreferredSize(new Dimension(400, 40));

	}

	private void addObject() {
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		bottom.add(textBox);
		bottom.add(send);
	}

	private void initListener() {
		textBox.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textBox.getText();
					textBox.setText("");
				}
			}
		});

	}

	public static void main(String[] args) {
		new ChattingFrame();
	}
}
