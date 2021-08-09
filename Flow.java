// Starter code for max flow
package jxs190058;

//import idsa.Graph.Edge;
//import idsa.Graph.Factory;
//import idsa.Graph.GraphAlgorithm;
//import idsa.Graph.Vertex;



import idsa.Graph;
import idsa.Graph.*;

import java.util.*;

/**
 * Author:
 * Jie Su jxs190058
 * PengChao Cai pxc190029
 *
 * Implementing preflow-push algorithm. Calculating residual capacity of an edge on the fly.
 * Residual Capacity  r(u,v) = c(u,v) - f(u,v) + f(v,u)
 */
public class Flow extends Graph.GraphAlgorithm<Flow.FlowVertex> {
    Deque<FlowVertex> queue;
    FlowVertex s;
    FlowVertex t;
    HashMap<Graph.Edge, Integer> capacity;
    HashMap<Graph.Edge, Integer> flow;
    public Flow(Graph g, Graph.Vertex s, Graph.Vertex t, HashMap<Edge, Integer> capacity) {
        super(g, new FlowVertex(null));
        this.s = get(s);
        this.t = get(t);
        this.capacity = capacity;
        this.flow = new HashMap<>();
        queue = new ArrayDeque<>();
        for(Edge edge : capacity.keySet()){

        }
    }

    public static class FlowVertex implements Factory {

        Vertex self;
        int height;
        int excess;
        boolean visited;
        boolean inQueue;

        @Override
        public FlowVertex make(Vertex u) {
            return new FlowVertex(u);
        }

        FlowVertex(Vertex u) {
            this.excess = 0;
            this.self = u;
            this.visited = false;
            this.inQueue = false;
        }
    }

    // Return max flow found. Use either FIFO or Priority queue.

    /**
     * Return max flow found using FIFO queue.
     *
     * @return max flow
     */
    public int preflowPush() {
        if(g.size() == 2){
            int maxflow = 0;
            for(Edge e : g.outEdges(s.self)){
                flow.put(e, capacity.get(e));
                maxflow = capacity.get(e);
            }
            for(Edge e : g.inEdges(s.self)){
                flow.put(e,0);
            }
            return maxflow;
        }
	    initialize();
	    while(!queue.isEmpty()){
	        FlowVertex u = queue.poll();
	        u.inQueue = false;
	        discharge(u);
	        if(u.excess > 0){
	            relable(u);
            }
        }
        return t.excess;
    }

    /**
     * initialize flow, Heights,excess of Vertex, update queue
     */
    private void initialize(){
        for (Edge e : g.getEdgeArray()) {
            flow.put(e, 0);
        }
        initHeights();
        s.height = g.size();
        for (Edge e : g.outEdges(s.self)) {
            int cap = capacity.get(e);
            flow.put(e,cap);//init flow
            s.excess = s.excess-cap;
            FlowVertex u = get(e.toVertex());
            u.excess = u.excess + cap;
            queue.add(u);
            u.inQueue = true;
        }
    }

    /**
     * discharge u: push flow forward to sink Vertex or backward to source Vertex
     *
     * @param u
     */
    private void discharge(FlowVertex u){
        boolean forward = false;
        //forwards
        Vertex uu = u.self;;
        for(Edge edge : g.outEdges(uu)){
            Graph.Vertex v = edge.toVertex();
            int residualCap = capacity(edge) - flow.get(edge);
            if(u.height == get(v).height + 1 && residualCap > 0){
                int delta = Math.min(u.excess, residualCap);
                forward = true;
                flow.put(edge,flow(edge) + delta);
                u.excess = u.excess - delta;
                get(v).excess = get(v).excess + delta;
                if(!get(v).inQueue && !v.equals(s.self) && !v.equals(t.self)){
                    queue.add(get(v));
                    get(v).inQueue = true;
                }
                if(u.excess == 0) return;
            }
        }
        //backwards
        if(!forward){
            for(Edge edge : g.inEdges(uu)){
                Graph.Vertex v = edge.fromVertex();
                if(u.height == get(v).height + 1 && flow(edge) > 0){
                    int delta = Math.min(u.excess, flow(edge));
                    flow.put(edge, flow.get(edge)-delta);
                    u.excess = u.excess - delta;
                    get(v).excess = get(v).excess + delta;
                    if(!get(v).inQueue && !v.equals(s.self) && !v.equals(t.self)){
                        queue.add(get(v));
                        get(v).inQueue = true;
                    }
                    if(u.excess == 0) return;
                }
            }

        }
    }

    /**
     * initialize heights of Vertex by bfs
     */
    private void initHeights() {
        Deque<Vertex> q  = new ArrayDeque<>();
        g.reverseGraph();
        q.offer(t.self);
        t.visited = true;
        int dis = 0;

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i< size; i++) {
                Vertex curV = q.poll();
                FlowVertex curFV = get(curV);
                curFV.height = dis;
                for (Edge e : g.outEdges(curV)) {
                    FlowVertex fromFV = get(e.fromVertex());
                    if (!fromFV.visited) q.offer(fromFV.self);
                    fromFV.visited = true;
                }
            }
            dis++;
        }
        g.reverseGraph();
    }

    /**
     * When no admissible edges and excess of Vertex is non-zero, relabel it
     *
     * @param u
     */
    public void relable(FlowVertex u){
        int minHeight = Integer.MAX_VALUE;
        for(Edge edge : g.outEdges(u.self)){
            if(capacity(edge) - flow.get(edge) > 0){
                minHeight = Math.min(minHeight, get(edge.toVertex()).height);
            }
        }
        for(Edge edge : g.inEdges(u.self)){
            if(flow.get(edge) > 0){
                minHeight = Math.min(minHeight, get(edge.fromVertex()).height);
            }
        }
        u.height = minHeight + 1;
        queue.add(u);
        u.inQueue = true;
    }

    // flow going through edge e
    public int flow(Edge e) {
	return flow.get(e);
    }

    // capacity of edge e
    public int capacity(Edge e) {
	return capacity.get(e);
    }

    /* After maxflow has been computed, this method can be called to
       get the "S"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutS() {
	return null;
    }

    /* After maxflow has been computed, this method can be called to
       get the "T"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutT() {
	return null;
    }

    public static void main(String[] args) {
        Scanner in;
        HashMap<Edge, Integer> capacity = new HashMap<>();
        int[] arr = new int[]{7,15,12,12,4,10,7,10};
        String input = "6 7 1 2 1 1 3 1 2 4 1 3 4 1 3 5 1 4 6 1 5 6 1";
        in = new Scanner(input);
        Graph g = Graph.readDirectedGraph(in);

        for (Vertex u : g) {
            for (Edge e : g.outEdges(u)) {
                capacity.put(e, arr[e.getName()]);
            }
        }
        Flow f = new Flow(g, g.getVertex(1), g.getVertex(6), capacity);
        int value = f.preflowPush();
        System.out.println("maxflow" + value);
            for (Vertex u : g) {
                System.out.print(u + " : ");
                for(Edge e: g.outEdges(u)) {
                    System.out.print(e + ":" + f.flow(e) + "/" + f.capacity(e) + " | ");
                }
                System.out.println();
            }
    }
}
