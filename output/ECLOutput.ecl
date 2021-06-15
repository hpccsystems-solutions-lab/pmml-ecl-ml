IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LinearRegression as LR;
model := DATASET([{1, 1, 1, 0.9999999999998498},
    {1, 1, 2, 2.000000000000026}], Types.Layout_Model);
linearRegression := LR.OLS();
//Use `linearRegression.Predict(matrixNF, model);` to predict new values after this line.
