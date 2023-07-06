package tcpNoJson.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JOptionPane;

public abstract class TCPSender extends Thread {
	
	protected int select;
	
	
    @Override
    public void run() {
    	try {
            // ����Socket�������ӷ�����
            Socket socket = new Socket("localhost", 23333);

            // ��ȡ���������������
            // GET/POST/LOGIN
			PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sender(out, in);

            // �ر�����
            socket.close();
        } catch (ConnectException e) {
        	JOptionPane.showMessageDialog(null, "����������ʧ��,���Եȴ�һ��ʱ������:"+e);
            e.printStackTrace();
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(null, e);
        	e.printStackTrace();
		}
    }

	protected abstract void sender(PrintWriter out, BufferedReader in) throws Exception;
}