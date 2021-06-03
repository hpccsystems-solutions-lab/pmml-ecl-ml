IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT PBblas;
IMPORT PBblas.Types;
IMPORT LinearRegression as LR;

pointRecord := RECORD
        INTEGER id;
        real A1;
END;

xPoints := DATASET([
    {0, 3},
    {1, 5},
    {2, 6.5}
], pointRecord);

yPoints := DATASET([
    {0, 7},
    {1, 11},
    {2, 14}
], pointRecord);

ML_Core.ToField(xPoints, xPointsNF);
ML_Core.ToField(yPoints, yPointsNF);

linRegress := LR.OLS(xPointsNF, yPointsNF);
OUTPUT(linRegress);
