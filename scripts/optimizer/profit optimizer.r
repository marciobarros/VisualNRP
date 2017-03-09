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


datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/data/requirements/bma.txt", sep="")
bmaData <- read.table(datafile, sep=",", header=TRUE)


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


#
# Box-plots
#

pdf("boxplots-visual-nrp.pdf", 7, 8.5)	
par(mfrow=c(5,1))
par(mar=c(2, 2.5, 1, 1) + 0.1)

for (index in seq(1, 39, by = 8))
{
	instancePlot <- instances[c(index:min(index+7,39))]

	inst <- c();
	vis <- c();

	inst2 <- c();
	bmax <- c();
	bavg <- c();
	
	for (instance_ in instancePlot)
	{
		visils <- subset(data, paste(data$instance, data$budget, sep="-") == instance_ & alg == "VISILS")$fit
		bmamax <- subset(bmaData, paste(bmaData$instance, bmaData$budget, sep="-") == instance_)$bmamax
		bmaavg <- subset(bmaData, paste(bmaData$instance, bmaData$budget, sep="-") == instance_)$bmaavg
		optima <- subset(bmaData, paste(bmaData$instance, bmaData$budget, sep="-") == instance_)$opt
	
		minValue <- min(visils, bmamax, bmaavg, optima)
		visils <- (visils - minValue) / (optima - minValue)
		bmamax <- (bmamax - minValue) / (optima - minValue)
		bmaavg <- (bmaavg - minValue) / (optima - minValue)
		optima <- 1.0
		
		inst <- c(inst, rep(instance_, 30))
		vis <- c(vis, visils)
		
		inst2 <- c(inst2, instance_)
		bmax <- c(bmax, bmamax)
		bavg <- c(bavg, bmaavg)
	}
	
	boxplot(vis~inst, ylim=c(min(vis, bavg), 1));
	stripchart(bmax~inst2, vertical = TRUE, method = "jitter", pch = 22, bg = 12, cex = 1.5, add = TRUE) 
	stripchart(bavg~inst2, vertical = TRUE, method = "jitter", pch = 23, bg = 12, cex = 1.5, add = TRUE) 
}

dev.off()
