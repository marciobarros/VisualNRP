#
# Clean up
#
rm(list=ls())
options(scipen=100, digits=4)


#
# BASE DIRECTORY
#
basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
# basedir <- "~"


#
# LOADS DATA
#
datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/release/nrp1.txt", sep="")
data <- read.table(datafile, sep=",", header=TRUE)
rounds <- unique(data$round)

#
# PLOTS ALL RELEASES
#
pdf("release-landscape.pdf", width=11.69, height=6)
par(mfrow=c(2, 3))
par(mar=c(2.1, 3.1, 2.1, 1.1))

for (round_ in rounds) {
	rdata <- subset(data, data$round == round_)
	boxplot(rdata$fit~rdata$cust, main=paste("nrp1-15 R", round_, sep=""))
}

dev.off()
