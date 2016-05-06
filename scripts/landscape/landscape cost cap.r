#
# PLOTS AN INSTANCE
#
plotInstance <- function(basedir, name) {

	datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/cost-cap/", name, ".txt", sep="")
	data <- read.table(datafile, sep=",", header=TRUE)
	
	data30 <- subset(data, data$budget == 30 & data$risk == 8)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Cap 8%", sep=""))
	hist(data30$ratio[data30$ratio >= 0.04 & data30$ratio <= 0.10], main=paste(name, "-30", sep=""), breaks=100)
}

#
# BASE DIRECTORY
#
# basedir <- "/Users/marcio"
basedir <- "/Users/marcio.barros"
# basedir <- "~";

#
# PLOTS ALL CLASSIC INSTANCES
#
pdf("cost-cap-plot-full.pdf", width=11.69, height=8.27)
par(mfrow=c(5, 2))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance(basedir, "nrp1");
plotInstance(basedir, "nrp2");
plotInstance(basedir, "nrp3");
plotInstance(basedir, "nrp4");
plotInstance(basedir, "nrp5");
dev.off()
