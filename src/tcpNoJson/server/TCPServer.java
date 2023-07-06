package tcpNoJson.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

import tcpNoJson.Msg;
import tcpNoJson.User;

public class TCPServer extends Thread {
    
	@Override
	public void run() {
		try {
			setName("�����");
            // ����ServerSocket���󣬼���ָ���˿�
            ServerSocket serverSocket = new ServerSocket(23333);
            System.out.println("[" + getName() + "]�����ɹ�,ִ�����̲߳���");
            
            while (true) {
            	synchronized (this) {
            		new Thread() {
    					@Override
    					public void run() {
    						try {
    							setName("�����-"+getId());
    							System.out.println("[" + getName()+"]��ʼ�ȴ��ͻ��˻�Ӧ");
    							
    							// �ȴ��ͻ�������
    			                Socket socket = serverSocket.accept();
    			                System.out.println("[" + getName()+"]���ӳɹ�,�ͻ���:" + socket.getInetAddress().getHostAddress());
    							
    							// ����ͻ�������
    			                // ...
    			                InputStream is = socket.getInputStream();
    							BufferedReader in = new BufferedReader(new InputStreamReader(is));
    							PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
    							System.err.println(is.available());
    							String connectType = in.readLine();
    							System.out.println("[" + getName()+"]��������:"+connectType);
    							try {
    								if (connectType != null && connectType.equals("GET")) GET(in, out);
        							if (connectType != null && connectType.equals("POST")) POST(in, out);
        							if (connectType != null && connectType.equals("LOGIN")) LOGIN(in, out);
								} catch (Exception e) {
									// ά����һ���߳�����ִ��
									e.printStackTrace();
								} finally {
									out.flush();	// =>
								}
    							
    							// �ر�����
    							socket.close();
    							// ά�ֽ�һ���߳��ڼ���״̬
    							synchronized (TCPServer.this) {
    			                    TCPServer.this.notify();
    			                }
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
    					}
    				}.start();
    				wait();
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void GET(BufferedReader in, PrintWriter out) throws Exception {
		// GET_TYPE: ��ѯ��ɫ
		// Token UID GET_TYPE
		
		// ��֤�׶�
		String token = in.readLine();
		String UID = in.readLine();
		String getType = in.readLine();
		if (token == null || UID == null || getType == null) Sender(out, "["+getName()+"]GET��ʽ����");
		int uid = Integer.parseInt(UID);
		if (Token.check(token, uid, new Timestamp(new Date().getTime()))) Sender(out, "["+getName()+"]Token����");
		
		UserFormOper ufo = new UserFormOper();
		// 1 : User => roomIDs ��ѯ�û���Ӧ����
		switch (Integer.parseInt(getType)) {
		case 1:
//			ufo.
			break;
		case 2:
			
			break;
		default:
			break;
		}
	}
	
	public void POST(BufferedReader in, PrintWriter out) throws Exception {
		// POST_TYPE: �������� ������Ϣ
		// Token UID POST_TYPE
		
		// ��֤�׶�
		String token = in.readLine();
		String UID = in.readLine();
		String postType = in.readLine();
		if (token == null || UID == null || postType == null) Sender(out, "["+getName()+"]POST��ʽ����");
		int uid = Integer.parseInt(UID);
		if (Token.check(token, uid, new Timestamp(new Date().getTime()))) Sender(out, "["+getName()+"]Token����");
		
		switch (Integer.parseInt(postType)) {
		case 1:
			// 1:��������
			// ��ʼ������ ������ֵ �ͻ���֪��
			RoomFormOper rfo = new RoomFormOper();
			if(rfo.create()==0) throw new Exception("["+getName()+"]��������-��ʼ������ʧ��");
			else Sender(out, uid);
			break;
		case 2:
			// 2:������Ϣ
			MsgOper mo = new MsgOper();
			
			mo.add(new Msg());
			break;
		default:
			break;
		}
	}
	
	public void LOGIN(BufferedReader in, PrintWriter out) throws Exception {
		// neckName pwd sign
		
		// ��֤�׶�
		String neckName = in.readLine();
		String pwd = in.readLine();
		String sign = in.readLine();
		if (neckName == null || pwd == null || sign == null) Sender(out, "["+getName()+"]LOGIN��ʽ����");
		int signType = Integer.parseInt(sign);
		
		UserFormOper ufo = new UserFormOper();
		User usr = ufo.search(neckName);
		ufo.close();
		System.out.println("[" + getName()+"]LOGIN��������:"+signType);
		switch (signType) {
		case 1:
			// ��¼
			if (usr == null) Sender(out, "["+getName()+"]�˻�������,���Գ���ע��");
			else if (usr.getPassword().equals(pwd)) Sender(out, usr.getUID());
			else Sender(out, "["+getName()+"]��¼�������");
			break;
		case 2:
			// ע��
			if (usr != null) Sender(out, "["+getName()+"]�˻��Ѵ���,���Գ��Ե�¼");
			else {
				ufo.connect();
				ufo.add(new User(ufo.getMax("UID")+1, neckName, pwd));
			}
			Sender(out, usr.getUID());
			break;
		default:
			// �һ�
			// �޸�����
			break;
		}
	}
	
	public void Sender(PrintWriter out, String e) throws Exception {
		// ����ʧ�ܻ�ִ
		out.println("404");
		out.println(e);
		out.flush();
		throw new Exception(e);
	}
	
	public void Sender(PrintWriter out, int uid) throws Exception {
		// ���ɹ� ��ִ
		// 200:success UID Token 404:error ErrorContent
		out.println("200");
		out.println(uid);
		out.println(Token.update(uid, new Timestamp(new Date().getTime())));
		out.flush();
	}
}
