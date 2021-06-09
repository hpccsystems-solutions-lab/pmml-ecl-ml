IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LinearRegression as LR;
model := DATASET([{1, 1, 1, 0.9999999999998498},
    {1, 1, 2, 2.000000000000026}], Types.Layout_Model);
linearRegression := LR.OLS();
//Use `linearRegression.Predict(matrixNF, model);` to predict new values.
OUTPUT(model);
newX := DATASET([{1, 5.0, [1, 1]},
    {2, 7.0, [1, 2]}], Types.Layout_Model2);
newXNF := ModelOps2.ToNumericField(newX);
OUTPUT(linearRegression.Predict(newXNF, model));