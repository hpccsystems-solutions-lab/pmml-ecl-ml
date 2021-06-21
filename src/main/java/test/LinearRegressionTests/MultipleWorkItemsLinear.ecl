IMPORT ML_Core;
IMPORT ML_Core.Types as Types;
IMPORT LinearRegression as LR;

depPointsNF := DATASET([
    {1, 1, 1, 2},
    {1, 2, 1, 5},
    {1, 3, 1, 6.5},
    {2, 1, 1, 1},
    {2, 2, 1, 2},
    {2, 3, 1, 7}
], Types.NumericField);

indepPointsNF := DATASET([
    {1, 1, 1, 5},
    {1, 2, 1, 11},
    {1, 3, 1, 14},
    {2, 1, 1, 2},
    {2, 2, 1, 5},
    {2, 3, 1, 20}
], Types.NumericField);

linRegress := LR.OLS(depPointsNF, indepPointsNF);

OUTPUT('LinearRegression');
OUTPUT(linRegress.GetModel);
