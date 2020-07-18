package 实验一;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
 
class ClientReadServer extends Thread implements Runnable{
    private Socket socket;
    MsgPackage[] MsgList;
    public ClientReadServer(Socket socket){
        MsgList=new MsgPackage[10];
        MsgList[1]=new Login(OperationCode.Login," ");
        MsgList[2]=new SendMessage(OperationCode.SendMessage," ");
        MsgList[3]=new Logout(OperationCode.Logout," ");
        MsgList[4]=new ForcedLogoutMessage(OperationCode.ForcedLogout," ");
        MsgList[5]=new RefreshMessage(OperationCode.RefreshMessage," ");
        MsgList[6]=new LoginSuccessMessage(OperationCode.LoginSuccessMessage," ");
        MsgList[7]=new LoginFailedMessage(OperationCode.LoginFailedMessage," ");
        MsgList[8]=new SendGroupMessage(OperationCode.SendGroupMessage," ");
        this.socket=socket;
    }
    @Override
    public void run() {
        try {
            InputStream in=socket.getInputStream();
            byte b[]=new byte[2333];
            int lenb;
            while(true){
            	if(this.isInterrupted())
            	{
            		return;
            	}
            	lenb=in.read(b);
            	byte[] temp=new byte[lenb];
            	for(int i=0;i<lenb;i++) temp[i]=b[i];
            	MsgPackage Msg=new MsgPackage(temp);
            	MsgList[Msg.OperationType].Reconstruct(Msg.Data);
            	MsgList[Msg.OperationType].DoSth(this);
            	System.out.println((int) Msg.OperationType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void AddGrounpMsg(SendGroupMessage sendGroupMessage)
    {
    	Client.g.AddGrounpMsg(sendGroupMessage);
    }
	public void LoginSuccess() {
		Client.LoginSuccess();
		
	}
	public void LoginFailed() {
		Client.LoginFailed();
		
	}
	public void RefreshList(byte[] Data) {
		try {
			String tot=new String(Data,"UTF-8");
			Vector<String> namelist=new Vector<String>();
			String[] ll=tot.split(" ");
			for(int i=0;i<ll.length;i++) namelist.add(ll[i]);
	        Set<Map.Entry<String,Vector<data>>> mapset=Client.map.entrySet();
	        for(Map.Entry<String,Vector<data>> entry:mapset){
	        	boolean exist=false;
	        	for(String name:namelist)
	        	{
	        		if(entry.getKey().equals(name)) exist=true;
	        	}
	        	if(!exist)
	        	{
	        		Client.map.remove(entry.getKey());
	        	}
	        }
	    	for(String name:namelist)
	    	{
	    		if(!Client.map.containsKey(name)) {
	    			Client.map.put(name, new Vector<data>());
	    		}
	    	}
	    	Client.set.clear();
	    	for(int i=0;i<ll.length;i++)Client.set.add(ll[i]);
	    	Client.refresh();
	    	} catch (UnsupportedEncodingException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}

	}
}
class ClientSendServer extends Thread implements Runnable{
    public Socket socket;
    MsgPackage msg;
    public ClientSendServer(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {
        try {
            OutputStream out=socket.getOutputStream();
                while(true){
                	if(this.isInterrupted())
                	{
                		return;
                	}
                	if(this.msg!=null)
                	{
                		out.write(msg.ToByte());
                		this.msg=null;
                	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void SendMsg(MsgPackage d)
    {
       	this.msg=d;
    }
    public void Login(String name)
    {
    	MsgPackage Msg=new Login(OperationCode.Login,name);
    	SendMsg(Msg);
    }
    public void Logout()
    {
    	MsgPackage Msg=new Logout(OperationCode.Logout," ");
    	SendMsg(Msg);    	
    }
}
public class Client {
	public static ClientGUI g;
	static ClientSendServer send;
	static ClientReadServer read;
    public static Set<String> set=new HashSet<String>();
	static Map<String,Vector<data> > map=new ConcurrentHashMap<>();
    public static void main(String[] args) throws IOException{
        Socket socket=new Socket("127.0.0.1",6666);
        send=new ClientSendServer(socket);
        send.start();
    	g=new ClientGUI();
        read=new ClientReadServer(socket);
        read.start();
    }
    public static void refresh() {
		// TODO 自动生成的方法存根
		g.reconstruct();
	}
	public static Vector<String> GetCurrentOnlineList()
    {
    	Vector<String> temp=new Vector<String>();
    	for(String s:set)
    	{
    		temp.add(s);
    	}
    	return temp;
    }
    public static void LoginFailed() {
		g.LoginFailed();
		
	}
	public static void LoginSuccess() {
		g.LoginSuccess();
		
	}
	public static void Login(String name)
    {
    	send.Login(name);
    }
    public static void ForcedLogout()
    {
    	g.ForcedLogout();
    }
    public static void exit()
    {
    	System.exit(0);
    }
    public static void AddSingleMessage(data Msg)
    {
    	map.get(Msg.name).add(Msg);
    }
}