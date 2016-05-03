# COMMAND: LCRR -i classic -b 30 -r 10 25 40 -o results/landscape/cost-risk/%s.txt

#
# PLOTS AN INSTANCE
#
plotInstance <- function(basedir, name) {

	datafile <- paste(basedir, "results/landscape/cost-risk/", name, ".txt", sep="")
	data <- read.table(datafile, sep=",", header=TRUE)
	data <- subset(data, data$fit >= 0)

	data10 <- subset(data, data$budget == 30 & data$risk == 10)
	boxplot(data10$fit~data10$cust, main=paste(name, "-30 / Risk 10%", sep=""))
	
	data25 <- subset(data, data$budget == 30 & data$risk == 20)
	boxplot(data25$fit~data25$cust, main=paste(name, "-30 / Risk 20%", sep=""))
	
	data40 <- subset(data, data$budget == 30 & data$risk == 40)
	boxplot(data40$fit~data40$cust, main=paste(name, "-30 / Risk 40%", sep=""))
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
pdf("cost-risk-plot.pdf", width=11.69, height=8.27)
par(mfrow=c(5, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance(basedir, "nrp1");
plotInstance(basedir, "nrp2");
plotInstance(basedir, "nrp3");
plotInstance(basedir, "nrp4");
plotInstance(basedir, "nrp5");
dev.off()
