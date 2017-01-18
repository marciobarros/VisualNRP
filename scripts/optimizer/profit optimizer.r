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

datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/optimizer/output_op_all.txt", sep="")
data <- read.table(datafile, sep=",", header=TRUE)

drops <- c("risk", "solution")
data <- data[ , !(names(data) %in% drops)]

data <- subset(data, !grepl("nrp-",data$instance) | budget != 70);

library("data.table")
dt <- data.table(data)


#
# Descriptive statistics
#
means <- dt[, .(mean = mean(fit)), by=list(alg, instance, budget)]
meanByAlgorithm <- reshape(means, v.names = "mean", idvar = c("instance", "budget"), timevar = "alg", direction = "wide")

sds <- dt[, .(sd = sd(fit)), by=list(alg, instance, budget)]
sdByAlgorithm <- reshape(sds, v.names = "sd", idvar = c("instance", "budget"), timevar = "alg", direction = "wide")

maxs <- dt[, .(max = max(fit)), by=list(alg, instance, budget)]
maxByAlgorithm <- reshape(maxs, v.names = "max", idvar = c("instance", "budget"), timevar = "alg", direction = "wide")


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
instances <- unique(paste(data$instance, data$budget, sep="-"))

names <- c()
pvalues <- c()
effectsizes <- c()

for (instance_ in instances)
{
	ils <- subset(data, paste(data$instance, data$budget, sep="-") == instance_ & alg == "ILS")$fit
	visils <- subset(data, paste(data$instance, data$budget, sep="-") == instance_ & alg == "VISILS")$fit
	
	names <- c(names, instance_)
	
	pvalue <- wilcox.test(visils, ils)$p.value
	pvalues <- c(pvalues, pvalue)
	
	effectsize <- vargha.delaney(visils, ils)
	effectsizes <- c(effectsizes, effectsize)
}

result <- data.frame(instance=names, pvalue=pvalues, es=effectsizes)
