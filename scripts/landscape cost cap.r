#
# PLOTS AN INSTANCE
#
plotInstance <- function(basedir, name) {

	datafile <- paste(basedir, "results/landscape/cost-cap/", name, ".txt", sep="")
	data <- read.table(datafile, sep=",", header=TRUE)
	#data <- subset(data, data$fit >= 0)

	data30 <- subset(data, data$budget == 30 & data$risk == 20)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Cap 20%", sep=""))
	
	data30 <- subset(data, data$budget == 30 & data$risk == 40)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Cap 40%", sep=""))
	
	data30 <- subset(data, data$budget == 30 & data$risk == 60)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Cap 60%", sep=""))
}

#
# BASE DIRECTORY
#
# basedir <- "/Users/marcio/Desktop/Codigos/VisualNRP/"
# basedir <- "/Users/marcio.barros/Desktop/Codigos/VisualNRP/"
basedir <- "~/Desktop/Codigos/VisualNRP/";

#
# PLOTS ALL CLASSIC INSTANCES
#
pdf("cost-cap-plot-full.pdf", width=11.69, height=8.27)
par(mfrow=c(5, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance(basedir, "nrp1");
plotInstance(basedir, "nrp2");
plotInstance(basedir, "nrp3");
plotInstance(basedir, "nrp4");
plotInstance(basedir, "nrp5");
dev.off()
