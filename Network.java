import java.util.*;

/**
 * A simple Network class to build a network
 *
 * @author Vaastav Arora, arora74@purdue.edu
 */
public class Network {

   
    /**
     * computerConnections represents list of all inter-computer edges
     * Each edge is an Integer[] of size 3
     * edge[0] = source computer index ( Not IP, it's the Index !)
     * edge[1] = destination computer index ( Not IP, it's the Index !)
     * edge[2] = latency/edge weight
     */
    public LinkedList<Integer[]> computerConnections;
    /**
     * Adjacency List representing computer graph
     */
    private LinkedList<LinkedList<Integer>> computerGraph;
    /**
     * LinkedList of clusters where each cluster is represented as a LinkedList of computer IP addresses
     */
    private LinkedList<LinkedList<Integer>> cluster;
    /**
     * Adjacency List representing router graph
     */
    private LinkedList<LinkedList<Integer[]>> routerGraph;

    private Scanner s; // Scanner to read Stdin input
    
    public int array[];

    public boolean explore[];

    public Router[] routers;


    /**
     * Default Network constructor, initializes data structures
     * @param s Provided Scanner to be used throughout program
     */
    public Network(Scanner s) {
        this.s = s;
        this.computerConnections = new LinkedList<>();
        this.cluster = new LinkedList<>();
        this.routerGraph = new LinkedList<>();
        this.computerGraph = new LinkedList<>();
        this.array = null;
        this.explore = null;
        this.routers = null;
    }

    /**
     * Method to parse Stdin input and generate inter-computer edges
     * Edges are stored within computerConnections
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of comp 1] [IP address of comp 2] [latency of connection]
     */
    public void buildComputerNetwork() {

        int num_edges = this.s.nextInt();

        this.array = new int[num_edges];

        for(int i = 0; i < array.length; i++) {
            this.array[i] = -1;
        }

        int counter = 0;

        while(counter < num_edges) {
            int array_index = 0;
            Integer[] edge = new Integer[3];
            int vertex_1 = this.s.nextInt();
            int vertex_2 = this.s.nextInt();
            int vertex_1_index = 0;
            int vertex_2_index = 0;
            boolean v1 = false;
            boolean v2 = false;

            while (array_index != num_edges) {
                if (this.array[array_index] == vertex_1) {
                    v1 = true;
                    vertex_1_index = array_index;
                } else if (this.array[array_index] == vertex_2) {
                    v2 = true;
                    vertex_2_index = array_index;
                } else if (this.array[array_index] == -1) {
                    if (!v1 && !v2) {
                        this.array[array_index] = vertex_1;
                        vertex_1_index = array_index;
                        this.array[array_index + 1] = vertex_2;
                        vertex_2_index = array_index + 1;
                    } else if (v1 && v2) {
                        break;
                    }
                    else if (v1) {
                        this.array[array_index] = vertex_2;
                        vertex_2_index = array_index;
                    } else if (v2) {
                        this.array[array_index] = vertex_1;
                        vertex_1_index = array_index;
                    }
                    break;
                }
                array_index++;
            }
            edge[0] = vertex_1_index;
            edge[1] = vertex_2_index;
            edge[2] = s.nextInt();
            this.computerConnections.add(edge);
            counter++;
        }
    }

    /**
     * Method to generate clusters from computer graph
     * Throws Exception when cannot create required clusters
     * @param k number of clusters to be created
     */
    public void buildCluster(int k) throws Exception {

        if (k < 1 || k > getArray_size()) {
            throw new Exception("Cannot create clusters");
        }

        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < getArray_size(); i++) {
            computerGraph.add(list);
            list = new LinkedList<>();
        }

        for (int i = 0; i < k; i++) {
            cluster.add(list);
            list = new LinkedList<>();
        }

        Integer[][] sorted_list = new Integer[computerConnections.size()][3];

        for (int i = 0; i < computerConnections.size(); i++) {
            sorted_list[i] = computerConnections.get(i);
        }

        for (int i = 0; i < sorted_list.length; i++) {
            int min = i;
            for (int j = i + 1; j < sorted_list.length; j++) {
                if (sorted_list[j][2] < sorted_list[min][2]) {
                    min = j;
                }
            }
            Integer[] temp = sorted_list[i];
            sorted_list[i] = sorted_list[min];
            sorted_list[min] = temp;
        }

        UnionFind unionFind = new UnionFind(getArray_size());

        Integer[] edge;
        int counter = 0;
        while (unionFind.components() > k) {
            edge = sorted_list[counter];
            if (!unionFind.connected(edge[0], edge[1])) {
                unionFind.union(edge[0], edge[1]);
                computerGraph.get(edge[0]).add(edge[1]);
                computerGraph.get(edge[1]).add(edge[0]);
            }
            counter++;
        }

        explore = new boolean[getArray_size()];

        int start_index = 0;

        while (this.computerGraph.get(start_index).size() == 0) {
            explore[start_index] = true;
            start_index++;
        }

        explore[start_index] = true;

        DFS(start_index, 0);

        int cluster_num = 1;

        while (cluster_num < k) {
            counter = 0;
            while (explore[counter]) {
                counter++;
            }
            explore[counter] = true;
            DFS(counter, cluster_num);
            cluster_num++;
        }

        counter = 0;

        this.routers = new Router[k];

        for (int i = 0; i < routers.length; i++) {
            routers[i] = new Router();
        }

        while (counter < this.cluster.size()) {
            int IP;
            int max_IP = 0;
            for (int i = 0; i < this.cluster.get(counter).size(); i++) {
                IP = this.cluster.get(counter).get(i);
                if (IP > max_IP) {
                    max_IP = IP;
                }
                routers[counter].addComp(IP);
            }
            this.routers[counter].setIPPrefix(max_IP);
            counter++;
        }
    }

    /**
     * Method to parse Stdin input and generate inter-router edges
     * Graph is stored within routerGraph as an adjacency list
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of Router 1] [IP address of Router 2] [latency of connection]
     */
    public void connectCluster() {

        for (int i = 0; i < cluster.size(); i++) {
            LinkedList<Integer[]> router = new LinkedList<>();
            routerGraph.add(router);
        }

        int num_edges = this.s.nextInt();

        int counter = 0;

        while(counter < num_edges) {
            Integer[] connection = new Integer[2];
            Integer[] connection2 = new Integer[2];
            int router_1 = this.s.nextInt();
            int router_2 = this.s.nextInt();
            int edge = this.s.nextInt();
            int router_1_index = 0;
            int router_2_index = 0;

            for (int i = 0; i < this.routers.length; i++) {
                if (this.routers[i].getIPPrefix() == router_1) {
                    router_1_index = i;
                }
                else if (this.routers[i].getIPPrefix() == router_2) {
                    router_2_index = i;
                }
            }

            connection[0] = router_2_index;
            connection[1] = edge;

            this.routerGraph.get(router_1_index).add(connection);

            connection2[0] = router_1_index;
            connection2[1] = edge;

            this.routerGraph.get(router_2_index).add(connection2);

            counter++;
        }

        s.nextLine();
    }

    /**
     * Method to take a traversal request and find the shortest path for that traversal
     * Traversal request passed in through parameter test
     * Format of Request => [IP address of Source Router].[IP address of Source Computer] [IP address of Destination Router].[IP address of Destination Computer]
     * Eg. 123.456 128.192
     *  123 = IP address of Source Router
     *  456 = IP address of Source Computer
     *  128 = IP address of Destination Router
     *  192 = IP address of Destination Computer
     * @param test String containing traversal input
     * @return Shortest traversal distance between Source and Destination Computer
     */
    public int traversNetwork(String test) {

        String source_string;
        String s_comp_string;
        String destination_string;
        String d_comp_string;
        int source;
        int s_comp;
        int destination;
        int d_comp;
        int end_string = 0;
        int start_string = 0;
        int total_length;

        while (test.charAt(end_string) != '.') {
            end_string++;
        }

        source_string = test.substring(0, end_string);
        start_string = end_string + 1;

        while (test.charAt(end_string) != ' ') {
            end_string++;
        }

        s_comp_string = test.substring(start_string, end_string);
        start_string = end_string + 1;

        while (test.charAt(end_string) != '.') {
            end_string++;
        }

        destination_string = test.substring(start_string, end_string);
        start_string = end_string + 1;

        d_comp_string = test.substring(start_string);

        s_comp = Integer.parseInt(s_comp_string);
        d_comp = Integer.parseInt(d_comp_string);

        source = Integer.parseInt(source_string);
        destination = Integer.parseInt(destination_string);

        if (source == destination) {
            return 0;
        }

        total_length = shortestPath(source, destination, s_comp, d_comp);

        if (total_length == -1) {
            return -1;
        }

        return total_length;
        
    }

    public int getArray_size() {
        int counter = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (this.array[i] != -1) {
                counter += 1;
            }
        }
        return counter;
    }

    public int shortestPath(int begin, int end, int s_comp, int d_comp) {
        int matrix[][] = new int[routerGraph.size()][routerGraph.size()];

        for (int i = 0; i < routerGraph.size(); i++) {
            for (int j = 0; j < routerGraph.get(i).size(); j++) {
                matrix[i][routerGraph.get(i).get(j)[0]] = routerGraph.get(i).get(j)[1];
            }
        }

        int dist[] = new int[this.routerGraph.size()];
        boolean in_path[] = new boolean[this.routerGraph.size()];

        int begin_index = -1;
        int end_index = -1;

        for (int i = 0; i < this.routers.length; i++) {
            dist[i] = Integer.MAX_VALUE;
            in_path[i] = false;
            if (this.routers[i].getIPPrefix() == begin) {
                begin_index = i;
                if (!routers[i].checkComp(s_comp)) {
                    return -1;
                }
            } else if (this.routers[i].getIPPrefix() == end) {
                end_index = i;
                if (!routers[i].checkComp(d_comp)) {
                    return -1;
                }
            }
        }

        if (begin_index == -1 || end_index == -1) {
            return -1;
        }

        dist[begin_index] = 0;

        for (int i = 0; i < dist.length - 1; i++) {
            int u = minDistance(dist, in_path);
            in_path[u] = true;
            for (int j = 0; j < dist.length; j++) {
                if (!in_path[j] && matrix[u][j] != 0 && dist[u] != Integer.MAX_VALUE &&
                        (dist[u] + matrix[u][j]) < dist[j]) {
                    dist[j] = dist[u] + matrix[u][j];
                }
            }
        }

        return dist[end_index];
    }

    public int minDistance(int dist[], boolean in_path[]) {
        int min = Integer.MAX_VALUE;
        int min_index = -1;

        for (int i = 0; i < dist.length; i++) {
            if (!in_path[i] && dist[i] <= min) {
                min = dist[i];
                min_index = i;
            }
        }

        return min_index;
    }

    public void DFS(int index, int cluster_num) {

        this.cluster.get(cluster_num).add(array[index]);

        if (computerGraph.get(index).size() == 0) {
            return;
        }

        int counter = 0;
        while (counter < computerGraph.get(index).size()) {
            int vertex = computerGraph.get(index).get(counter);
            if (!explore[vertex]) {
                explore[vertex] = true;
                DFS(vertex, cluster_num);
            }
            counter++;
        }
    }

    private void printGraph(LinkedList< LinkedList<Integer[]>> graph){
        for (var i:graph) {
            for (var j: i){
                System.out.print(j[0]+" "+j[1]);
            }
            System.out.println();
        }
    }

    public LinkedList<Integer[]> getComputerConnections() {
        return computerConnections;
    }

    public LinkedList<LinkedList<Integer>> getComputerGraph() {
        return computerGraph;
    }

    public LinkedList<LinkedList<Integer>> getCluster() {
        return cluster;
    }

    public LinkedList<LinkedList<Integer[]>> getRouterGraph() {
        return routerGraph;
    }

}
