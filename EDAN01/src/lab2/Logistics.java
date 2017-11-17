package lab2;

import org.jacop.constraints.IfThen;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.Subcircuit;
import org.jacop.constraints.XeqC;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XneqC;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleMatrixSelect;

public class Logistics {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		new Logistics().example(3);

		long end = System.currentTimeMillis();
		System.out.println("Execution time: " + (end - start) + " ms");
	}

	/**
	 * The examples from the lab instructions
	 * 
	 * @param i
	 *            The example number
	 */
	private void example(int i) {
		switch (i) {
		case 1:
			int graph_size1 = 6;
			int start1 = 1;
			int n_dests1 = 1;
			int[] dest1 = { 6 };
			int n_edges1 = 7;
			int[] from1 = { 1, 1, 2, 2, 3, 4, 4 };
			int[] to1 = { 2, 3, 3, 4, 5, 5, 6 };
			int[] cost1 = { 4, 2, 5, 10, 3, 4, 11 };
			solve(graph_size1, start1, n_dests1, dest1, n_edges1, from1, to1, cost1);
			break;
		case 2:
			int graph_size2 = 6;
			int start2 = 1;
			int n_dests2 = 2;
			int[] dest2 = { 5, 6 };
			int n_edges2 = 7;
			int[] from2 = { 1, 1, 2, 2, 3, 4, 4 };
			int[] to2 = { 2, 3, 3, 4, 5, 5, 6 };
			int[] cost2 = { 4, 2, 5, 10, 3, 4, 11 };
			solve(graph_size2, start2, n_dests2, dest2, n_edges2, from2, to2, cost2);
			break;
		case 3:
			int graph_size3 = 6;
			int start3 = 1;
			int n_dests3 = 2;
			int[] dest3 = { 5, 6 };
			int n_edges3 = 9;
			int[] from3 = { 1, 1, 1, 2, 2, 3, 3, 3, 4 };
			int[] to3 = { 2, 3, 4, 3, 5, 4, 5, 6, 6 };
			int[] cost3 = { 6, 1, 5, 5, 3, 5, 6, 4, 2 };
			solve(graph_size3, start3, n_dests3, dest3, n_edges3, from3, to3, cost3);
			break;
		default:
			System.out.println("No such example!");
		}
	}

	private void solve(int graph_size, int start, int n_dests, int[] dest, int n_edges, int[] from, int[] to,
			int[] cost) {
		// Create store
		Store store = new Store();

		// Initialize the edges
		IntVar[][] edge = new IntVar[graph_size][graph_size];
		for (int i = 0; i < graph_size; i++) {
			for (int j = 0; j < graph_size; j++) {
				edge[i][j] = new IntVar(store, 0, 1);
			}
		}

		// Initialize weight matrix
		int[][] weight = init_weights(graph_size, n_edges, from, to, cost);

		// Initialize the graphs for each destination
		IntVar[][] graph = new IntVar[n_dests][graph_size];
		for (int i = 0; i < n_dests; i++) {
			for (int j = 1; j <= graph_size; j++) {
				graph[i][j - 1] = new IntVar(store, j, j);
			}

		}

		// Destination nodes are connected to start
		for (int i = 0; i < n_dests; i++) {
			graph[i][dest[i] - 1].addDom(start, start);
		}

		// Add the connected nodes to the domain
		for (int i = 0; i < n_dests; i++) {
			for (int j = 0; j < graph_size; j++) {
				for (int k = 0; k < graph_size; k++) {
					if (weight[j][k] > 0) {
						graph[i][j].addDom(k + 1, k + 1);
					}
				}
			}
		}

		for (int i = 0; i < n_dests; i++) {
			// Must leave start node
			store.impose(new XneqC(graph[i][start - 1], start));
			// Destination node connected to start
			store.impose(new XeqC(graph[i][dest[i] - 1], start));
			store.impose(new Subcircuit(graph[i]));
		}

		IntVar[] sub_cost = new IntVar[graph_size];
		for (int i = 0; i < graph_size; i++) {
			sub_cost[i] = new IntVar(store, 0, IntDomain.MaxInt);
		}

		IntVar[] nodes = new IntVar[graph_size];
		for (int i = 1; i <= graph_size; i++) {
			nodes[i - 1] = new IntVar(store, i, i);
		}

		// Get the visited edges cost
		for (int i = 0; i < n_dests; i++) {
			for (int j = 0; j < graph_size; j++) {
				for (int k = 0; k < graph_size; k++) {
					PrimitiveConstraint c1 = new XeqY(graph[i][j], nodes[k]);
					PrimitiveConstraint c2 = new XeqC(edge[j][k], 1);
					store.impose(new IfThen(c1, c2));
				}
			}
		}

		// The cost
		IntVar total_cost = new IntVar(store, 0, IntDomain.MaxInt);
		store.impose(new LinearInt(store, flatten_matrix(edge), flatten_matrix(weight), "==", total_cost));

		// Search for solution
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(graph, null, new IndomainMin<IntVar>());
		boolean result = search.labeling(store, select, total_cost);
		if (result) {
			System.out.println("Cost: " + total_cost.value());
			print(edge);
		} else {
			System.out.println("No solution found!");
		}
	}

	/**
	 * Prints the edges chosen in the format (Node from) -> (Node to)
	 * 
	 * @param edge
	 *            The matrix
	 */
	private void print(IntVar[][] edge) {
		for (int i = 0; i < edge.length; i++) {
			for (int j = 0; j < edge[0].length; j++) {
				if (edge[i][j].value() == 1) {
					System.out.println((i + 1) + " -> " + (j + 1));
				}
			}
		}
	}

	/**
	 * Flattens the matrix to an array
	 * 
	 * @param m
	 *            The matrix
	 * @return An array
	 */
	private int[] flatten_matrix(int[][] m) {
		int[] res = new int[m.length * m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				res[i * m[0].length + j] = m[i][j];
			}
		}
		return res;
	}

	/**
	 * Flattens the matrix to an array
	 * 
	 * @param m
	 *            The matrix
	 * @return An array
	 */
	private IntVar[] flatten_matrix(IntVar[][] m) {
		IntVar[] res = new IntVar[m.length * m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				res[i * m[0].length + j] = m[i][j];
			}
		}
		return res;
	}

	/**
	 * Initialize the weight matrix
	 * 
	 * @param graph_size
	 *            The graph size
	 * @param from
	 *            The nodes from
	 * @param to
	 *            The nodes corresponding to the from node
	 * @param cost
	 *            The weight for the path from node from to node to
	 * @return An int matrix
	 */
	private int[][] init_weights(int graph_size, int n_edges, int[] from, int[] to, int[] cost) {
		int[][] weight = new int[graph_size][graph_size];
		for (int i = 0; i < n_edges; i++) {
			weight[from[i] - 1][to[i] - 1] = cost[i];
			weight[to[i] - 1][from[i] - 1] = cost[i];
		}
		return weight;
	}
}
