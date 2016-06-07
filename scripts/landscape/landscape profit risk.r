#
# PLOTS AN INSTANCE
#
plotInstance <- function(basedir, name, focused) {

	datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/profit-risk/", name, ".txt", sep="")
	data <- read.table(datafile, sep=",", header=TRUE)

	if (focused) {
		data <- subset(data, data$fit >= 0)
	}

	data30 <- subset(data, data$budget == 30 & data$risk == 10)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Risk 10%", sep=""))
	
	data50 <- subset(data, data$budget == 30 & data$risk == 20)
	boxplot(data50$fit~data50$cust, main=paste(name, "-30 / Risk 20%", sep=""))
	
	data70 <- subset(data, data$budget == 30 & data$risk == 40)
	boxplot(data70$fit~data70$cust, main=paste(name, "-30 / Risk 40%", sep=""))
}

#
# BASE DIRECTORY
#
basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
# basedir <- "~";

#
# PLOTS ALL CLASSIC INSTANCES IN FULL MODE
#
pdf("profit-risk-plot-full.pdf", width=11.69, height=8.27)
par(mfrow=c(5, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance(basedir, "nrp1", FALSE);
plotInstance(basedir, "nrp2", FALSE);
plotInstance(basedir, "nrp3", FALSE);
plotInstance(basedir, "nrp4", FALSE);
plotInstance(basedir, "nrp5", FALSE);
dev.off()

#
# PLOTS ALL CLASSIC INSTANCES IN FOCUS MODE
#
pdf("profit-risk-plot-focus.pdf", width=11.69, height=8.27)
par(mfrow=c(5, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance(basedir, "nrp1", TRUE);
plotInstance(basedir, "nrp2", TRUE);
plotInstance(basedir, "nrp3", TRUE);
plotInstance(basedir, "nrp4", TRUE);
plotInstance(basedir, "nrp5", TRUE);
dev.off()
