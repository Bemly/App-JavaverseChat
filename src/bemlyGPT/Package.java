package bemlyGPT;

public class Package {
	public Msg msg;
	public int packageType;
	public Exception err;
	public User usr;
	
	public Package() {}
	// 1 ����  POST:3����ɹ�
	public Package(int packageType, Msg msg) {
		this.packageType = packageType;
		this.msg = msg;
	}
	// 2�������
	public Package(int packageType, User usr) {
		this.packageType = packageType;
		this.usr = usr;
	}
	// GET:3����ɹ�
	public Package(int packageType) {
		this.packageType = packageType;
	}
	// 4����ʧ��
	public Package(int packageType, Exception err) {
		this.packageType = packageType;
		this.err = err;
	}
}
