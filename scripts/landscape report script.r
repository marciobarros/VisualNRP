plotInstance <- function(name) {

	filename <- paste("~/Desktop/Codigos/VisualNRP/results/landscape/", name, ".txt", sep="")
	data <- read.table(filename, sep=",", header=TRUE)

	par(mfrow=c(3, 1))

	data30 <- subset(data, data$budget == 30)
	boxplot(data30$fit~data30$cust, main="Budget 30%")
	
	data50 <- subset(data, data$budget == 50)
	boxplot(data50$fit~data50$cust, main="Budget 50%")
	
	data70 <- subset(data, data$budget == 70)
	boxplot(data70$fit~data70$cust, main="Budget 70%")
}

plotInstance("nrp1");
