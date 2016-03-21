rm(list=ls())

# basedir <- "/Users/marcio"
# basedir <- "/Users/marcio.barros"
basedir <- "~"

zipfile <- paste(basedir, "/Desktop/Codigos/VisualNRP/results/analysis/resultados - bsgreedy.zip", sep="")
data <- read.table(unz(zipfile, "saida.txt"), sep=";", header=FALSE)

library("data.table")
dt <- data.table(data)

means <- dt[, .(mean = mean(V5)), by=list(V1, V2)]
meanByAlgorithm <- reshape(means, v.names = "mean", idvar = c("V2"), timevar = "V1", direction = "wide")
# print(result, nrows=200)

sds <- dt[, .(sd = sd(V5)), by=list(V1, V2)]
sdByAlgorithm <- reshape(sds, v.names = "sd", idvar = c("V2"), timevar = "V1", direction = "wide")

maxs <- dt[, .(max = max(V5)), by=list(V1, V2)]
maxByAlgorithm <- reshape(maxs, v.names = "max", idvar = c("V2"), timevar = "V1", direction = "wide")
