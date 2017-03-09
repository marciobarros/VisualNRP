# VisualNRP

Implementations of Hill Climbing and Iterated Local Search algorithms for the Next Release Problem.
These implementations use a graphical pattern observed in problem instances to narrow the search space.

**Fixed random seed rationale**: miliseconds system time on 2016-04-26.


## TUNNING

To run the tunning configurations, use the following commands:

* TIS -i classic -b 30 50 70 -o output_tis_classic.txt -s 253958446296927


## LANDSCAPES

To run the landscape reports for the "Cost-Risk as an Objective" (CRR), "Cost-Risk as a Constraint" (CCR), 
"Profit-Risk as an Objective" (PRR), and "Release Planning" (RPR), use the following commands:

* LCRR -i classic -b 30 -r 10 20 40 -o results/landscape/cost-risk/%s.txt -s 253958446296927

* LCCR -i classic -b 30 -r 8 -o results/landscape/cost-cap/%s.txt -s 253958446296927

* LPRR -i classic -b 30 -r 10 20 40 -o results/landscape/profit-risk/%s.txt -s 253958446296927

* LRPR -i nrp1 -b 15 -r 6 -o results/landscape/release/%s.txt -s 253958446296927


## OPTIMIZERS

To run the optimizers for the "Profit-Only" (OP), "Cost-Risk Only" (OCO), "Cost-Risk as an Objective" (OCR), 
"Cost-Risk as a Constraint" (OCC), "Profit-Risk Only" (OPO), "Profit-Risk as an Objective" (PRR), and 
"Release Planning" (ORL), use the following commands:

* OP -i classic -b 30 50 70 -o output_op_classic.txt -s 253958446296927

* OCO -i classic -b 30 50 70 -o output_oco_classic.txt -s 253958446296927

* OPO -i classic -b 30 50 70 -o output_opo_classic.txt -s 253958446296927

* OCR -i classic -b 30 -r 10 20 40 -o output_ocr_classic.txt -s 253958446296927

* OCC -i classic -b 30 -r 8 -o output_occ_classic.txt -s 253958446296927

* OPR -i classic -b 30 -r 10 20 40 -o output_opr_classic.txt -s 253958446296927

* ORL -i classic -b 15 -r 6 -t 5 -o output_orl_classic.txt -s 253958446296927
