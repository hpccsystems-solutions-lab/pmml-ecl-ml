IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LinearRegression as LR;
model := DATASET([{1, 1, 1, 6.008706171265235},
    {1, 1, 2, 3.363167396766842E-4},
    {1, 1, 3, 1.238009786077277E-4},
    {1, 1, 4, -0.07364295448649694},
    {1, 1, 5, -0.04315230485415502},
    {1, 1, 6, -0.011583900555823673},
    {1, 1, 7, 0.7840777698224044}], Types.Layout_Model);

// X1 (intercept) - 6.008706171265235
// X2 - 3.363167396766842E-4(latitude)
// X3 - 1.238009786077277E-4(longitude)
// X4 - -0.07364295448649694(zon_winds)
// X5 - -0.04315230485415502(mer_winds)
// X6 - -0.011583900555823673(humidity)
// X7 - 0.7840777698224044(s_s_temp)

linearRegression := LR.OLS();
//Use `linearRegression.Predict(matrixNF, model);` to predict new values after this line.
