#!/bin/bash

cmpl="compile.txt"
run="run.txt"
np="nopolly"
p_n="polly_normal"
p_p="polly_parallel"
p_t="polly_tile"
p_v="polly_vector"
p_v_t="polly_vector_tile"
timer="/usr/bin/time -f \"%E\""

for N in ` seq 1024 32 2048 `;
	do
	echo "Doing for N = $N"

	# Compile times
	$timer ./clang -O3 -D N=$N matmul_2D.c -o $np &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $np\_$cmpl
	$timer ./clang -O3 -mllvm -polly -D N=$N matmul_2D.c -o $p_n &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_n\_$cmpl
	$timer ./clang -O3 -mllvm -polly -mllvm -polly-vectorizer=stripmine -D N=$N matmul_2D.c -o $p_v &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >>  $p_v\_$cmpl
	$timer ./clang -O3 -mllvm -polly -mllvm -polly-tiling -D N=$N matmul_2D.c -o $p_t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_t\_$cmpl
	$timer ./clang -O3 -mllvm -polly -mllvm -polly-tiling -mllvm -polly-vectorizer=stripmine -D N=$N matmul_2D.c -o $p_v_t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_v_t\_$cmpl
	
	for t in ` seq 0 1 4 `;
		do
		$timer ./clang -O3 -mllvm -polly -mllvm -polly-parallel -mllvm -polly-num-threads=$t -lgomp -D N=$N matmul_2D.c -o $p_p\_$t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >>  $p_p\_$t\_$cmpl
		done
		
	# Run times
	$timer ./$np &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $np\_$run
	$timer ./$p_n &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_n\_$run
	$timer ./$p_v &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_v\_$run	
	$timer ./$p_t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_t\_$run
	$timer ./$p_v_t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_v_t\_$run
	rm $np $p_n $p_v $p_t $p_v_t

	for t in ` seq 0 1 4`;
		do
		$timer ./$p_p\_$t &> /dev/stdout | awk -F: '{ print ($0 * 3600) + ($1 * 60) + $2 }' >> $p_p\_$t\_$run
		rm $p_p\_$t
		done		
	done
	
mkdir matmul_2D
cp *.txt ./matmul_2D
rm *.txt
