package 实验一;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
class LoginGUI extends JFrame{
	class LabelAndTextField extends JPanel{
		JLabel L;
		JTextField T;
		LabelAndTextField()
		{
		}
		LabelAndTextField(int size_x,int size_y,String _L)
		{
			this.setSize(size_x, size_y);
			L=new JLabel(_L);
			BoxLayout layout=new BoxLayout(this,BoxLayout.X_AXIS);
			this.setLayout(layout);
			T=new JTextField();
			T.setSize(size_x-100, size_y);
			T.setPreferredSize(new Dimension(size_x-100, size_y));
			T.setMaximumSize(new Dimension(size_x-100, size_y));
			this.add(L);
			this.add(T);
		}
	}
	LabelAndTextField L;
	JButton button;
	JPanel f;
	LoginGUI()
	{
		this.setSize(400, 400);
		this.setTitle("登录");
		f=new JPanel();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Client.exit();
			}
		});
		this.add(f);
		this.setVisible(true);
		this.reconstruct();
	}
	void reconstruct()
	{
		f.removeAll();
		LayoutManager layout=new BoxLayout(f,BoxLayout.Y_AXIS);
		f.setLayout(layout);
		L=new LabelAndTextField(400,30,"ID：");
		f.add(Box.createVerticalGlue());
		f.add(L);
		button=new JButton("确认");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Client.Login(ClientGUI.name=L.T.getText());
			}
			
		});
		f.add(button);
		f.add(Box.createVerticalGlue());
		f.updateUI();
	}
	public void IDused() {
		JDialog dialog=new JDialog(this,true);
		dialog.setResizable(false);
		dialog.setAutoRequestFocus(true);
		dialog.setSize(200, 200);
		JPanel Panel=new JPanel();
		dialog.add(Panel);
		LayoutManager layout=new BoxLayout(Panel,BoxLayout.Y_AXIS);
		Panel.setLayout(layout);
		Panel.add(Box.createVerticalGlue());
		JPanel LabelPanel=new JPanel();
		LabelPanel.setLayout(new BoxLayout(LabelPanel,BoxLayout.X_AXIS));
		LabelPanel.add(Box.createHorizontalGlue());
		LabelPanel.add(new JLabel("这个ID已经被使用了"));
		LabelPanel.add(Box.createHorizontalGlue());
		Panel.add(LabelPanel);
		Panel.add(Box.createVerticalGlue());
		JButton button=new JButton("确定");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				reconstruct();
			}
			
		});
		JPanel ButtonPanel=new JPanel();
		ButtonPanel.setLayout(new BoxLayout(ButtonPanel,BoxLayout.X_AXIS));
		ButtonPanel.add(Box.createHorizontalGlue());
		ButtonPanel.add(button);
		ButtonPanel.add(Box.createHorizontalGlue());
		Panel.add(ButtonPanel);
		Panel.add(Box.createVerticalGlue());
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.dispose();
				reconstruct();				
			}
		});
		dialog.setVisible(true);
	}
}
class ChatGUI extends JFrame{
	public static JPanel f;
	public static ClientSendServer fThread;
	public static ChatPanel Chat;
	public static OnlineList online;
	static String ListSelected=" ";
	class ChatPanel extends JPanel{
		GroupChatPanel GroupChat;
		Vector<data> CurrentSingleChat;
		SingleChatPanel SingleChat;
		int size_x,size_y;
		public ChatPanel(int size_x, int size_y) {
			this.setMaximumSize(new Dimension(size_x,size_y));
			this.setMinimumSize(new Dimension(size_x,size_y));
			this.setPreferredSize(new Dimension(size_x,size_y));
			this.size_x=size_x;this.size_y=size_y;
			GroupChat=new GroupChatPanel(size_x,size_y/2);
			SingleChat=new SingleChatPanel(size_x,size_y/2);
			online=new OnlineList(Client.GetCurrentOnlineList(),200,size_y) {
				@Override
				protected void Action()
				{
					_reconstruct();
					
				}
				public void _reconstruct()
				{
					int temp=this.table.getSelectedRow();
					if(temp!=-1)
					{
						String name=Client.GetCurrentOnlineList().elementAt(temp);
						ListSelected=name;
						CurrentSingleChat=Client.map.get(name);
					}
					ChatGUI.reconstruct();
				}
			};
			this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			this.add(GroupChat);
			this.add(SingleChat);
			// TODO 自动生成的构造函数存根
		}
		public synchronized void reconsturct()
		{
			GroupChat.reconstruct();
			SingleChat.reconstruct();
			this.updateUI();
		}
		class GroupChatPanel extends JPanel{
			JScrollPane Pane;
			JTextArea GroupMessage;
			LabelAndTextField Text;
			class LabelAndTextField extends JPanel{
				JTextField T;
				LabelAndTextField()
				{
				}
				LabelAndTextField(int size_x,int size_y)
				{
					this.setSize(size_x, size_y);
					BoxLayout layout=new BoxLayout(this,BoxLayout.X_AXIS);
					this.setLayout(layout);
					T=new JTextField();
					T.setSize(size_x-60, size_y);
					T.setPreferredSize(new Dimension(size_x-60, size_y));
					T.setMaximumSize(new Dimension(size_x-60, size_y));
					this.add(T);
					JButton button=new JButton("发送");
					button.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if(!T.getText().isEmpty())
							{
								new SendGroupMessage(OperationCode.SendGroupMessage,new data(ClientGUI.name,T.getText()).to_byte()).DoSth(Client.send);
								ChatGUI.reconstruct();
							}
						}
						
					});
					this.add(button);
				}
			}
			int size_x;
			GroupChatPanel()
			{
			}
			GroupChatPanel(int size_x,int size_y)
			{
				this.setMaximumSize(new Dimension(size_x,size_y));
				this.setMinimumSize(new Dimension(size_x,size_y));
				this.setPreferredSize(new Dimension(size_x,size_y));
				GroupMessage=new JTextArea(7,30);
				GroupMessage.setLineWrap(true);    //设置文本域中的文本为自动换行
				GroupMessage.setEditable(false);
				Pane=new JScrollPane(GroupMessage);
				Pane.setSize(size_x-40, size_y-40);
				this.size_x=size_x;
				this.reconstruct();
			}
			void reconstruct()
			{
				this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				this.removeAll();
				this.add(Pane);
				GroupMessage.selectAll();
				Text=new LabelAndTextField(size_x-40,20);
				this.add(Text);
				this.updateUI();
			}
			void AddMessage(data Msg)
			{
				GroupMessage.append(Msg.name+"\n");
				GroupMessage.append(Msg.msg+"\n");
			}
		}
		class SingleChatPanel extends JPanel{
			JScrollPane Pane;
			JTextArea SingleMessage;
			LabelAndTextField Text;
			int size_x;
			class LabelAndTextField extends JPanel{
				JTextField T;
				LabelAndTextField()
				{
				}
				LabelAndTextField(int size_x,int size_y)
				{
					this.setSize(size_x, size_y);
					BoxLayout layout=new BoxLayout(this,BoxLayout.X_AXIS);
					this.setLayout(layout);
					T=new JTextField();
					T.setSize(size_x-60, size_y);
					T.setPreferredSize(new Dimension(size_x-60, size_y));
					T.setMaximumSize(new Dimension(size_x-60, size_y));
					this.add(T);
					JButton button=new JButton("发送");
					button.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if(!ListSelected.equals(" ")&&!T.getText().isEmpty())
							{	
								String name=ListSelected;
								Client.map.get(name).add(new data(ClientGUI.name,T.getText()));
								new SendMessage(OperationCode.SendMessage,new data(name,T.getText()).to_byte()).DoSth(Client.send);
								ChatGUI.reconstruct();
							}
						}
						
					});
					this.add(button);
				}
			}
			SingleChatPanel()
			{
			}
			SingleChatPanel(int size_x,int size_y)
			{
				this.setMaximumSize(new Dimension(size_x,size_y));
				this.setMinimumSize(new Dimension(size_x,size_y));
				this.setPreferredSize(new Dimension(size_x,size_y));
				SingleMessage=new JTextArea(7,30);
				SingleMessage.setLineWrap(true);    //设置文本域中的文本为自动换行
				SingleMessage.setEditable(false);
				Pane=new JScrollPane(SingleMessage);
				Pane.setSize(size_x-40, size_y-40);
				this.size_x=size_x;
				this.reconstruct();
			}
			void reconstruct()
			{
				this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				this.removeAll();
				SingleMessage=new JTextArea(7,30);
				SingleMessage.setLineWrap(true);    //设置文本域中的文本为自动换行
				SingleMessage.setEditable(false);
				if(CurrentSingleChat!=null)
				{
					for(int i=0;i<CurrentSingleChat.size();i++)
					{
						SingleMessage.append(CurrentSingleChat.elementAt(i).name+"\n");
						SingleMessage.append(CurrentSingleChat.elementAt(i).msg+"\n");
					}
				}
				Pane=new JScrollPane(SingleMessage);
				Pane.setSize(size_x-40, size_y-40);
				this.add(Pane);
				SingleMessage.selectAll();
				Text=new LabelAndTextField(size_x-40,20);
				this.add(Text);
				this.updateUI();
			}
			
		}
		public void AddGrounpMsg(SendGroupMessage sendGroupMessage) {
			GroupChat.AddMessage(new data(sendGroupMessage.Data));
			
		}
	}
	public static void reconstruct()
	{
		f.removeAll();
//		ChatGUI.Chat.CurrentSingleChat=null;
		BoxLayout layout=new BoxLayout(f,BoxLayout.X_AXIS);
		f.setLayout(layout);
		online.RefreshList(Client.GetCurrentOnlineList());
		if(Client.map.get(ListSelected)==null) {
			ListSelected=" ";
			ChatGUI.Chat.CurrentSingleChat=null;
		}
		f.add(online);
		Chat.reconsturct();
		f.add(Chat);
		f.updateUI();		
	}
	ChatGUI(String name)
	{
		super();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Client.exit();
			}
		});
		this.setTitle(name);
		Chat=new ChatPanel(300,400);
		fThread=Client.send;
		this.setResizable(false);
		this.setSize(500, 450);
		f=new JPanel();
		this.add(f);
		BoxLayout layout=new BoxLayout(f,BoxLayout.X_AXIS);
		f.setLayout(layout);
		this.setVisible(true);
		this.reconstruct();
	}
	public static void AddGrounpMsg(SendGroupMessage sendGroupMessage) {
		Chat.AddGrounpMsg(sendGroupMessage);
		
	}
	public void ForcedLogout() {
		JDialog dialog=new JDialog(this,true);
		dialog.setResizable(false);
		dialog.setAutoRequestFocus(true);
		dialog.setSize(200, 200);
		JPanel Panel=new JPanel();
		dialog.add(Panel);
		LayoutManager layout=new BoxLayout(Panel,BoxLayout.Y_AXIS);
		Panel.setLayout(layout);
		Panel.add(Box.createVerticalGlue());
		JPanel LabelPanel=new JPanel();
		LabelPanel.setLayout(new BoxLayout(LabelPanel,BoxLayout.X_AXIS));
		LabelPanel.add(Box.createHorizontalGlue());
		LabelPanel.add(new JLabel("服务器已将你移除"));
		LabelPanel.add(Box.createHorizontalGlue());
		Panel.add(LabelPanel);
		Panel.add(Box.createVerticalGlue());
		JButton button=new JButton("确定");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				Client.exit();
			}
			
		});
		JPanel ButtonPanel=new JPanel();
		ButtonPanel.setLayout(new BoxLayout(ButtonPanel,BoxLayout.X_AXIS));
		ButtonPanel.add(Box.createHorizontalGlue());
		ButtonPanel.add(button);
		ButtonPanel.add(Box.createHorizontalGlue());
		Panel.add(ButtonPanel);
		Panel.add(Box.createVerticalGlue());
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.dispose();
				Client.exit();
			}
		});
		dialog.setVisible(true);
	}
}
public class ClientGUI extends JFrame{
	LoginGUI login;
	ChatGUI chat;
	static String name;
	ClientGUI()
	{
		login=new LoginGUI();
	}
	public void reconstruct()
	{
		ChatGUI.reconstruct();
	}
	public void LoginFailed() {
		login.IDused();
	}
	public void LoginSuccess() {
		login.setVisible(false);
		chat=new ChatGUI(ClientGUI.name);
	}
	public void AddGrounpMsg(SendGroupMessage sendGroupMessage) {
		ChatGUI.AddGrounpMsg(sendGroupMessage);
		
	}
	public void ForcedLogout() {
		chat.ForcedLogout();
		
	}
}
