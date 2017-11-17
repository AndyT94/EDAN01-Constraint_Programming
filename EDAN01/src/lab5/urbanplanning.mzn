% Urban planning
include "globals.mzn";

% Data input parameters
int: n;
int: n_commercial;
int: n_residential;
array[0..n] of int: point_distribution;

% The grid where 0 represents commercial and 1 represents residential
array[1..n, 1..n] of var 0..1: grid;

% Number of residential lots of each row and column
array[1..n] of var int: residentials_row;
array[1..n] of var int: residentials_col;

% The points of each row and column
array[1..n] of var int: points_row;
array[1..n] of var int: points_col;

% The cost
var int: cost;

% Constraint for number of residential lots
constraint sum (i in 1..n) (residentials_row[i]) = n_residential;

% Sum the residential lots of each row and column
constraint forall (i in 1..n) (residentials_row[i] = sum (j in 1..n) (grid[i, j]));
constraint forall (i in 1..n) (residentials_col[i] = sum (j in 1..n) (grid[j, i]));

% Translate to point system
constraint forall (i in 1..n) (points_row[i] = point_distribution[residentials_row[i]]);
constraint forall (i in 1..n) (points_col[i] = point_distribution[residentials_col[i]]);

% Sum of the points is the cost
constraint cost = sum (i in 1..n) (points_row[i] + points_col[i]);

% Symmetry breaking constraint
constraint lex2(grid);

% Solve the problem
solve maximize cost;

% Output the cost and layout
output ["Cost = ", show(cost), "\nLayout =\n", show2d(grid)];