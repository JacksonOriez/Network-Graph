
import java.util.HashSet;

/**
 * A simple Router class
 *
 * @author Vaastav Arora, arora74@purdue.edu
 */

public class Router {

    private int IPPrefix; // IP address of the Router
    private HashSet<Integer> computers; // Computers withing the Router's cluster

    /**
     * Router constuctor
     * @param IPPrefix IP address to be assigned to the router
     */
    public Router(int IPPrefix){

        this.IPPrefix = IPPrefix;
        computers = new HashSet<>();

    }

    /**
     * Default Router constructor
     */

    public Router(){

        this.IPPrefix = -1;
        computers = new HashSet<>();

    }

    /**
     * Getter for IP address of Router
     * @return IP address of ROuter
     */
    public int getIPPrefix() {
        return IPPrefix;
    }

    /**
     * Setter for IP address of Router
     * @param IPPrefix New IP address of Router
     */
    public void setIPPrefix(int IPPrefix) {
        this.IPPrefix = IPPrefix;
    }

    /**
     * Getter for set of computers within Router's cluster
     * @return computers within router
     */
    public HashSet<Integer> getComputers() {
        return computers;
    }

    /**
     * Adds a computer to the Router's cluster
     * @param compIP IP address of the computer to be added
     */
    public void addComp(int compIP) {
        computers.add(compIP);
    }

    /**
     * Checks if computer with IP address compIP exists within cluster
     * @param compIP IP address of computer to check
     * @return True if exists, false if it doens't
     */
    public boolean checkComp(int compIP){
        return computers.contains(compIP);
    }
}
