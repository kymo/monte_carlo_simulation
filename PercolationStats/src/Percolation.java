//percolation system definition
    public class Percolation{
        //   g[i][j] = -1  empty
        //              0  open
        //              1  full
        public int M;
        public int g[][];
        int color[];
        //  root[i] the root if node i
        private int root[];
        //  t[i] the deepth of tree which i belongs to
        private int t[];
        
        public int getSize()
        {
        	return M;
        }
        //  find operation of union-found
        public int find(int j)
        {
            if(root[j] == j)
                return j;
            else
                return find(root[j]);
        }
        
        //  union operation
        public void union(int i, int j)
        {
            int s1 = find(i);
            int s2 = find(j);
           // System.out.println(s1 + " " + s2);
            int s1i = (s1 - 1) / M + 1;
            int s1j = (s1 - 1) % M + 1;
            int s2i = (s2 - 1) / M + 1;
            int s2j = (s2 - 1) % M + 1;
            /*
            System.out.println("(" + s1i + "," + s1j + "),(" + s2i + "," + s2j + ")");
            System.out.println(g[s1i][s1j] + " " + g[s2i][s2j]);
            */
            if(g[(s1 - 1) / M + 1][(s1 - 1) % M + 1] != g[(s2 - 1) / M + 1][(s2 - 1) % M + 1])
            {
                for(int k = 1; k <= M * M; k ++)
                {
                    int rot = find(k);
                    if(rot == s1 || rot == s2)
                    {
                    	//System.out.println("sdf'");
                        g[(k - 1) / M + 1][(k - 1) % M + 1] =  1;
                    }
                }
            }
            if(s1 == s2)
                return;
            else
            {
                if(t[s1] >= t[s2])
                {
                    if(t[s1] == t[s2])
                        t[s1] ++;
                    root[s2] = s1;
                }
                else
                    root[s1] = s2;
            }
        }
        
        //init g[][] -1
        public Percolation(int N)
        {
            M = N;
            g = new int[N + 1][N + 1];
            root = new int[N * N + 1];
            t = new int[N * N + 1];
            for(int j = 1; j <= N;j ++)
            {
                for(int k = 1; k <= N; k ++)
                {
                	int rot = (j - 1) * M + k;
                	root[rot] = rot;
                	t[rot] = 1;
                    g[j][k] = -1;
                }
            }
        }
        
        public void open(int i, int j)
        {
            g[i][j] = 0;
            if(i == 1)
            	g[i][j] = 1;
            boolean tag = true;
            for(int k = 1;k <= i; k ++)
            	if(g[k][j] != 1)
            		tag = false;
            if(tag)
            	g[i][j] = 1;
            int dx[] = {-1, 0, 1, 0};
            int dy[] = {0, 1, 0, -1};
            boolean isRight = false;
            int tempRoot[] = new int[4];
            int st = 0;
            for(int k = 0; k < 4; k ++)
            {
                int _i = i + dx[k];
                int _j = j + dy[k];
               
                if(_i >= 1 && _i <= M && _j >= 1 && _j <= M)
                {
                    if(g[_i][_j] >= 0)
                        union((i - 1) * M + j, (_i - 1) * M + _j);
                }
            }
        }
        
        public boolean isOpen(int i, int j)
        {
            return g[i][j] == 0;
        }
        public boolean isFull(int i, int j)
        {
            return g[i][j] == 1;
        }
        public boolean percolates()
        {
            for(int j = 1; j <= M; j ++)
                if(g[M][j] == 1)
                    return true;
            return false;
        }
    }
    
