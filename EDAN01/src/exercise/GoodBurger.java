package exercise;

import org.jacop.constraints.LinearInt;
import org.jacop.constraints.XeqY;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class GoodBurger {
	private Store store;

	public static void main(String args[]) {
		GoodBurger gb = new GoodBurger();
		gb.model();
	}

	private void model() {
		store = new Store();

		IntVar beef = new IntVar(store, "beef", 1, 5);
		IntVar bun = new IntVar(store, "bun", 1, 5);
		IntVar cheese = new IntVar(store, "cheese", 1, 5);
		IntVar onion = new IntVar(store, "onion", 1, 5);
		IntVar pickle = new IntVar(store, "pickle", 1, 5);
		IntVar lettuce = new IntVar(store, "lettuce", 1, 5);
		IntVar ketchup = new IntVar(store, "ketchup", 1, 5);
		IntVar tomato = new IntVar(store, "tomato", 1, 5);

		IntVar ingredients[] = { beef, bun, cheese, onion, pickle, lettuce, ketchup, tomato };

		int[] sodium = { 50, 330, 310, 1, 260, 3, 160, 3 };
		int[] fat = { 17, 9, 6, 2, 0, 0, 0, 0 };
		int[] calories = { 220, 260, 70, 10, 5, 4, 20, 9 };

		store.impose(new LinearInt(store, ingredients, sodium, "<", 3000));
		store.impose(new LinearInt(store, ingredients, fat, "<", 150));
		store.impose(new LinearInt(store, ingredients, calories, "<", 3000));

		store.impose(new XeqY(ketchup, lettuce));
		store.impose(new XeqY(pickle, tomato));

		IntVar cost = new IntVar(store, "cost", -10000, 0);
		int[] price = { -25, -15, -10, -9, -3, -4, -2, -4 }; // in cent
		store.impose(new LinearInt(store, ingredients, price, "==", cost));

		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(ingredients, null, new IndomainMin<IntVar>());
		boolean result = search.labeling(store, select, cost);

		if (result) {
			System.out.println("Items:");
			for (int i = 0; i < ingredients.length; i++) {
				System.out.println(ingredients[i]);
			}
			System.out.println("Cost: " + (-1) * cost.value() + " cents ");
		} else {
			System.out.println("ARGH!!!");
		}
	}
}
