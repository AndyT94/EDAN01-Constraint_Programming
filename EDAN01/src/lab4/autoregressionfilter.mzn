include "globals.mzn";
include "diffn.mzn";

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
int: max_cycles = if del_add > del_mul then n * del_add else n * del_mul endif;

% List of tasks' duration
array[1..length(add)] of int: dur_add = [del_add | i in 1..length(add)];
array[1..length(mul)] of int: dur_mul = [del_mul | i in 1..length(mul)];
array[1..n] of var int: dur_all;

% List of resource capacity
array[1..length(add)] of int: res_add = [1 | i in 1..length(add)];
array[1..length(mul)] of int: res_mul = [1 | i in 1..length(mul)];

% List of tasks' start
array[1..length(add)] of var 0..max_cycles: start_add;
array[1..length(mul)] of var 0..max_cycles: start_mul;
array[1..n] of var 0..max_cycles: start_all;

% List of assigned adder/multiplier
array[1..length(add)] of var 1..number_add: assigned_add;
array[1..length(mul)] of var 1..number_mul: assigned_mul;

var int: total_cycles;

% Constraints
constraint forall (i in index_set(add)) (dur_all[add[i]] = del_add /\ start_all[add[i]] = start_add[i]);
constraint forall (i in index_set(mul)) (dur_all[mul[i]] = del_mul /\ start_all[mul[i]] = start_mul[i]);

% Resource constraints
constraint cumulative(start_add, dur_add, res_add, number_add);
constraint cumulative(start_mul, dur_mul, res_mul, number_mul);

constraint diffn(start_add, assigned_add, dur_add, res_add);
constraint diffn(start_mul, assigned_mul, dur_mul, res_mul);

% Precedence constraints
constraint forall (i in 1..n, j in dependencies[i]) (start_all[i] + dur_all[i] <= start_all[j]);

constraint forall (i in index_set(last)) (start_all[last[i]] + dur_all[last[i]] <= total_cycles);

solve :: int_search(start_all, first_fail, indomain_min, complete) minimize total_cycles;

output ["Clock cycles: ", show(total_cycles), "\nAdder:\t\t", show(array1d(add)), "\nAdder start:\t", show(start_add), "\nAssigned adder:\t", show(assigned_add), 
            "\nMultiplier:\t", show(array1d(mul)), "\nMul start:\t", show(start_mul), "\nAssigned mul:\t", show(assigned_mul)];