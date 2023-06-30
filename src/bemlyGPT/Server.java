package bemlyGPT;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server extends WebSocketServer {
	
	// ��Msg���ͻ���
	public String GET(User usr) throws Exception {
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
		StringJoiner json = new StringJoiner("\n");
		for (Msg msg : msgList) json.add(new ObjectMapper().writeValueAsString(msg));
		return json.toString();
	}
	
	// ���տͻ��˵���Ϣ
	public String POST(Msg msg) throws Exception {
		System.out.println("INSERT=>"+msg.content);
		new MySQL().add(msg).close();
        // ���������л�ΪJSON�ַ���
        // 1 ���� 2������� 3����ɹ� 4����ʧ��
        return new ObjectMapper().writeValueAsString(new Package(3));
	}
	
    public static void main(String[] args) throws Exception {
        Server s = new Server(23333);
        s.start();
        
    }
    
    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        System.out.println("�������������");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    	System.out.printf("[%s]�ͻ������� ��ַ=%s%n",Thread.currentThread().getName(), conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.printf("[%s]�ͻ��˵��� ��ַ=%s%n",Thread.currentThread().getName(), conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    	try {
    		System.out.printf("[%s]�յ���=%s%n",Thread.currentThread().getName(), message);
    		// �����л�JSONΪ���� ���
            Package pack = new ObjectMapper().readValue(message, Package.class);
            // 1 ���� 2������� 3����ɹ� 4����ʧ��
            if (pack.packageType == 1) conn.send(POST(pack.msg));
            if (pack.packageType == 2) conn.send(GET(pack.usr));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }
}