plotInstance <- function(name) {

	basedir <- "/Users/marcio"
	# basedir <- "/Users/marcio.barros"
	# basedir <- "~"
	
	# zipfile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/landscape.zip", sep="")
	# datafile <- paste(name, ".txt", sep="")
	# data <- read.table(unz(zipfile, datafile), sep=",", header=TRUE)

	datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/", name, ".txt", sep="")
	data <- read.table(datafile, sep=",", header=TRUE)

	# par(mfrow=c(3, 1))

	data30 <- subset(data, data$budget == 30 & data$risk == 20)
	boxplot(data30$fit~data30$cust, main=paste(name, "-30 / Risk 20%", sep=""))
	
	data50 <- subset(data, data$budget == 30 & data$risk == 50)
	boxplot(data50$fit~data50$cust, main=paste(name, "-30 / Risk 50%", sep=""))
	
	data70 <- subset(data, data$budget == 30 & data$risk == 80)
	boxplot(data70$fit~data70$cust, main=paste(name, "-30 / Risk 80%", sep=""))
}

par(mfrow=c(5, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

plotInstance("nrp1");
plotInstance("nrp2");
plotInstance("nrp3");
plotInstance("nrp4");
plotInstance("nrp5");


par(mfrow=c(3, 1))
plotInstance("nrp1");



#basedir <- "/Users/marcio"
#datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/nrp3.txt", sep="")
#data <- read.table(datafile, sep=",", header=TRUE)
#par(mfrow=c(1, 1))
#data70 <- subset(data, data$budget == 30 & data$risk == 80)
#boxplot(data70$fit~data70$cust, main="nrp3-30 / Risk 80%")
