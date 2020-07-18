package 实验一;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

 
class MultiServer implements Runnable{
    public static Map<String,Socket> map=new ConcurrentHashMap<>();
    public static Set<String> set=new HashSet<String>();
    public static Map<Socket,MultiServer> MultiServerMap=new ConcurrentHashMap<>();
    String name;
    MsgPackage[] MsgList;
    public Socket socket;
    ServerGUI g;
    private MsgPackage Msg;
    public static Vector<String> GetCurrentOnlineList()
    {
    	Vector<String> temp=new Vector<String>();
    	for(String s:set)
    	{
    		temp.add(s);
    	}
    	return temp;
    }
    public MultiServer(Socket socket,ServerGUI g){
        this.socket=socket;
        this.g=g;
        MultiServerMap.put(socket,this);
        MsgList=new MsgPackage[10];
        MsgList[1]=new Login(OperationCode.Login," ");
        MsgList[2]=new SendMessage(OperationCode.SendMessage," ");
        MsgList[3]=new Logout(OperationCode.Logout," ");
        MsgList[4]=new ForcedLogoutMessage(OperationCode.ForcedLogout," ");
        MsgList[5]=new RefreshMessage(OperationCode.RefreshMessage," ");
        MsgList[6]=new LoginSuccessMessage(OperationCode.LoginSuccessMessage," ");
        MsgList[7]=new LoginFailedMessage(OperationCode.LoginFailedMessage," ");
        MsgList[8]=new SendGroupMessage(OperationCode.SendGroupMessage," ");
    }
    public MultiServer(){}
    @Override
    public void run() {
        try {
        	InputStream in=socket.getInputStream();
       		int lenb;
            while(true){
           		byte b[];
           		MsgPackage Msg=null;
           		b=new byte[2333];
           		if((lenb=in.read(b))>2)
           		{	
           			byte[] temp=new byte[lenb];
           			for(int i=0;i<lenb;i++) temp[i]=b[i];
           			MsgList[0]=new MsgPackage(temp);
           			MsgList[MsgList[0].OperationType].Reconstruct(MsgList[0].Data);
           			Msg=MsgList[MsgList[0].OperationType];
           			Msg.DoSth(this);
           		}
            }
        }catch(SocketException e)
        {
            userExit(socket);
            MultiServer.Refresh();
            g.reconstruct();
            return;

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userRegist(String userName){
    	if(set.add(userName))
    	{
    		LoginSuccess();
    		this.name=userName;
    		map.put(userName,this.socket);
    		MultiServer.Refresh();
    		g.reconstruct();
    	}else LoginFailed();
    }
    public static void Refresh()
    {
    	String temp=new String();
    	for(String iterator:set)
    	{
    		temp+=iterator;
    		temp+=' ';
    	}
    	new RefreshMessage(OperationCode.RefreshMessage,temp).DoSth(new MultiServer());;
    }
    public static void ForcedLogout(String name)
    {
    	if(MultiServer.map.get(name)!=null)
    	{
    		MsgPackage Msg;
    		Msg=new ForcedLogoutMessage(OperationCode.ForcedLogout," ");
    		Msg.DoSth(MultiServer.MultiServerMap.get(MultiServer.map.get(name)));
    		userExit(MultiServer.map.get(name));
            MultiServer.Refresh();
    	}
    	Server.g.reconstruct();
    }
    private void LoginFailed() {
    	MsgPackage Msg;
		Msg=new LoginFailedMessage();
		Msg.DoSth(this);
	}
	private void LoginSuccess() {
    	MsgPackage Msg;
		Msg=new LoginSuccessMessage();
		Msg.DoSth(this);
	}
	public void SendMessage(MsgPackage Msg)
	{
		try {
			OutputStream out=this.socket.getOutputStream();
			out.write(Msg.ToByte());
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	public void ForceSomeoneLogout()
	{

	}
    public static synchronized void userExit(Socket socket){
        String userName=null;
        for(String key:map.keySet()){
            if(map.get(key).equals(socket)){
                userName=key;
                break;
            }
        }
        if(userName!=null)
        {	
        	map.remove(userName,socket);
        	set.remove(userName);
        }
    }
	public static void SendGroupMessage(String string, String text) {
		// TODO 自动生成的方法存根
		MsgPackage Msg=new SendGroupMessage(OperationCode.SendGroupMessage,new data(string,text).to_byte());
		Msg.DoSth(new MultiServer());
    	Server.g.reconstruct();	
	}
	public void SendSingleMessage(MsgPackage sendMessage) {
		String name=new data(sendMessage.Data).name;
		String msg=new data(sendMessage.Data).msg;
		MultiServer temp=MultiServerMap.get(map.get(name));
		temp.SendMessage(new SendMessage(sendMessage.OperationType,new data(this.name,msg).to_byte()));
	}
}
public class Server {
	public static ServerGUI g;
    public static void main(String[] args){
    	g=new ServerGUI();
        try {
            ServerSocket serverSocket=new ServerSocket(6666);
            ExecutorService executorService= Executors.newFixedThreadPool(20);
            while(true){
                Socket socket=serverSocket.accept();
                executorService.execute(new MultiServer(socket,g));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 