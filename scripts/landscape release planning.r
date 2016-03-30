basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
# basedir <- "~"

datafile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/landscape/release/nrp1.txt", sep="")
data <- read.table(datafile, sep=",", header=TRUE)

boxplot(data$fit~data$cust, main="nrp1-30 R2")
