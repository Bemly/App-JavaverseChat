package tcpGPT;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server extends Thread {
	
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
    			                InputStream in = socket.getInputStream();
    							// ��TCP�����ж�ȡJSON�ַ���
    							ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    							
    							
								byte[] data = new byte[1024];
    					        int length;
    					        while ((length = in.read(data)) != -1) {
    					            buffer.write(data, 0, length);
    					        }
    							String json = buffer.toString();
    					        buffer.close();
    					        // ��JSON�ַ��������л�Ϊ����
    					        ObjectMapper mapper = new ObjectMapper();
    					        Package pack = mapper.readValue(json, Package.class);
    					        
    					        OutputStream out = socket.getOutputStream();
    					        // 1 ���� 2������� 3����ɹ� 4����ʧ��
    					        if (pack.packageType == 1) POST(pack.msg, out);
    					        if (pack.packageType == 2) GET(pack.usr, out);
								out.flush();
    							
    							// �ر�����
								in.close();
								out.close();
						        socket.close();
    							// ά�ֽ�һ���߳��ڼ���״̬
    							synchronized (Server.this) {
    			                    Server.this.notify();
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
	
	public void GET(User usr, OutputStream out) throws Exception {
		MySQL sql = new MySQL();
		// ��ѯȫ������
		ResultSet rs = sql.query();
		List<Msg> msgList = new ArrayList<>();
		while (rs.next()) {
			int count = rs.getInt("count");
            int uid = rs.getInt("UID");
            Timestamp time = rs.getTimestamp("time");
            String content = rs.getString("content");
            String whoSeeDB = rs.getString("UIDs");
            if (whoSeeDB != null && whoSeeDB != "") {
            	String[] whoSeeSA = whoSeeDB.split(",");
            	String[] newWhoSeeSA = new String[whoSeeSA.length];
            	// �������������Ƿ����Լ�
                for (int i = 0, n = 0; i < whoSeeSA.length; i++) {
                	// ���Լ�������Ϣ ͬʱɾ�������б�
					if (whoSeeSA[i] != null && !whoSeeSA[i].equals("null")) 
						if (usr.uid == Integer.parseInt(whoSeeSA[i])) {
						MySQL subSql = new MySQL();
						// ������Ϣ�����б�
		                msgList.add(new Msg(new User(uid, subSql.getName(uid)), time, content));
		                subSql.close();
		             // ֻ���������Լ���ֵ
					} else newWhoSeeSA[n++] = whoSeeSA[i];
				}
                // �鿴�Լ��Ƿ���������
                if (newWhoSeeSA[newWhoSeeSA.length - 1] == null) {
                	// ���ھ͸��±��е��б�
                	MySQL subSql = new MySQL();
                	subSql.update(count, Arrays.toString(newWhoSeeSA)
                			.replaceAll("\\bnull\\b,?|,\\bnull\\b", "")
                			.replaceAll("[\\[\\]\\s]", ""));
                	subSql.close();
                }
            }
        }
		// ���͸��ͻ��� msgList msgJson
		PrintWriter pw = new PrintWriter(out, false);
		pw.println(msgList.size());
		for (Msg msg : msgList) {
			String json = new ObjectMapper().writeValueAsString(msg);
			pw.println(json);
		}
		pw.flush();
		pw.close();
	}
	
	public void POST(Msg msg, OutputStream out) throws Exception {
		System.out.println("INSERT=>"+msg.content);
		MySQL sql = new MySQL();
		sql.add(msg);
		sql.close();
		System.out.println("OK=>Client");
		// ����JSON����
        ObjectMapper mapper = new ObjectMapper();

        // ���������л�ΪJSON�ַ���
        // 1 ���� 2������� 3����ɹ� 4����ʧ��
        String json = mapper.writeValueAsString(new Package(3));
        out.write(json.getBytes());
	}
	
    public static void main(String[] args) throws Exception {
    	new Server().start();
    }
}