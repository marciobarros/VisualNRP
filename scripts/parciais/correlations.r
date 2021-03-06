corte <- c(
23.000,
16.333,
34.313,
31.311,
39.163,
40.567,
30.467,
59.267,
55.249,
66.300,
65.533,
50.353,
90.567,
87.253,
95.993,
34.820,
34.990,
35.526,
35.163,
35.288,
37.503,
35.831,
36.304,
31.280,
32.112,
30.610,
31.379,
56.057,
56.748,
56.798,
56.199,
56.831,
59.862,
57.171,
57.676,
52.708,
54.160,
51.987,
52.799);


depreq <- c(
1.71,
1.68,
2.00,
3.08,
2.67,
1.71,
1.68,
2.00,
3.08,
2.67,
1.71,
1.68,
2.00,
3.08,
2.67
);

reqs <- c(
140,
620,
1500,
3250,
1500,
140,
620,
1500,
3250,
1500,
140,
620,
1500,
3250,
1500,
3502,
4254,
2844,
3186,
2690,
2650,
2512,
2246,
4060,
4368,
3566,
3643,
3502,
4254,
2844,
3186,
2690,
2650,
2512,
2246,
4060,
4368,
3566,
3643
);

custs <- c(
100,
500,
500,
750,
1000,
100,
500,
500,
750,
1000,
100,
500,
500,
750,
1000,
536,
491,
456,
399,
445,
315,
423,
294,
768,
617,
765,
568,
536,
491,
456,
399,
445,
315,
423,
294,
768,
617,
765,
568
);

diffils <- c(
0.233,
1.056,
0.419,
0.516,
3.424,
0.079,
0.605,
0.328,
0.194,
0.363,
-0.008,
0.216,
0.242,
0.419,
0.148,
0.008,
0.073,
0.115,
0.014,
0.087,
0.090,
0.025,
-0.026,
0.148,
0.086,
0.022,
0.107,
0.113,
0.047,
0.052,
0.037,
0.068,
0.059,
-0.024,
0.066,
0.106,
0.097,
0.194,
0.033);

diffbma <- c(
0,
3.18307964006391,
3.57740614241587,
6.49345646793717,
3.71252134770463,
0.614243816058874,
3.73178902537201,
2.39682956180273,
5.71603123389145,
1.48704966238737,
-0.0918273645546445,
1.72713125297975,
0.164286973382685,
0.146760478266493,
-0.00484549784029743,
3.8544061302682,
3.69773395278278,
3.04047413858273,
2.1934543435255,
2.80184816745317,
1.47149257664939,
2.59453585315742,
0.848710246507423,
6.61555022568035,
5.27374405599284,
6.81635375590999,
4.84653085633312,
3.74417103744171,
3.19651500484028,
2.35420355169268,
2.10460358998931,
2.167896157887,
0.965821254190078,
1.52325086333319,
0.975932511785626,
6.12007360734512,
4.90309053952402,
6.10593067280292,
4.42249778565101
);

budget <- c(rep(30,5), rep(50,5), rep(70,5), rep(30,12), rep(50,12));



cor(diffils, corte, method="spearman")

cor(diffils[1:length(depreq)], depreq, method="spearman")

cor(diffils, reqs, method="spearman")

cor(diffils, custs, method="spearman")

cor(diffils, budget, method="spearman")




cor(diffbma, corte, method="spearman")

cor(diffbma[1:length(depreq)], depreq, method="spearman")

cor(diffbma, reqs, method="spearman")

cor(diffbma, custs, method="spearman")

cor(diffbma, budget, method="spearman")

