package tcpGPT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Menu;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTabbedPane;

public class ClientDemoUI extends JFrame implements Runnable {
	
	private JLabel chatArea;
    private JTextArea inputArea;
    private JButton sendButton;
    private User usr;
    private JMenuBar menuBar;
    private JMenu usrCenter;
    private JMenuItem setting;
    private JMenuItem logOut;
    private JCheckBoxMenuItem isOnline;
    private JMenu addRoom;
    private JMenuItem addFriend;
    private JMenuItem addGroup;
    private JMenuItem P2Pchat;
    private JMenu roomMsg;
    private JMenu chatPic;
    private JMenu chatFile;
    private JTabbedPane roomMenu;
    private JPanel room1;
    private JPanel toolPane;
    private JButton upPic;
    private JButton upFile;
    private JButton hisload;

    public ClientDemoUI(int uid, String name) {
    	usr = new User(uid, name);
    	
    	// ���ô��ڱ���
        setTitle("Chat GUI");
        setVisible(true);

        // ���������¼����
        chatArea = new JLabel();
        chatArea.setHorizontalAlignment(JLabel.LEFT);
        chatArea.setVerticalAlignment(JLabel.TOP);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setMinimumSize(new Dimension(200, 100));

        // �������������ͷ��Ͱ�ť
        inputArea = new JTextArea();
        JScrollPane scrollEdit = new JScrollPane(inputArea);
        inputArea.setPreferredSize(new Dimension(400, 100));
        sendButton = new JButton("����");
        sendButton.setBackground(new Color(0, 150, 136)); // ���ð�ť����ɫ
        sendButton.setForeground(Color.WHITE); // ���ð�ťǰ��ɫ
        sendButton.setFocusPainted(false); // ȥ����ť�Ľ����
        sendButton.setPreferredSize(new Dimension(120, 20));
        scrollPane.setMaximumSize(new Dimension(120, 80));
        sendButton.addActionListener(e -> {
        	try {
        		if (inputArea.getText().equals("")) JOptionPane.showMessageDialog(null, "���ɷ��Ϳհ�����");
				else sender(new Msg(usr, new Timestamp(new Date().getTime()), inputArea.getText()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });

        // ��������������
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        roomMenu = new JTabbedPane(JTabbedPane.LEFT);
        getContentPane().add(roomMenu, BorderLayout.NORTH);
        
        room1 = new JPanel();
        roomMenu.addTab("\u804A\u5929\u5BA41", null, room1, null);
        room1.setLayout(new BorderLayout(0, 0));
        room1.add(scrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(400, 125));
        inputPanel.add(scrollEdit, BorderLayout.CENTER);
        
        toolPane = new JPanel();
        inputPanel.add(toolPane, BorderLayout.NORTH);
        
        upPic = new JButton("\u4E0A\u4F20\u56FE\u7247");
        toolPane.add(upPic);
        
        upFile = new JButton("\u4E0A\u4F20\u6587\u4EF6");
        toolPane.add(upFile);
        
        hisload = new JButton("\u804A\u5929\u5386\u53F2\u5C55\u5F00");
        toolPane.add(hisload);
        inputPanel.add(sendButton, BorderLayout.EAST);
        room1.add(inputPanel, BorderLayout.SOUTH);
        

        // ���ô��ڴ�С�͹رղ���
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        usrCenter = new JMenu("User");
        menuBar.add(usrCenter);
        
        isOnline = new JCheckBoxMenuItem("\u663E\u793A\u5728\u7EBF");
        usrCenter.add(isOnline);
        
        addRoom = new JMenu("\u6DFB\u52A0\u597D\u53CB/\u7FA4\u7EC4");
        usrCenter.add(addRoom);
        
        addFriend = new JMenuItem("\u641C\u7D22\u597D\u53CB");
        addRoom.add(addFriend);
        
        addGroup = new JMenuItem("\u641C\u7D22\u7FA4\u7EC4");
        addRoom.add(addGroup);
        
        P2Pchat = new JMenuItem("\u9762\u5BF9\u9762\u79C1\u5BC6\u804A\u5929");
        addRoom.add(P2Pchat);
        
        setting = new JMenuItem("\u8BBE\u7F6E");
        usrCenter.add(setting);
        
        logOut = new JMenuItem("\u9000\u51FA\u767B\u5F55/\u767B\u51FA");
        usrCenter.add(logOut);
        
        roomMsg = new JMenu("\u804A\u5929\u4FE1\u606F");
        menuBar.add(roomMsg);
        
        chatPic = new JMenu("\u804A\u5929\u5BA4\u56FE\u7247");
        menuBar.add(chatPic);
        
        chatFile = new JMenu("\u804A\u5929\u5BA4\u6587\u4EF6");
        menuBar.add(chatFile);
    }

    public static void main(String[] args) throws Exception {
    	
    	try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	new Thread(new ClientDemoUI(1, "z3")).start();
    	new Thread(new ClientDemoUI(2, "l4")).start();
    	new Thread(new ClientDemoUI(3, "w5")).start();
        
    }
    
    @Override
    public void run() {
    	
        
        // ������Ļ����
        new Thread(){
        	@Override
        	public void run() {
        		while (true) {
        			try {
        				List<Msg> msgList = receive();
        				// �������Ϣ�͸���
        				if (msgList.size() > 0) for (Msg msg : msgList) {
        					Calendar time = Calendar.getInstance();
        					time.setTimeInMillis(msg.time.getTime());
        					String f = String.format("%d/%d/%d %d:%d:%d", time.get(Calendar.YEAR), 
									time.get(Calendar.MONTH), time.get(Calendar.DATE), 
									time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MILLISECOND));
        					chatArea.setText(String.format(
        						"<html>%s<h4 color='blue'>[%s %s]</h4><h3>%s</h3></html>",
        						chatArea.getText().replaceAll("^<[^>]+>|<[^>]+>$", ""), msg.usr.name,
        						f, msg.content.replaceAll("\n", "<br>")));
						}
    					sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        	}
        }.start();
    }
    
    // ����
    public boolean sender(Msg msg) throws Exception {
    	// ����TCP����
        Socket socket = new Socket("localhost", 23333);

        // ����JSON����
        ObjectMapper mapper = new ObjectMapper();

        // ���������л�ΪJSON�ַ���
        String json = mapper.writeValueAsString(new Package(1, msg));

        // ����JSON�ַ���
        OutputStream out = socket.getOutputStream();
        System.out.println("POST=>"+json);
        out.write(json.getBytes());
        out.flush();
        socket.shutdownOutput();
        
        // ��TCP�����ж�ȡJSON�ַ���
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream in = socket.getInputStream();
        byte[] data = new byte[1024];
        int length;
        while ((length = in.read(data)) != -1) {
            buffer.write(data, 0, length);
        }
        json = buffer.toString();
        // �ر�����
        socket.shutdownInput();
        in.close();
        out.close();
        socket.close();
        buffer.close();
        // ��JSON�ַ��������л�Ϊ����
        Package pack = new ObjectMapper().readValue(json, Package.class);
        // 1 ���� 2������� 3����ɹ� 4����ʧ��
        if (pack.packageType == 3) return true;
        else if (pack.err != null && pack.packageType == 4) {
			JOptionPane.showMessageDialog(null, pack.err.getMessage());
			pack.err.printStackTrace();
			return false;
		} else {
			JOptionPane.showMessageDialog(null, "δ֪����");
			return false;
		}
    }
    
    // ����
    public List<Msg> receive() throws Exception {
    	// ����TCP����
        Socket socket = new Socket("localhost", 23333);

        // ����JSON����
        ObjectMapper mapper = new ObjectMapper();

        // ���������л�ΪJSON�ַ���
        String json = mapper.writeValueAsString(new Package(2, usr));

        // ����JSON�ַ���
        OutputStream out = socket.getOutputStream();
        System.out.println("GET=>"+json);
        out.write(json.getBytes());
        out.flush();
        socket.shutdownOutput();
        
        // ��TCP�����ж�ȡJSON�ַ���
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		List<Msg> msgList = new ArrayList<>();
        String size = in.readLine(), line;
        if (size != null && !size.equals("0")) while ((line = in.readLine()) != null) {
        	// ��JSON�ַ��������л�Ϊ����
            Msg msg = new ObjectMapper().readValue(line, Msg.class);
            msgList.add(msg);
		}
        // �ر�����
        socket.shutdownInput();
        in.close();
        out.close();
        socket.close();
        buffer.close();
       
        return msgList;
    }
    
}