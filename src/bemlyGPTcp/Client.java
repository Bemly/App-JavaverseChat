package tcpGPT;

// ֧�� ��Ϣ ��������
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



// �������
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

// ֧�����ݿ��ʱ�������ת��
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

// ����������������ֽ���
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
// ����TCPͨ��
import java.net.Socket;



// ����GPTBot
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.util.Header;
import com.azure.core.util.HttpClientOptions;
import com.azure.core.util.IterableStream;
// JSON ���л��������л�����
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client extends JFrame implements Runnable {
	static Object syncBotAccess;
    private JTextArea inputArea;
    private JButton sendButton;
    public JLabel chatArea, botArea;
    private JScrollPane scrollPane, botPane;
    private User usr;
    private Bot bot;
    private JButton clearChat;

    public Client(int uid, String name) {
    	usr = new User(uid, name);
    	bot = new Bot();
    	
    	// ���ô��ڱ���
        setTitle("�û���:"+name);
        setVisible(true);
        
        // ���������¼����
        chatArea = new JLabel();
        chatArea.setHorizontalAlignment(JLabel.LEFT);
        chatArea.setVerticalAlignment(JLabel.TOP);
        scrollPane = new JScrollPane(chatArea);
        
        // ���������˻ش�����
        botArea = new JLabel();
        botArea.setHorizontalAlignment(JLabel.LEFT);
        botArea.setVerticalAlignment(JLabel.TOP);
        botPane = new JScrollPane(botArea);

        // �������������ͷ��Ͱ�ť
        inputArea = new JTextArea();
        JScrollPane scrollEdit = new JScrollPane(inputArea);
        inputArea.setPreferredSize(new Dimension(400, 100));
        sendButton = new JButton("����");
        sendButton.addActionListener(e -> {
        	try {
        		sender(new Msg(usr, new Timestamp(new Date().getTime()), inputArea.getText()));
				// ִ��botָ��
				if (inputArea.getText().toLowerCase().startsWith("/bot ")) 
					bot.sendMsg(new Msg(usr, new Timestamp(new Date().getTime()), inputArea.getText().substring(5)));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });

        // ��������������
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        // ��ӷָ��
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.add(scrollPane, JSplitPane.LEFT);
        splitPane.add(botPane, JSplitPane.RIGHT);
        
        clearChat = new JButton("\u6E05\u7A7AGPT\u4E0A\u4E0B\u6587");
        clearChat.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		bot.clearHisMsg();
        	}
        });
        botPane.setColumnHeaderView(clearChat);
        splitPane.setPreferredSize(new Dimension(400, 300));
        getContentPane().add(splitPane, BorderLayout.NORTH);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(400, 100));
        inputPanel.add(scrollEdit, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        contentPane.add(inputPanel, BorderLayout.SOUTH);

        // ���ô��ڴ�С�͹رղ���
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) throws Exception {
    	
    	try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	syncBotAccess = new Object();
    	new Thread(new Client(5, "bemly")).start();
    	new Thread(new Client(9, "����ȡ���Ͳ�ȡ��")).start();
    	new Thread(new Client(10, "�²�����˭")).start();
        
    }
    
    @Override
    public void run() {
    	// ������Ļ����
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
						chatArea.getText().replaceAll("^<[^>]+>|<[^>]+>$", ""),
						msg.usr.name, f, msg.content.replaceAll("[\n\r]", "<br>")));
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
    
    class Bot {
    	
    	private String azureOpenaiKey = "";
    	private String endpoint = "";
    	private String modelId = "";
    	private OpenAIClient client;
    	private List<ChatMessage> chatMsgList;
    	
    	public Bot() {
    		client = new OpenAIClientBuilder()
    			.httpClient(HttpClient.createDefault(new HttpClientOptions()
    			.setHeaders(Collections.singletonList(new Header("Accept-Charset", "utf-8")))))
    			.endpoint(endpoint)
    			.credential(new AzureKeyCredential(azureOpenaiKey))
    			.buildClient();
    		chatMsgList = new ArrayList<>();
    	}
    	
    	
    	public void sendMsg(Msg msg) {
    		new Thread() {
    			@Override
    			public void run() {
    				synchronized (syncBotAccess) {
    					System.out.println();
    					// ���û��͹���Ա����
    					if (usr.name.equals("bemly")) chatMsgList.add(new ChatMessage(ChatRole.SYSTEM).setContent(msg.content));
    					else chatMsgList.add(new ChatMessage(ChatRole.USER).setContent(msg.content));
        	            try {
        	            	Calendar time = Calendar.getInstance();
        					time.setTimeInMillis(msg.time.getTime());
        					String f = String.format("%d/%d/%d %d:%d:%d", time.get(Calendar.YEAR), 
        						time.get(Calendar.MONTH), time.get(Calendar.DATE), 
        						time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MILLISECOND));
        					botArea.setText(String.format(
        						"<html>%s<h4>[GPT Bot�ش�%s %s]</h4><h3 color='red'>��������: %s</h3><h3 color='blue'>�ش�: </h3></html>",
        						botArea.getText().replaceAll("^<[^>]+>|<[^>]+>$", ""), msg.usr.name, f, msg.content));
        	            	
        					// �����ֽ��� �ȴ�api�ش� ����
        					IterableStream<ChatCompletions> completionsStream = 
        	            			client.getChatCompletionsStream(modelId, new ChatCompletionsOptions(chatMsgList));
        	            	completionsStream.forEach(completion -> {
        	        		    for (ChatChoice choice : completion.getChoices()) {
        	        		    	try { sleep(100); } catch (Exception e) { e.printStackTrace(); }
        	        		        ChatMessage message = choice.getDelta();
        	        		        if (message != null && message.getContent() != null)
        	        		        	botArea.setText(String.format("<html>%s%s</h3></html>",
        	        		        		botArea.getText().replaceAll("^<[^>]+>|<[^>]+>$", "")
        	        		        		.replaceAll("</h3>$", "")
        	        		        		.replaceAll("[\n\r]", "<br>"),
        	    							message.getContent()));
        	        		    }
        	        		});
        	            } catch (HttpResponseException e) {
        	    			JOptionPane.showMessageDialog(null, e, "���棺Υ��OpenAI GPT���Թ涨��", JOptionPane.WARNING_MESSAGE);
        	    			e.printStackTrace();
        	            } catch (Exception e) {
        	            	e.printStackTrace();
        				}
					}
    			};
    		}.start();
    	}
    	
    	public void clearHisMsg() {
    		if (chatMsgList != null && !chatMsgList.isEmpty()) chatMsgList.clear();
    	}
    }
}