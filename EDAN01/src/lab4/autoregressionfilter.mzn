include "globals.mzn";

% Data input
int: del_add;
int: del_mul;
int: number_add;
int: number_mul;
int: n;
set of int: last;
set of int: add;
set of int: mul;
array[1..n] of set of int: dependencies;

var int: cost;

solve minimize cost;

output ["Clock cycles: ", show(cost)];