package 实验一;

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
			// TODO 自动生成的 catch 块
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
		//拆包
		this.OperationType=Msg[0];
		this.Data=new byte[Msg.length-2];
		for(int i=2;i<Msg.length;i++) this.Data[i-2]=Msg[i];
	}
	public byte[] ToByte()
	{
		//打包
		byte[] temp=new byte[2+Data.length];
		temp[0]=this.OperationType;
		temp[1]=0;
		for(int i=0;i<Data.length;i++) temp[i+2]=Data[i];
		return temp;
	}
	public void DoSth(MultiServer S)
	{
		//这是个用来让继承类重写的函数
		S.SendMessage(this);
	}
	public void DoSth(ClientSendServer C)
	{
		//这是个用来让继承类重写的函数
		C.SendMsg(this);
	}
	public void DoSth(ClientReadServer C)
	{
		//这是个用来让继承类重写的函数
	}
	public void Reconstruct(byte[] Data)
	{
		//这是个用来让继承类重写的函数
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
		//这是个用来让继承类重写的函数
		try {
			S.userRegist(new String(this.Data,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
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
		//这是个用来让继承类重写的函数
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
		//这是个用来让继承类重写的函数
		S.userExit(S.socket);
	}

	
}
class ForcedLogoutMessage extends MsgPackage{

	ForcedLogoutMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO 自动生成的构造函数存根
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
		// TODO 自动生成的构造函数存根
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
				// TODO 自动生成的 catch 块
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
		// TODO 自动生成的构造函数存根
	}
	public void DoSth(ClientReadServer C)
	{
		C.LoginFailed();
	}
	
}
class SendGroupMessage extends MsgPackage{

	SendGroupMessage(byte OperationType, String Data) {
		super(OperationType, Data);
		// TODO 自动生成的构造函数存根
	}
	public SendGroupMessage(byte OperationType, byte[] Data) {
		super(OperationType,Data);
	}
	public void DoSth(MultiServer S)
	{
		//这是个用来让继承类重写的函数
        Set<Map.Entry<String,Socket>> set=MultiServer.map.entrySet();
        for(Map.Entry<String,Socket> entry:set){
            Socket client=entry.getValue();
            OutputStream out;
			try {
				out = client.getOutputStream();
	            out.write(this.ToByte());
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        }
	}
	public void DoSth(ClientReadServer C)
	{
		//这是个用来让继承类重写的函数
		C.AddGrounpMsg(this);
	}	
	
}