
/**
 * A simple Union-Find class to build a network
 *
 * @author Vaastav Arora, arora74@purdue.edu
 */

public class UnionFind {
    private int[] parent;
    private int[] rank;
    private int count;

    /**
     * Constructor for UnionFind class
     * @param n number of components
     */
    public UnionFind(int n) {
        if ( n< 0 ) throw new IllegalArgumentException();
        count = n;
        parent = new int[n];
        rank = new int[n];
        for(int i=0;i<n;i++){
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Finds parent component of required component
     * @param p component queried
     * @return parent compoenent of queried component
     */
    public int find(int p) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }
        return p;
    }

    /**
     * Combines two components, giving them the same parent component
     * @param p Component one to be combined
     * @param q Component two to be combined
     */
    public void union(int p,int q){
        int rootP = find(p);
        int rootQ = find(q);
        if( rootP == rootQ) return;

        if(rank[rootP] < rank[rootQ]) parent[rootP] = rootQ;
        else if(rank[rootP] > rank[rootQ]) parent[rootQ] = rootP;
        else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }

        count--;
    }

    /**
     * Checks if two components are connected
     * @param p Componenet one
     * @param q Component two
     * @return returns true if connected
     */
    public boolean connected(int p, int q){
        return find(p) == find(q);
    }

    /**
     * Check number of components in Union Find instance
     * @return number of components in current Union Find instance
     */
    public int components(){
        return count;
    }
}
