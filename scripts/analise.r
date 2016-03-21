rm(list=ls())

basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
# basedir <- "~"

zipfile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/analysis/resultados - original.zip", sep="")
data <- read.table(unz(zipfile, "saida.txt"), sep=";", header=FALSE)

library("data.table")
dt <- data.table(data)

result <- dt[, .(mean = mean(V5), sd = sd(V5), max = max(V5)), by=list(V1, V2)]
print(result, nrows=200)