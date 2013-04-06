import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PercolationStats extends JFrame implements ActionListener {
    private DrawComponent cGameDc;//the game draw panel
    private double X[];
    int t,n;
    boolean stop = false;
    double u, std;
    Percolation currentPercolation;
    boolean isGameOver = true;
    int sleepTime = 0;
    private JButton m_bGameStart = new JButton("start");
    private JLabel m_lTime = new JLabel("Run Times");
    private JLabel m_lSleep = new JLabel("Sleep Time(ms)");
    private JLabel m_lSize = new JLabel("Size");
    private JTextField m_tSleep = new JTextField();
    private JTextField m_tTime = new JTextField();
    private JTextField m_tSize= new JTextField();
    private JLabel m_lCurrent = new JLabel("");
    private JLabel m_lTotal = new JLabel("");
    public void percolationStart(int nt, int tt, int st)
    {
    	currentPercolation = new Percolation(nt);
    	Timer timer = new Timer();
    	t = tt;
    	n = nt;
    	sleepTime = st;
    	timer.schedule(new TimerTask()
    	{
    		public void run()
    		{
    			if(! isGameOver)
    			{
			    	for(int i = 0; i < t; i ++)
			        {
			    		if(! stop)
			    		{
				            Percolation pl = new Percolation(n);
				            currentPercolation = pl;
				            while(! pl.percolates())
				            {
			                	cGameDc.repaint();
				                int first = (int)(Math.random() * n) + 1;
				                int second = (int)(Math.random() * n) + 1;
			                	try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				                if(pl.g[first][second] >= 0)
				                    continue;
				                else
				                {
				                	pl.open(first, second);
				                }
			                	cGameDc.repaint();
				            }
				            
				            int cnt = 0;
				            for(int j = 1; j <= n; j ++)
				                for(int k = 1; k <= n; k ++)
				                    if(pl.g[j][k] == 1)
				                        cnt ++;
				            X[i] = (double)cnt / (n * n);
							String html = "<html><body>" +
									"Run times:<br/> " + String.valueOf(i + 1)+ "<br/>" + 
									"propability:<br/> " + String.valueOf(X[i]) + "<br/>" +
									"Target:" + String.valueOf(cnt) + "<br/>" +
									"</body></html>";
							System.out.println(html);
							m_lCurrent.setText(html);
							double cutAns = 0.0;
							for(int k = 0; k < i + 1; k ++)
								cutAns += X[k];
							double us = cutAns / ( i + 1);
							double totalAns = 0.0;
							for(int j = 0; j < i; j ++)
								totalAns += (X[j] - us) * (X[j] - us);
							String htmlTotal = "<html><body>" + 
									"mean P: <br/>" + String.valueOf(us) + "<br/>"  +
									"stddev: <br/>" + String.valueOf(totalAns / (i)) + "<br/>" +
									"</body></html>";
				        	System.out.println("over");
							m_lTotal.setText(htmlTotal);
				            //stop = true;
							m_bGameStart.setEnabled(true);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    	}
		    		}   
			    	u = mean();
			        std = stddev();
			        isGameOver = true;
			        System.out.println("mean                    = " + mean());
			        System.out.println("stddev                  = " + stddev());
			        System.out.println("95% confidence interval = " + confidenceLo() + ", " + confidenceHi());
		        }
    		}
    	},0 , 5 * 340);

        
    }
    //constute function
    public PercolationStats()
    {   
		initUI();
    }
    
    public void initUI()
    {
    	getContentPane().setLayout(null);
    	JPanel outer = new JPanel();
    	getContentPane().add(outer);
    	outer.setBounds(4, 4, 560, 560);
    	//outer.setBorder(BorderFactory.createTitledBorder("DC"));
    	outer.setLayout(null);
    	cGameDc = new DrawComponent(this);
    	cGameDc.setBounds(10,10,400,400);
    	outer.add(cGameDc);
    	JPanel right = new JPanel();
    	m_bGameStart.setBounds(10,10,120,30);
    	m_lTime.setBounds(10,45,120,20);
        m_tTime.setBounds(10, 65, 120,20);
        m_lSize.setBounds(10, 85, 120, 20);
        m_tSize.setBounds(10, 105, 120, 20);
        m_lSleep.setBounds(10, 125, 120,20);
        m_tSleep.setBounds(10, 145,120,20);
        m_lCurrent.setBounds(10, 165, 120, 120);
        m_lTotal.setBounds(10, 285, 120, 120);
        m_lCurrent.setBorder(BorderFactory.createTitledBorder("Current Data"));
        m_lTotal.setBorder(BorderFactory.createTitledBorder("Total Data"));
        
        right.add(m_bGameStart);
        right.add(m_lTime);
        right.add(m_tTime);
        right.add(m_lSize);
        right.add(m_tSize);
        right.add(m_lSleep);
        right.add(m_tSleep);
        right.add(m_lTotal);
        right.add(m_lCurrent);
        
    	right.setBounds(420,0,140,420);
    	//right.setBorder(BorderFactory.createTitledBorder("DCs"));
    	right.setLayout(null);
    	outer.add(right);
    	m_bGameStart.addActionListener((ActionListener) this);
    	setVisible(true);
    	pack();
    	setSize(580,500);
    	setTitle("Monte Carlo Simulation");
    	this.addWindowListener(new WindowAdapter()
        {
           public void windowClosing(WindowEvent e)
           {
        	    isGameOver = true;
                System.exit(0);
            }
        });
    }
    public double mean()
    {
        double ans = 0.0;
        for(int j = 0; j < t; j ++)
            ans += X[j];
        return ans / t;
    }
    public double stddev()
    {
        double ans = 0.0;
        double u = mean();
        for(int j = 0; j < t; j ++)
            ans += (X[j] - u) * (X[j] - u);
        return Math.sqrt(ans / (t - 1));
    }
    public double confidenceLo()
    {
        double left = u - 1.96 * Math.sqrt(std) / Math.sqrt(t);
        return left;
    }
    public double confidenceHi()
    {
        double right = u + 1.96 * Math.sqrt(std) / Math.sqrt(t);
        return right;   
    }
    public static void main(String args[])
    {
        PercolationStats ps = new PercolationStats();
        
    }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == m_bGameStart){
			if(( m_tTime.getText() == "" || m_tSize.getText() == "" || m_tSleep.getText() == ""))
			{
				int i = JOptionPane.showConfirmDialog(null, "fuck you", "es)", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try{
					int RT = Integer.parseInt(m_tTime.getText());
					int ST = Integer.parseInt(m_tSize.getText());
					n = ST;
					t = RT;
					currentPercolation = new Percolation(ST);
			        if(ST <= 0 || RT <= 0)
			            throw new java.lang.IllegalArgumentException();
			        X = new double[RT];
			        sleepTime = Integer.parseInt(m_tSleep.getText());
			        System.out.println(RT + ST + " " + sleepTime);
					percolationStart(RT, ST, sleepTime);
					isGameOver = false;
					m_bGameStart.setEnabled(false);
						
				}catch(Exception e)
				{
					int i = JOptionPane.showConfirmDialog(null, "fuck you", "es)", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}

class DrawComponent extends JComponent{
  //the draw panel size
	PercolationStats cutPercolation;
	private final int DRAWPANEL_WEIGHT = 400;
	int N,width;
	private final int DRAWPANEL_HEIGHT = 400;
    private final int BLOCK_SIZE = 20;
    public DrawComponent(PercolationStats pl)
    {
    	cutPercolation = pl;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //draw the background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, DRAWPANEL_WEIGHT, DRAWPANEL_HEIGHT);
        
        if(! cutPercolation.isGameOver)
        {
            N = cutPercolation.currentPercolation.getSize();
            width = 400 / N;
	        for(int j = 1; j <= N; j ++)
	        {
	        	for(int k = 1; k <= N; k ++)
	        	{
	        		//System.out.println(cutPercolation.currentPercolation.g[j][k]);
	        		if(cutPercolation.currentPercolation.g[j][k] == -1)
	        			g2.setColor(Color.black);
	        		else if(cutPercolation.currentPercolation.g[j][k] == 0)
	        			g2.setColor(Color.white);
	        		else
	        			g2.setColor(new Color(0, 128, 128));
	        		int y = (j - 1) * width;
	        		int x = (k - 1) * width;
	        		g2.fillRect(x, y, x + width, y + width);
	        		
	        		for(int i = 0; i <= N; i ++)
	                {
	                	int xt = width * i;
	                	//System.out.println("sfd");
	                	g2.setColor(Color.gray);
	                	g2.drawLine(xt, 0, xt, 400);
	                	g2.drawLine(0, xt, 400, xt);
	                }
	        	}
	        }
        }
    }
}
