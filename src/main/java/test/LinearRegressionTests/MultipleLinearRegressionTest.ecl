IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT PBblas;
IMPORT PBblas.Types;
IMPORT LinearRegression as LR;

pointRecord := RECORD
        REAL id;
        REAL A1;
        REAL A2;
END;

predRecord := RECORD
        INTEGER id;
        REAL A1;
END;

depPoints := DATASET([
    {0, 1, 2},
    {1, 5, 1},
    {2, 6.5, 1},
    {3, 2, 3}
], pointRecord);

indepPoints := DATASET([
    {0, 8},
    {1, 10},
    {2, 11.5},
    {3, 11}
], predRecord);

ML_Core.ToField(depPoints, depPointsNF);
ML_Core.ToField(indepPoints, indepPointsNF);

linRegress := LR.OLS(depPointsNF, indepPointsNF);
OUTPUT('LinearRegression');
OUTPUT(linRegress.GetModel);
