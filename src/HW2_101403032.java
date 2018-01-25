//進階java HW2
//資管二 101403032 胡瑋庭

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Stroke;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class HW2_101403032 extends JFrame {
	private static final String[] tool = { "筆刷", "直線", "橢圓", "矩形" };
	private static final String[] size = { "小", "中", "大" };
	private static final String[] box = { "Yes", "No" };
	private static int r;
	private static int l = 1;
	private JComboBox combobox;
	private JLabel verticalLabel;
	private JLabel horizontalLabel;
	private JLabel mLabel;
	private JLabel rightLabel;
	private JRadioButton small;
	private JRadioButton medium;
	private JRadioButton large;
	private ButtonGroup radioGroup;
	private JCheckBox checkbox;
	private JButton button;
	private CGPanel mousePanel;
	private JPanel panel;
	private JPanel hpanel;
	private Point p1, p2;
	private Point[] points = new Point[100000];
	private int pointCounts = 0;
	private int pointCount = 0; // 存放圖形個數的變數
	private boolean isDraw = false; // 決定滑鼠是否可以在畫布上繪圖
	private String draw[] = new String[200]; // 存放圖形類型陣列
	private String fill[] = new String[200];
	private Point[] p_1 = new Point[200]; // 存放起點陣列
	private Point[] p_2 = new Point[200]; // 存放終點陣列
	private Color pen = Color.black; // 筆的顏色
	private Color[] color = new Color[200]; // 存取顏色陣列
	private Point[] p = new Point[100000]; // 存取筆刷的點
	private int[] bsize = new int[200]; // 存取大小的半徑
	private int[] num = { 4, 10, 25 };
	

	public HW2_101403032() {
		super("小畫家");

		// 畫板區
		mousePanel = new CGPanel();
		mousePanel.setBackground(Color.WHITE);
		add(mousePanel, BorderLayout.CENTER);

		// 工具區
		panel = new JPanel();
		panel.setLayout(new GridLayout(8, 1));

		// 狀態區
		hpanel = new JPanel();
		hpanel.setLayout(new GridLayout(1, 5));

		// 游標位置
		horizontalLabel = new JLabel("游標：(0,0)");
		hpanel.add(horizontalLabel);

		// 滑鼠相關處置
		MouseHandler handler = new MouseHandler();
		mousePanel.addMouseListener(handler);
		mousePanel.addMouseMotionListener(handler);

		// 繪圖工具
		verticalLabel = new JLabel("[繪圖工具]");
		panel.add(verticalLabel);

		// 工具選項
		combobox = new JComboBox(tool);
		combobox.setMaximumRowCount(3);

		// 工具使用狀態
		mLabel = new JLabel("工具：" + tool[combobox.getSelectedIndex()]
				+ ",  筆刷大小：" + size[0] + ",  填滿：" + box[l]);
		hpanel.add(mLabel);

		// 下拉式選單相關處置
		combobox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				mLabel.setText("工具：" + tool[combobox.getSelectedIndex()]
						+ ",  筆刷大小：" + size[r] + ",  填滿：" + box[l]);
				if (tool[combobox.getSelectedIndex()] == "筆刷") {
					checkbox.setEnabled(false);
				} else {
					checkbox.setEnabled(true);
				}
			}

		});

		panel.add(combobox);

		// 筆刷大小
		verticalLabel = new JLabel("[筆刷大小]");
		panel.add(verticalLabel);

		// 筆刷
		small = new JRadioButton(size[0], true);
		medium = new JRadioButton(size[1], false);
		large = new JRadioButton(size[2], false);
		panel.add(small);
		panel.add(medium);
		panel.add(large);

		radioGroup = new ButtonGroup();
		radioGroup.add(small);
		radioGroup.add(medium);
		radioGroup.add(large);
		large.addItemListener(new RadioButtonHandler(size[2]));
		small.addItemListener(new RadioButtonHandler(size[0]));
		medium.addItemListener(new RadioButtonHandler(size[1]));

		// 填滿
		checkbox = new JCheckBox("填滿");
		panel.add(checkbox);
		checkbox.setEnabled(false);

		// 填滿的相關處置
		CheckBoxHandler handler1 = new CheckBoxHandler();
		checkbox.addItemListener(handler1);

		// 清除畫面
		button = new JButton("清除畫面");
		panel.add(button);

		// 清除畫面的相關處置
		ButtonHandler handler3 = new ButtonHandler();
		button.addActionListener(handler3);

		// 這裡是其它狀態列
		rightLabel = new JLabel("滑鼠在畫布外面");
		hpanel.add(rightLabel);

		add(panel, BorderLayout.WEST);
		add(hpanel, BorderLayout.SOUTH);

	}

	private class MouseHandler implements MouseMotionListener, MouseListener {
		// 滑鼠移動
		public void mouseMoved(MouseEvent event) {
			horizontalLabel.setText(String.format("游標位置：(%d,%d)", event.getX(),
					event.getY()));
		}

		// 滑鼠拖曳
		public void mouseDragged(MouseEvent event) {
			rightLabel.setText(String.format("拖曳"));

			// 終點
			p2 = event.getPoint();
			isDraw = true;
			repaint();

			// 筆刷
			if (tool[combobox.getSelectedIndex()] == "筆刷") {
				if (pointCounts < points.length) {
					points[pointCounts] = event.getPoint();
					bsize[pointCounts] = num[r];
					pointCounts++;

				}

			}

		}

		public void mouseClicked(MouseEvent event) {
			rightLabel.setText(String.format("按一下"));

		
		}

		// 滑鼠按下
		public void mousePressed(MouseEvent Event) {
			rightLabel.setText(String.format("按下"));

			isDraw = false;

			// 起始點
			p1 = Event.getPoint();
		}

		// 滑鼠彈開
		public void mouseReleased(MouseEvent e) {
			rightLabel.setText(String.format("彈開"));

			if (isDraw)
				pointCount++;
			isDraw = false;

		}

		// 滑鼠進畫布
		public void mouseEntered(MouseEvent e) {
			rightLabel.setText(String.format("滑鼠進入畫布"));

		}

		// 滑鼠出畫布
		public void mouseExited(MouseEvent e) {
			rightLabel.setText(String.format("滑鼠離開畫布"));

		}

	}

	// 筆刷大小的相關處置
	private class RadioButtonHandler implements ItemListener {
		private int k;

		public RadioButtonHandler(String s) {
			if (s == "小") {
				k = 0;
			} else if (s == "中") {
				k = 1;
			} else if (s == "大") {
				k = 2;
			}

		}

		public void itemStateChanged(ItemEvent e) {
			r = k;
			mLabel.setText("工具：" + tool[combobox.getSelectedIndex()]
					+ ",  筆刷大小：" + size[k] + ",  填滿：" + box[l]);
		}
	}

	// 填滿的相關處置
	private class CheckBoxHandler implements ItemListener {
		private int p;

		public void itemStateChanged(ItemEvent Event) {
			if (checkbox.isSelected()) {
				p = 0;
			} else {
				p = 1;
			}
			mLabel.setText("工具：" + tool[combobox.getSelectedIndex()]
					+ ",  筆刷大小：" + size[r] + ",  填滿：" + box[p]);
			l = p;
		}

	}

	// 清除畫面
	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			rightLabel.setText(String.format("清除畫面"));

			// 執行清除

			pointCount++;
			// repaint();
			pointCount = 0;
			pointCounts = 0;
			isDraw = false;
		}
	}

	class CGPanel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE);

			if (!isDraw)
				return;

			// 重繪已有的圖片
			for (int i = 0; i < pointCount; i++) {
				drawing(g, draw[i], fill[i], p_1[i].x, p_1[i].y, p_2[i].x,
						p_2[i].y);
			}

			for (int i = 0; i < pointCounts; i++) {
				g.fillOval(points[i].x, points[i].y, bsize[i], bsize[i]);
			}

			// 繪製新圖片

			drawing(g, tool[combobox.getSelectedIndex()], box[l], p1.x, p1.y,
					p2.x, p2.y);

			// 最新繪製的圖形存在陣列變數中
			p_1[pointCount] = p1;
			p_2[pointCount] = p2;
			draw[pointCount] = tool[combobox.getSelectedIndex()];
			fill[pointCount] = box[l];
			p[pointCount] = points[pointCounts];

		}
	}

	// 畫布畫新圖形
	public void drawing(Graphics g, String s, String t, int px1, int py1,
			int px2, int py2) {
		if (s == "直線" && t == "Yes")
			g.drawLine(px1, py1, px2, py2);
		if (s == "直線" && t == "No") {
			Graphics2D g2d = (Graphics2D) g;
			Stroke st = g2d.getStroke();
			Stroke bs;
			// LINE_TYPE_DASHED
			bs = new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 0, new float[] { 16, 4 }, 0);
			// 變成虛線
			g2d.setStroke(bs);
			g2d.drawLine(px1, py1, px2, py2);
			// 再變回實現
			g2d.setStroke(st);

		}
		if (s == "橢圓" && t == "No")
			g.drawOval(Math.min(px1,px2), Math.min(py1,py2), Math.abs(px2 - px1), Math.abs(py2 - py1));
		if (s == "橢圓" && t == "Yes")
			g.fillOval(Math.min(px1,px2), Math.min(py1,py2), Math.abs(px2 - px1),  Math.abs(py2 - py1));
		if (s == "矩形" && t == "No")
			g.drawRect(Math.min(px1,px2), Math.min(py1,py2), Math.abs(px2 - px1),Math.abs(py2 - py1));
		if (s == "矩形" && t == "Yes")
			g.fillRect(Math.min(px1,px2), Math.min(py1,py2),Math.abs(px2 - px1), Math.abs(py2 - py1));

	}

}
