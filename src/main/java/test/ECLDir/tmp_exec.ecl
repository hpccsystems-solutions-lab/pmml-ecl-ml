IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT PBblas;
IMPORT PBblas.Types;
IMPORT LinearRegression as LR;

pointRecord := RECORD
        REAL id;
        REAL A1;
END;

predRecord := RECORD
        INTEGER id;
        REAL A1;
END;

depPoints := DATASET([
    {0, 2},
    {1, 5},
    {2, 6.5}
], pointRecord);

indepPoints := DATASET([
    {0, 5},
    {1, 11},
    {2, 14}
], predRecord);

ML_Core.ToField(depPoints, depPointsNF);
ML_Core.ToField(indepPoints, indepPointsNF);

linRegress := LR.OLS(depPointsNF, indepPointsNF);

OUTPUT(depPointsNF);
OUTPUT(linRegress.GetModel);
