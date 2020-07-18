package สตั้าป;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MsgPanel extends JPanel{
	static data[] msg_queue=new data[8];
	static int head=0,tail=0;
	MsgPanel()
	{
		
	}
	MsgPanel(int x,int y)
	{
		super();
		this.setSize(x,y);
	}
	void AddMsg(data d)
	{
		msg_queue[tail]=d;
		tail=(tail+1)%msg_queue.length;
		if(head==tail) head=(head+1)%msg_queue.length;
		this.repaint();
	}
	public void paint(Graphics g)
	{
		int x=20,y=40;
		for(int i=head;i!=tail;i=(i+1)%msg_queue.length)
		{
			g.drawString(msg_queue[i].name, x, y);
			y+=30;
			g.drawString(msg_queue[i].msg, x, y);
			y+=50;
		}
	}
}
