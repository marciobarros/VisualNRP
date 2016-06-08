#
# Clean up
#
rm(list=ls())
options(scipen=4, digits=2)


#
# Loading data sets
#
# basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
basedir <- "~"

datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/optimizer/output_orl_classic.txt", sep="")
data <- read.table(datafile, sep=",", header=TRUE)

drops <- c("rate", "budget", "rounds")
data <- data[ , !(names(data) %in% drops)]

library("data.table")
dt <- data.table(data)


#
# Descriptive statistics
#
means <- dt[, .(mean = mean(fit)), by=list(alg, instance)]
meanByAlgorithm <- reshape(means, v.names = "mean", idvar = c("instance"), timevar = "alg", direction = "wide")

sds <- dt[, .(sd = sd(fit)), by=list(alg, instance)]
sdByAlgorithm <- reshape(sds, v.names = "sd", idvar = c("instance"), timevar = "alg", direction = "wide")

maxs <- dt[, .(max = max(fit)), by=list(alg, instance)]
maxByAlgorithm <- reshape(maxs, v.names = "max", idvar = c("instance"), timevar = "alg", direction = "wide")


#
# Vargha & Delaney's A12
#
vargha.delaney <- function(r1, r2) {
	m <- length(r1);
	n <- length(r2);
	return ((sum(rank(c(r1, r2))[seq_along(r1)]) / m - (m + 1) / 2) / n);
}


#
# Inference tests and effect-size
#
instances <- unique(data$instance)
names <- c()
pvalues <- c()
effectsizes <- c()

for (instance_ in instances)
{
	ils <- subset(data, instance == instance_ & alg == "ILS")$fit
	visils <- subset(data, instance == instance_ & alg == "VISILS")$fit
	
	names <- c(names, instance_)
	
	pvalue <- wilcox.test(visils, ils)$p.value
	pvalues <- c(pvalues, pvalue)
	
	effectsize <- vargha.delaney(visils, ils)
	effectsizes <- c(effectsizes, effectsize)
}

result <- data.frame(instance=names, pvalue=pvalues, es=effectsizes)
