package lab1;

public class Photo {
	public static void main(String[] args) {
		new Photo().example(1);
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
		}
	}

	private void solve(int n, int n_prefs, int[][] prefs) {
		// TODO Auto-generated method stub
	}
}
