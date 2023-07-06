package tcpNoJson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;

public class demo {
	
	public static void main(String[] args) throws Exception {
		String[] names = {"������", "����", "����", "�Ų�"};

        for (String name : names) {
            String encoded = Base64.getEncoder().encodeToString(name.getBytes());
            System.out.println(name + " ��Base64����Ϊ��" + encoded);
        }
//		demo d = new demo();
//		d.connect();
//		d.s();
//		d.close();
        String t = "�ٲ����� ����\n\n����";
        System.out.println(t);
        System.out.println(t.replaceAll("\n", "<br>").replaceAll("<br>", "\n"));
	}
	
	public void s() throws Exception {
		String sql = "UPDATE user SET neckName=? WHERE UID=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // ���ò���
        pstmt.setString(1, "���");
        pstmt.setInt(2, 1); // ����Ҫ���µ�UIDΪ1

        int rows = pstmt.executeUpdate();
        System.out.println(rows + " �м�¼�����¡�");
	}
	
	public void f() throws Exception {
		String sql = "SELECT friendIDs FROM user WHERE UID=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // ���ò���
        pstmt.setInt(1, 3);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String friendIDs = rs.getString("friendIDs");
            System.out.println("�����ӵ�friendIDsΪ��" + friendIDs);
        }
	}
	
	public void e() throws Exception {
            String sql = "CREATE TABLE `" + 1 + "` (count INT NOT NULL AUTO_INCREMENT, UID INT NOT NULL, time TIMESTAMP NOT NULL, content TEXT NOT NULL, roomID INT NOT NULL, UIDs TEXT NOT NULL, PRIMARY KEY (count)) ENGINE=InnoDB DEFAULT CHARSET=gbk COLLATE=gbk_chinese_ci;";
            stmt.executeUpdate(sql);
            System.out.println("��1�����ɹ���");
	}
	
	public void a() throws SQLException {
		String sql = "SELECT MAX(roomID) AS max_roomID FROM room";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int maxRoomID = rs.getInt("max_roomID");
            System.out.println("room�������Զ���ŵ�roomID����ǣ�" + maxRoomID);
        }
	}
	
	
	protected Connection conn=null;
	protected PreparedStatement pstmt = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;
	// ��prepare׼��״̬�������ݿ�
	public void connect() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ChatPZH?useUnicode=true&characterEncoding=GBK&user=root&password=5477"); 
		stmt = conn.createStatement();
	}
	// �ر����ݿ�����
    public void close() throws Exception {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
    
    // �����ݿ������ȡһά����
    public static String int2DtoString(int[] arr) {
    	return String.join(",", Arrays.stream(arr).mapToObj(String::valueOf).toArray(String[]::new));
	}
    
    public static int[] stringtoInt2D(String str) {
    	return Arrays.stream(str.split(",")).mapToInt(Integer::parseInt).toArray();
    }
}
