plotInstance <- function(name) {

	basedir <- "/Users/marcio.barros"
	# basedir <- "~"
	
	zipfile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/landscape.zip", sep="")
	datafile <- paste(name, ".txt", sep="")
	data <- read.table(unz(zipfile, datafile), sep=",", header=TRUE)

	par(mfrow=c(3, 1))

	data30 <- subset(data, data$budget == 30)
	boxplot(data30$fit~data30$cust, main="Budget 30%")
	
	data50 <- subset(data, data$budget == 50)
	boxplot(data50$fit~data50$cust, main="Budget 50%")
	
	data70 <- subset(data, data$budget == 70)
	boxplot(data70$fit~data70$cust, main="Budget 70%")
}

plotInstance("nrp1");
