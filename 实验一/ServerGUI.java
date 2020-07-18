package 实验一;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
class ServerPanel extends JPanel{
	public OnlineList L;
	LayoutManager layout;
	ServerPanel()
	{
		this.setMaximumSize(new Dimension(300,500));
		this.setMinimumSize(new Dimension(300,500));
		this.setPreferredSize(new Dimension(300,500));
		layout=new BoxLayout(this,BoxLayout.Y_AXIS);
		this.setLayout(layout);
		L=new OnlineList(MultiServer.GetCurrentOnlineList(),300,400);
		this.add(L);
		JButton button=new JButton("踢人下线");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(L.Selected())
				{
					String temp=L.GetSelectedName();
					MultiServer.ForcedLogout(temp);
				}
				
			}
			
		});
		this.add(button);
	}
}class ServerGroupMessagePanel extends JPanel{
	LayoutManager layout;
	JPanel SPPanel;
	JTextField Text;
	ServerGroupMessagePanel()
	{
		SPPanel=new JPanel();
		SPPanel.setSize(300, 400);
		this.setMaximumSize(new Dimension(300,500));
		this.setMinimumSize(new Dimension(300,500));
		this.setPreferredSize(new Dimension(300,500));
		layout=new BoxLayout(this,BoxLayout.Y_AXIS);
		this.setLayout(layout);
		Text=new JTextField();
		Text.setSize(500, 30);
		Text.setPreferredSize(new Dimension(500,30));
		Text.setMaximumSize(new Dimension(500,30));
		this.add(SPPanel);
		JLabel label=new JLabel("群发消息");
		this.add(label);
		this.add(Text);
		JButton button=new JButton("确认");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Text.getText().isEmpty())
				{
					MultiServer.SendGroupMessage("服务器消息",Text.getText());
				}
				
			}
			
		});
		this.add(button);
	}
}
public class ServerGUI extends JFrame{
	MsgPanel msg_panel;
	JPanel f;
	ServerPanel S;
	ServerGroupMessagePanel G;
	ServerGUI()
	{
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f=new JPanel();
		this.setResizable(false);
		this.setSize(600, 500);
		this.setTitle("服务器端");
		this.add(f);
		BoxLayout layout=new BoxLayout(f,BoxLayout.X_AXIS);
		f.setLayout(layout);
		S=new ServerPanel();
		f.add(S);
		G=new ServerGroupMessagePanel();
		f.add(G);
		this.setVisible(true);
	}
	void reconstruct()
	{
		f.removeAll();
		BoxLayout layout=new BoxLayout(f,BoxLayout.X_AXIS);
		f.setLayout(layout);
		S=new ServerPanel();
		f.add(S);
		G=new ServerGroupMessagePanel();
		f.add(G);
		f.updateUI();
	}
}