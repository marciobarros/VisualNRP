## VisualNRP

Implementations of Hill Climbing and Iterated Local Search algorithms for the Next Release Problem.
These implementations use a graphical pattern observed in problem instances to narrow the search space.

More information in:
<i>Fuchshuber, R. and Barros. M.
Improving Heuristics for the Next Release Problem through Landscape Visualization
Search-Based Software Engineering, 222-227 (2014)</i>

FIXED RANDOM SEED: 253958446296927 (system time ou 26/04/2016)

==================================
LANDSCAPES
==================================

LCRR -i classic -b 30 -r 10 20 40 -m 1204 4970 7488 10690 18510 -o results/landscape/cost-risk/%s.txt -s 253958446296927

LCCR -i classic -b 30 -r 8 -o results/landscape/cost-cap/%s.txt -s 253958446296927

LPRR -i classic -b 30 -r 20 50 80 -o results/landscape/profit-risk/%s.txt -s 253958446296927

==================================
OPTIMIZERS
==================================

OP -i classic -b 30 50 70 -o output_op_classic.txt -s 253958446296927

OCC -i classic -b 30 50 70 -r 8 -o output_occ_classic.txt -s 253958446296927

OCR -i classic -b 30 -r 10 20 40 -m 1204 4970 7488 10690 18510 -o output_ocr_classic.txt -s 253958446296927

OPR -i classic -b 30 -r 10 20 40 -m 1204 4970 7488 10690 18510 -o output_opr_classic.txt -s 253958446296927
