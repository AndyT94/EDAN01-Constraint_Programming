package exercise;

import java.util.ArrayList;

import org.jacop.constraints.LinearInt;
import org.jacop.constraints.XeqY;
import org.jacop.constraints.XltC;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

import search.SimpleDFS;

public class GoodBurger {
	private Store store;

	public static void main(String args[]) {
		GoodBurger gb = new GoodBurger();
		gb.model();
	}

	private void model() {
		ArrayList<IntVar> vars = new ArrayList<IntVar>();
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
		for (IntVar v : ingredients) {
			vars.add(v);
		}

		int[] sodium = { 50, 330, 310, 1, 260, 3, 160, 3 };
		int[] fat = { 17, 9, 6, 2, 0, 0, 0, 0 };
		int[] calories = { 220, 260, 70, 10, 5, 4, 20, 9 };
		IntVar sodiumValue = new IntVar(store, "sodium", 0, IntDomain.MaxInt);
		IntVar fatValue = new IntVar(store, "fat", 0, IntDomain.MaxInt);
		IntVar caloriesValue = new IntVar(store, "calories", 0, IntDomain.MaxInt);

		store.impose(new LinearInt(store, ingredients, sodium, "==", sodiumValue));
		store.impose(new LinearInt(store, ingredients, fat, "==", fatValue));
		store.impose(new LinearInt(store, ingredients, calories, "==", caloriesValue));
		store.impose(new XltC(sodiumValue, 3000));
		store.impose(new XltC(fatValue, 150));
		store.impose(new XltC(caloriesValue, 3000));
		

		store.impose(new XeqY(ketchup, lettuce));
		store.impose(new XeqY(pickle, tomato));

		IntVar cost = new IntVar(store, "cost", 280, IntDomain.MaxInt);
		int[] price = { 25, 15, 10, 9, 3, 4, 2, 4 }; // in cent
		store.impose(new LinearInt(store, ingredients, price, ">=", cost));

		
		SimpleDFS search = new SimpleDFS(store);
		search.setVariablesToReport(ingredients);
		boolean result = search.label(ingredients);
		System.out.println(result);
		System.out.println(cost.value());
	}
}
