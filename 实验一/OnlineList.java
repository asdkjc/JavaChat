package ʵ��һ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OnlineList extends JPanel{
	Vector<String> namelist;
	JTable table;
	JScrollPane SP;
	OnlineList(Vector<String> namelist,int size_x,int size_y)
	{
		super();
		this.setMaximumSize(new Dimension(size_x,size_y));
		this.setMinimumSize(new Dimension(size_x,size_y));
		this.setPreferredSize(new Dimension(size_x,size_y));
		RefreshList(namelist);
	}
	public void RefreshList(Vector<String> namelist)
	{
		this.removeAll();
		LayoutManager layout=new BoxLayout(this,BoxLayout.Y_AXIS);
		this.setLayout(layout);
		if(namelist.size()<1)
		{
			namelist=new Vector<String>();
			namelist.add(" ");
		}
		this.namelist=namelist;
		String[] Title= {"�û�"};
		String[][] Text=new String[namelist.size()][1];
		for(int i=0;i<namelist.size();i++) Text[i][0]=namelist.elementAt(i);
		table=new JTable(Text,Title){
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e) {
						Action();
						
					}			
				});
		SP=new JScrollPane(table);
		SP.setMaximumSize(new Dimension(300,700));
		SP.setMinimumSize(new Dimension(300,700));
		SP.setPreferredSize(new Dimension(300,700));
		
		this.add(SP);
		this.updateUI();
	}
	protected void Action() {
		//����������д�ķ���
		
	}
	public boolean Selected()
	{
		return table.getSelectedColumn()!=-1;
	}
	public String GetSelectedName()
	{
		return namelist.elementAt(table.getSelectedRow());
	}
	public void reconstruct() {
		// TODO �Զ����ɵķ������
		
	}
}
