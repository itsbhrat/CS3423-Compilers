set terminal postscript eps enhanced color
set output "compile_time.eps"
set title "Compile Times using various options in Polly"
set ylabel "Time (seconds)"
set xlabel "N in matmul.c"
set xtics 1024, 32, 2048 rotate
set key font ",10"
set key at 1900, 3.5
set key vertical maxrows 8
plot "./matmul_2D/nopolly_compile.txt" using (1024+($0*32)):1 with lines title "Just O3",\
"./matmul_2D/polly_normal_compile.txt" using (1024+($0*32)):1 with lines title "Just Polly",\
"./matmul_2D/polly_parallel_1_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Parallel (num-threads=1)",\
"./matmul_2D/polly_parallel_2_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Parallel (num-threads=2)",\
"./matmul_2D/polly_parallel_3_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Parallel (num-threads=3)",\
"./matmul_2D/polly_parallel_4_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Parallel (num-threads=4)",\
"./matmul_2D/polly_tile_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Tiling",\
"./matmul_2D/polly_vector_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Vectorizer (Stripmining)",\
"./matmul_2D/polly_vector_tile_compile.txt" using (1024+($0*32)):1 with lines title "Polly with Tiling and Vectorizer (Stripmining)"
