package ʵ��һ;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class MsgPackage {
	byte OperationType;
	byte[] Data;
	MsgPackage(byte OperationType,String Data)
	{
		this.OperationType=OperationType;
		try {
			this.Data=Data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	MsgPackage(byte OperationType,byte[] Data)
	{
		this.OperationType=OperationType;
		this.Data=Data;
	}
	MsgPackage(byte Msg[])
	{
		//���
		this.OperationType=Msg[0];
		this.Data=new byte[Msg.length-2];
		for(int i=2;i<Msg.length;i++) this.Data[i-2]=Msg[i];
	}
	public byte[] ToByte()
	{
		//���
		byte[] temp=new byte[2+Data.length];
		temp[0]=this.OperationType;
		temp[1]=0;
		for(int i=0;i<Data.length;i++) temp[i+2]=Data[i];
		return temp;
	}
	public void DoSth(MultiServer S)
	{
		//���Ǹ������ü̳�����д�ĺ���
		S.SendMessage(this);
	}
	public void DoSth(ClientSendServer C)
	{
		//���Ǹ������ü̳�����д�ĺ���
		C.SendMsg(this);
	}
	public void DoSth(ClientReadServer C)
	{
		//���Ǹ������ü̳�����д�ĺ���
	}
	public void Reconstruct(byte[] Data)
	{
		//���Ǹ������ü̳�����д�ĺ���
		this.Data=Data;	
	}
}
class Login extends MsgPackage{

	Login(byte OperationType, String Data) {
		super(OperationType, Data);
	}
	@Override
	public void DoSth(MultiServer S)
	{
		//���Ǹ������ü̳�����д�ĺ���
		try {
			S.userRegist(new String(this.Data,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
}
class SendMessage extends MsgPackage{
	SendMessage(byte OperationType, String Data) {
		super(OperationType, Data);
	}
	SendMessage(byte OperationType, byte[] Data) {
		super(OperationType, Data);
	}
	public void DoSth(MultiServer S)
	{
		//���Ǹ������ü̳�����д�ĺ���
		S.SendSingleMessage(this);
	}
	public void DoSth(ClientReadServer C)
	{
		Client.AddSingleMessage(new data(this.Data));
		Client.g.reconstruct();
	}
}
class Logout extends MsgPackage{
	Logout(byte OperationType, String Data) {
		super(OperationType, Data);
	}
	@Override
	public void DoSth(MultiServer S)
	{
		//���Ǹ������ü̳�����д�ĺ���
		S.userExit(S.socket);
	}

	
}
class ForcedLogoutMessage extends MsgPackage{

	ForcedLogoutMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO �Զ����ɵĹ��캯�����
	}
	@Override
	public void DoSth(ClientReadServer C)
	{
		Client.ForcedLogout();		
	}
	
}
class RefreshMessage extends MsgPackage{

	RefreshMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO �Զ����ɵĹ��캯�����
	}
	public void DoSth(ClientReadServer C)
	{
		C.RefreshList(this.Data);
	}	
	@Override
	public void DoSth(MultiServer S)
	{
        Set<Map.Entry<String,Socket>> set=MultiServer.map.entrySet();
        for(Map.Entry<String,Socket> entry:set){
            Socket client=entry.getValue();
            OutputStream out;
			try {
				out = client.getOutputStream();
	            out.write(this.ToByte());
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
        }		
	}

}
class LoginSuccessMessage extends MsgPackage{
	LoginSuccessMessage()
	{
		super(OperationCode.LoginSuccessMessage," ");
	}
	LoginSuccessMessage(byte OperationType, String Data) {
		super(OperationType, Data);
	}
	public void DoSth(ClientReadServer C)
	{
		C.LoginSuccess();
	}
	
}
class LoginFailedMessage extends MsgPackage{
	LoginFailedMessage()
	{
		super(OperationCode.LoginFailedMessage," ");		
	}
	LoginFailedMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO �Զ����ɵĹ��캯�����
	}
	public void DoSth(ClientReadServer C)
	{
		C.LoginFailed();
	}
	
}
class SendGroupMessage extends MsgPackage{

	SendGroupMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO �Զ����ɵĹ��캯�����
	}
	public SendGroupMessage(byte OperationType, byte[] Data) {
		super(OperationType,Data);
	}
	public void DoSth(MultiServer S)
	{
		//���Ǹ������ü̳�����д�ĺ���
        Set<Map.Entry<String,Socket>> set=MultiServer.map.entrySet();
        for(Map.Entry<String,Socket> entry:set){
            Socket client=entry.getValue();
            OutputStream out;
			try {
				out = client.getOutputStream();
	            out.write(this.ToByte());
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
        }
	}
	public void DoSth(ClientReadServer C)
	{
		//���Ǹ������ü̳�����д�ĺ���
		C.AddGrounpMsg(this);
	}	
	
}