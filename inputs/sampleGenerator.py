import random
import argparse

INF = 1e9
COST_SCALAR = 12 + 1
COST_LINE = 1

parser = argparse.ArgumentParser()
parser.add_argument("-o", "--outputFile", required = True)
parser.add_argument("-n", "--numberOfPoints", required = False)
parser.add_argument("-d", "--dimensionOfPoints", required = False)
parser.add_argument("-s", "--sizeOfFile", required = False)
args = parser.parse_args()

numberOfPoints = args.numberOfPoints
dimensionOfPoints = args.dimensionOfPoints
sizeOfFile = args.sizeOfFile

numberInactive = (numberOfPoints is None) + (dimensionOfPoints is None) + (sizeOfFile is None)
if numberInactive != 1:
    print("Exactly 2 arguments from (numberOfPointsm, dimensionOfPoints, sizeOfFile) must be active")
    exit()

if numberOfPoints is None:
    sizeOfFile = int(sizeOfFile)
    dimensionOfPoints = int(dimensionOfPoints)
    numberOfPoints = sizeOfFile // (dimensionOfPoints * COST_SCALAR + COST_LINE)
elif dimensionOfPoints is None:
    sizeOfFile = int(sizeOfFile)
    numberOfPoints = int(numberOfPoints)
    dimensionOfPoints = ((sizeOfFile // numberOfPoints) - COST_LINE) // COST_SCALAR
else:
    dimensionOfPoints = int(dimensionOfPoints)
    numberOfPoints = int(numberOfPoints)



with open(args.outputFile, "w") as f:
    f.write(f"-1;{numberOfPoints};{dimensionOfPoints}\n")
    for i in range(numberOfPoints):
        line = [(random.uniform(-INF, INF)) for _ in range(dimensionOfPoints)]
        f.write("{};".format(i))
        for point in line:
            f.write("{:12.3f};".format(point))
        f.write("\n")
