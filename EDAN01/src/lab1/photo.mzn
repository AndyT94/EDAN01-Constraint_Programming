% Photo
include "globals.mzn";

int: n;
int: n_prefs;
array[1..n_prefs, 1..2] of 1..n: prefs;

% People placed at position 1 to n
array[1..n] of var 1..n: pos;

% Store fulfilled preferences. 1 if preference is fulfilled, 0 otherwise
array[1..n_prefs] of var 0..1: pref_ok;

% The cost
var 0..n_prefs: cost;

% Placed at different position
constraint all_different(pos);

% Check if preference is fulfilled
constraint forall (i in 1..n_prefs) (if abs(pos[prefs[i, 1]] - pos[prefs[i, 2]]) <= 2 then pref_ok[i] = 1 else pref_ok[i] = 0 endif);

% Sum the cost
constraint cost = sum (i in 1..n_prefs) (pref_ok[i]);

% Solve the problem
solve maximize cost;

% Output the cost
output ["Cost = ", show(cost), "\nPosition = ", show(pos)]