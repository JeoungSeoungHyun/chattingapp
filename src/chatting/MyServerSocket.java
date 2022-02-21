package chatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

// jwp = �����������
// 1. ���� �޼����� username���� üŷ
// 2.������ :
// 3.ALL:�޽���
// 4.CHAT:���̵�:�޽���

public class MyServerSocket {

	// ������(����ޱ�) - ���ν�����
	ServerSocket serverSocket;

	// ������ ���õ� ���� �����ϱ� ���� ����Ʈ
	List<�����㽺����> ������Ʈ;

	// �޽��� �޾Ƽ� ������ (Ŭ���̾�Ʈ ������ �����)

	public MyServerSocket() {
		try {
			// 2000�� ��Ʈ�� �������� ����
			serverSocket = new ServerSocket(2001);

			// ����Ʈ�� �������� �Ұ����� ����ȭ�� ó���� ArrayList�� Vector���
			������Ʈ = new Vector<>();

			while (true) {
				// ��Ʈ ����� ���� ����
				Socket socket = serverSocket.accept();
				System.out.println("���� �����");

				// ���� ���� �� ����Ʈ�� ���
				�����㽺���� t = new �����㽺����(socket);
				������Ʈ.add(t);
				System.out.println("���� ���� �� : " + ������Ʈ.size());

				// ���ο� ������ ���ѽ�����
				new Thread(t).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class �����㽺���� implements Runnable {

		// �͸�Ŭ������ ����� ������ �Ұ����ؼ� ����Ŭ������ ����->������ ���� ����
		Socket socket;
		BufferedReader reader;
		BufferedWriter writer;

		// ���ǿ��� �� while���� ���� ����
		boolean isLogin = true;

		// �������� ���� ID ����
		String userName;

		public �����㽺����(Socket socket) {
			this.socket = socket;

			// ���� �����
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ��üä�� �޼��� �������� (All:�޽���)
		public void chatPublic(String msg) {
			try {
				for (�����㽺���� t : ������Ʈ) {
					if (t != this) {
						t.writer.write(userName + ":" + msg + "\n");
						t.writer.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// �ӼӸ� �޼��� �������� (CHAT:ID:�޽���)
		public void chatPrivate(String receiver, String msg) {
			try {
				for (�����㽺���� t : ������Ʈ) {
					if (t.userName.equals(receiver)) {
						t.writer.write("[�ӼӸ�]" + userName + ":" + msg + "\n");
						t.writer.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void sendUserList() {
			try {
				String userList = "USER";
				for (�����㽺���� t : ������Ʈ) {
					userList = userList + ":" + t.userName;
				}
				this.writer.write(userList + "\n");
				this.writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ����������� �˻��
		// ALL:�ȳ�
		// CHAT:ID:�ȳ�
		public void jwp(String inputData) {
			// 1. �������� �и�
			String[] token = inputData.split(":");
			String protocol = token[0];
			if (protocol.equals("ALL")) {
				String msg = token[1];
				chatPublic(msg);

			} else if (protocol.equals("CHAT")) {
				String receiver = token[1];
				String msg = token[2];
				chatPrivate(receiver, msg);

			} else if (protocol.equals("USER")) {
				sendUserList();
			} else { // �������� ��� ����
				System.out.println("�������� ����");
			}
		}

		@Override
		public void run() {
			// ���� �޽����� userName�̴�.
			try {
				userName = reader.readLine();
				for (�����㽺���� t : ������Ʈ) {
					t.writer.write(userName + "���� �����Ͽ����ϴ�.\n");
					t.writer.flush();
				}
			} catch (Exception e1) {
				// e1.printStackTrace();
				isLogin = false; // ID������ ���� ���� �ȵ�
				System.out.println("ID�� ���� ���߽��ϴ�.");
			}

			while (isLogin) {
				try {
					String inputData = reader.readLine();
					// System.out.println("from Ŭ���̾�Ʈ : " + inputData);

					// �������� �˻��
					if (inputData != null) {
						System.out.println("������" + inputData);
						jwp(inputData);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					System.out.println("�������� : " + e.getMessage());
					e.printStackTrace();
					try {
						System.out.println("Ŭ���̾�Ʈ ���� ���� ��");
						isLogin = false; // while�� ����
						������Ʈ.remove(this); // ����Ʈ�� ����ִ� ������ ��������Ѵ�.
						writer.close(); // GarbageCollection�� �Ͼ����
						reader.close();// �ð��� �ɸ��µ� ���(IO�� ����� ����)�� ���ϰ�
						socket.close();// GarbageCollection�� ���ϰ� �� ũ�� ������ Garbage Collection�� ���� ���ش�.
					} catch (Exception f) {
						// f.printStackTrace();
						System.out.println("���� ���� ���� : " + f.getMessage());
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		new MyServerSocket();
	}
}