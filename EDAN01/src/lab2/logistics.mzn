include "globals.mzn";

% Data input
int: graph_size;
int: start;
int: n_dests;
array[1..n_dests] of int: dest;
int: n_edges;
array[int] of int: from;
array[int] of int: to;
array[int] of int: cost;


var int: output_cost;

% Solve the problem
solve minimize output_cost;

output ["Cost: ", show(output_cost)];