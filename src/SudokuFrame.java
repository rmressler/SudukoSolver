import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;


public class SudokuFrame extends JFrame {

	private JPanel buttons;
	private JPanel gridPane;
	private JButton solve, revert, reset, check;
	private JTextField txt;
	private JTextField[][] cells;
	private Grid g, old;
	private int size;
	private JButton btnResize;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SudokuFrame frame = new SudokuFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SudokuFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Sudoku Solver");
		setBounds(100, 100, 600, 475);
		
		size = 9;
		create(size);
				
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(0,1));
		txt = new JTextField();
		txt.setHorizontalAlignment(SwingConstants.CENTER);
		txt.setText("Resize");
		txt.addFocusListener(new FocusListener() {
	        @Override
	        public void focusGained(FocusEvent e){
	            txt.setText("");
	        }

			@Override
			public void focusLost(FocusEvent arg0) {
			}
	    });
		
		btnResize = new JButton("Resize");
		buttons.add(btnResize);
		buttons.add(txt);
		btnResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					size = Integer.parseInt(txt.getText());
				}
				catch (NumberFormatException e) {
					txt.setText("Invalid size");
				}
				if(size < 9 || Math.sqrt(size) % 1 != 0) {
					txt.setText("Invalid size");
				}
				else {
					getContentPane().remove(gridPane);
					create(size);
				}
			}
		});
		
		check = new JButton("Check");
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				store();
				if(g.errors()) txt.setText("Errors!");
				else txt.setText("No errors.");
			}
		});
		buttons.add(check);
		
		solve = new JButton("Solve!");
		solve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				store();
				old = new Grid(g);
				long startTime = System.nanoTime();
				g = g.solver(g);
				long endTime = System.nanoTime();
				if(g != null) {
					write();
					double time = (endTime - startTime) / Math.pow(10, 9);
					DecimalFormat df = new DecimalFormat("#.####");
					txt.setText(df.format(time));
				}
				else txt.setText("Not solvable");
			}
		});
		buttons.add(solve);

		revert = new JButton("Revert");
		revert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				g = old;
				write();
			}
		});
		buttons.add(revert);
		
		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				getContentPane().remove(gridPane);
				create(size);
			}
		});
		buttons.add(reset);
		
		getContentPane().add(buttons, BorderLayout.EAST);
	}

	public void store() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				String str = cells[i][j].getText();
				if(str.equals("")) g.grid[i][j].reset();
				else {
					int val = Integer.parseInt(str);
					if(val > 0 && val <= size) g.grid[i][j].setValue(val); 
				}
			}
		}
	}
	
	public void write() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				String str = "" + g.grid[i][j].getValue();
				if(str.equals("0")) {
					cells[i][j].setText("");
				}
				else cells[i][j].setText(str);
			}
		}
	}
	
	public void create(int size) {
		gridPane = new JPanel();
		g = new Grid(size);
		cells = new JTextField[size][size];
		gridPane.setLayout(new GridLayout(size, size));
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				cells[i][j] = new JTextField();
				final JTextField cell = cells[i][j];
				cell.setHorizontalAlignment(SwingConstants.CENTER);
				cell.setText("");
				if(Math.sqrt(size) % 2 == 0) {
					if((g.grid[i][j].groupid + (int)(i/4) % Math.sqrt(size)) % 2 == 0) {
						cell.setBackground(Color.BLUE);
						cell.setForeground(Color.WHITE);
					}
					else cell.setBackground(Color.RED);
				}
				else {
					if(g.grid[i][j].groupid % 2 == 0) {
						cell.setBackground(Color.BLUE);
						cell.setForeground(Color.WHITE);
					}
					else cell.setBackground(Color.RED);
				}
				gridPane.add(cell);
			}
		}
		getContentPane().add(gridPane, BorderLayout.CENTER);
		setBounds(100, 100, 600, 476);
		setBounds(100, 100, 600, 475);
	}
}
