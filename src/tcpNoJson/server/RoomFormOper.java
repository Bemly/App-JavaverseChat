package tcpNoJson.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tcpNoJson.Room;

class RoomFormOper extends AccessMySQL {
	
	// ��������
	// ��������Ҫʹ��DDL��Data Definition Language����䣬����CREATE TABLE��䡣DDL��䲻��ʹ��Ԥ����
	public int create() {
		try {
			int newRoomID = getMax("roomID") + 1;
			String sql = "CREATE TABLE `" + newRoomID + "` (count INT NOT NULL AUTO_INCREMENT,"
					+ " UID INT NOT NULL, time TIMESTAMP NOT NULL, content TEXT NOT NULL,"
					+ " roomID INT NOT NULL, UIDs TEXT NOT NULL, PRIMARY KEY (count))"
					+ " ENGINE=InnoDB DEFAULT CHARSET=gbk COLLATE=gbk_chinese_ci;";
			stmt.executeUpdate(sql);
			System.out.println("["+Thread.currentThread().getName()+"]����Ⱥ���ɹ�");
			return newRoomID;
		} catch (SQLException e) {
			System.out.println("["+Thread.currentThread().getName()+"]����Ⱥ���ʧ��");
			return 0;
		}
	}
	
	// ��ѯ�����������
	public int getMax(String rowName) throws SQLException {
		String sql = "SELECT MAX("+ rowName +") AS max_value FROM room";
        ResultSet rs = stmt.executeQuery(sql);
        int max = 0;
        while (rs.next()) max = rs.getInt("max_value");
        return max;
	}

	// ��ӷ���
	public void add(Room room) throws SQLException {
	    String sql = "INSERT INTO Room(roomID, Type, UIDs) VALUES (?, ?, ?, ?)";
	    pstmt = conn.prepareStatement(sql);
	    pstmt.setInt(1, room.getRoomID());
	    pstmt.setInt(2, room.getType());
	    pstmt.setString(3, int2DtoString(room.getUIDs()));
	    pstmt.executeUpdate();
	}

	// ɾ������
	public void deleteRoom(String roomID) throws Exception {
	    String sql = "DELETE FROM Room WHERE roomID=?";
	    pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, roomID);
	    pstmt.executeUpdate();
	}

	// �޸ķ�����Ϣ
	public void updateRoom(Room room) throws Exception {
	    String sql = "UPDATE Room SET Type=?, UIDs=? WHERE roomID=?";
	    pstmt = conn.prepareStatement(sql);
	    pstmt.setInt(4, room.getRoomID());
	    pstmt.setInt(1, room.getType());
	    pstmt.setString(2, int2DtoString(room.getUIDs()));
	    pstmt.executeUpdate();
	}

	// ��ѯ������Ϣ
	public Room getRoom(String roomID) throws Exception {
	    Room room = null;
	    String sql = "SELECT * FROM Room WHERE roomID=?";
	    pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, roomID);
	    rs = pstmt.executeQuery();
	    if (rs.next()) {
	        room = new Room();
	        room.setRoomID(rs.getInt("roomID"));
	        room.setType(rs.getInt("Type"));
	        room.setUIDs(stringtoInt2D(rs.getString("UIDs")));
	    }
	    return room;
	}
}

