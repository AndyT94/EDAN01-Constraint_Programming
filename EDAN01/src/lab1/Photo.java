package lab1;

import java.util.Arrays;

import org.jacop.constraints.Alldifferent;
import org.jacop.constraints.Distance;
import org.jacop.constraints.Eq;
import org.jacop.constraints.IfThenElse;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.XeqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class Photo {
	public static void main(String[] args) {
		new Photo().example(3);
	}

	private void example(int i) {
		switch (i) {
		case 1:
			int n1 = 9;
			int n_prefs1 = 17;
			int[][] prefs = { { 1, 3 }, { 1, 5 }, { 1, 8 }, { 2, 5 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 5 },
					{ 5, 6 }, { 5, 1 }, { 6, 1 }, { 6, 9 }, { 7, 3 }, { 7, 8 }, { 8, 9 }, { 8, 7 } };
			solve(n1, n_prefs1, prefs);
			break;
		case 2:
			int n2 = 11;
			int n_prefs2 = 20;
			int[][] prefs2 = { { 1, 3 }, { 1, 5 }, { 2, 5 }, { 2, 8 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 5 },
					{ 4, 6 }, { 5, 1 }, { 6, 1 }, { 6, 9 }, { 7, 3 }, { 7, 5 }, { 8, 9 }, { 8, 7 }, { 8, 10 },
					{ 9, 11 }, { 10, 11 } };
			solve(n2, n_prefs2, prefs2);
		case 3:
			int n3 = 15;
			int n_prefs3 = 20;
			int[][] prefs3 = { { 1, 3 }, { 1, 5 }, { 2, 5 }, { 2, 8 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 },
					{ 4, 15 }, { 4, 13 }, { 5, 1 }, { 6, 10 }, { 6, 9 }, { 7, 3 }, { 7, 5 }, { 8, 9 }, { 8, 7 },
					{ 8, 14 }, { 9, 13 }, { 10, 11 } };
			solve(n3, n_prefs3, prefs3);
			break;
		default:
			System.out.println("No such example!");
		}
	}

	private void solve(int n, int n_prefs, int[][] prefs) {
		// Create store
		Store store = new Store();

		// People placed at index 1 to n
		IntVar[] people = new IntVar[n];
		for (int i = 0; i < n; i++) {
			people[i] = new IntVar(store, "x" + (i + 1), 1, n);
		}
		store.impose(new Alldifferent(people));

		// The preferred distance
		Distance[] dist_prefs = new Distance[n_prefs];
		for (int i = 0; i < n_prefs; i++) {
			dist_prefs[i] = new Distance(people[prefs[i][0] - 1], people[prefs[i][1] - 1], new IntVar(store, 1, 1));
		}

		// The cost vars
		IntVar[] pref = new IntVar[n_prefs];
		for (int i = 0; i < n_prefs; i++) {
			pref[i] = new IntVar(store, -1, 0);
		}

		// Add constraint if preferred distance is fulfilled the var is -1 otherwise 0
		for (int i = 0; i < n_prefs; i++) {
			store.impose(new IfThenElse(dist_prefs[i], new XeqC(pref[i], -1), new XeqC(pref[i], 0)));
		}

		// Sum the cost and minimize
		IntVar cost = new IntVar(store, "cost", -n_prefs, 0);
		store.impose(new SumInt(store, pref, "==", cost));

		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(people, null, new IndomainMin<IntVar>());
		boolean result = search.labeling(store, select, cost);
		if (result) {
			System.out.println("\n*** Yes");
			System.out.println("Solution: " + Arrays.asList(people));
			System.out.println("Cost: " + (-1) * cost.value());
		} else {
			System.out.println("\n*** No");
		}
	}
}
