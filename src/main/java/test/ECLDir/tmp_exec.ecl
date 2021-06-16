IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LinearRegression as LR;
model := DATASET([{1, 1, 1, 2.999999999999873},
    {1, 1, 2, 1.000000000000016},
    {1, 1, 3, 2.000000000000033}], Types.Layout_Model);
linearRegression := LR.OLS();
//Use `linearRegression.Predict(matrixNF, model);` to predict new values after this line.

matrix := DATASET([{1, 1, 1, 5},
    {1, 1, 2, 1}], Types.NumericField);
OUTPUT(linearRegression.Predict(matrix, model));