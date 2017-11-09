package lab2;

import org.jacop.constraints.IfThen;
import org.jacop.constraints.IfThenElse;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.XeqC;
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

		// Initialize path matrix. 0 for no path and 1 for path
		IntVar[][] path = new IntVar[graph_size][graph_size];
		for (int i = 0; i < graph_size; i++) {
			for (int j = 0; j < graph_size; j++) {
				path[i][j] = new IntVar(store, 0, 1);
			}
		}

		// Initialize weight matrix
		int[][] weight = init_weights(graph_size, from, to, cost);
		// If weight is 0 then there is no path
		for (int i = 0; i < graph_size; i++) {
			for (int j = 0; j < graph_size; j++) {
				if (weight[i][j] == 0) {
					store.impose(new XeqC(path[i][j], 0));
				}
			}
		}

		// No need to visit start node
		store.impose(new SumInt(store, column(path, start - 1), "==", new IntVar(store, 0, 0)));

		// Start at the start node
		store.impose(new SumInt(store, path[start - 1], ">=", new IntVar(store, 1, 1)));

		// Must visit the destination nodes
		for (int i = 0; i < n_dests; i++) {
			store.impose(new SumInt(store, column(path, dest[i] - 1), "==", new IntVar(store, 1, 1)));
		}

		// Each node can only be visited once
		for (int i = 0; i < graph_size; i++) {
			store.impose(new SumInt(store, column(path, i), "<=", new IntVar(store, 1, 1)));
		}

		// Can only leave nodes which have been visited
		for (int i = 0; i < graph_size; i++) {
			if (i != start - 1) {
				PrimitiveConstraint c1 = new SumInt(store, column(path, i), "==", new IntVar(store, 1, 1));
				PrimitiveConstraint c2 = new SumInt(store, path[i], ">=", new IntVar(store, 0, 0));
				PrimitiveConstraint c3 = new SumInt(store, path[i], "==", new IntVar(store, 0, 0));
				store.impose(new IfThenElse(c1, c2, c3));
			}
		}

		// Can not return to a node traveled from
		for (int i = 0; i < graph_size; i++) {
			for (int j = 0; j < graph_size; j++) {
				PrimitiveConstraint c1 = new XeqC(path[i][j], 1);
				PrimitiveConstraint c2 = new XeqC(path[j][i], 0);
				store.impose(new IfThen(c1, c2));
			}
		}

		// The cost
		IntVar output_cost = new IntVar(store, 0, IntDomain.MaxInt);
		store.impose(new LinearInt(store, flatten_matrix(path), flatten_matrix(weight), "==", output_cost));

		// Search for solution
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(path, null, new IndomainMin<IntVar>());
		boolean result = search.labeling(store, select, output_cost);
		if (result) {
			System.out.println("Cost: " + output_cost.value());
			print(path);
		} else {
			System.out.println("No solution found!");
		}
	}

	/**
	 * Prints the path chosen in the format (Node from) -> (Node to)
	 * 
	 * @param path
	 *            The matrix
	 */
	private void print(IntVar[][] path) {
		for (int i = 0; i < path.length; i++) {
			for (int j = 0; j < path[0].length; j++) {
				if (path[i][j].value() == 1) {
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
	 * Get column i of an IntVar matrix
	 * 
	 * @param path
	 *            The matrix
	 * @param i
	 *            The column
	 * @return IntVar array
	 */
	private IntVar[] column(IntVar[][] path, int i) {
		IntVar[] col = new IntVar[path[i].length];
		for (int j = 0; j < col.length; j++) {
			col[j] = path[j][i];
		}
		return col;
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
	private int[][] init_weights(int graph_size, int[] from, int[] to, int[] cost) {
		int[][] weight = new int[graph_size][graph_size];
		for (int i = 0; i < from.length; i++) {
			weight[from[i] - 1][to[i] - 1] = cost[i];
			weight[to[i] - 1][from[i] - 1] = cost[i];
		}
		return weight;
	}
}
