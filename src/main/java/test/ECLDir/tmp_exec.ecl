IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LogisticRegression as LR;
betas := DATASET([
    {1, 0, 1, 12.66100299005275, 0},
    {1, 1, 1, -9.140426094131442, 0},
    {1, 2, 1, -5.628611253470368, 0},
    {1, 3, 1, -9.598161738533577, 0},
    {1, 0, 2, -12.6610029900526, 0},
    {1, 1, 2, 9.140426094131882, 0},
    {1, 2, 2, 5.62861125346997, 0},
    {1, 3, 2, 9.598161738533264, 0}], LR.Types.Model_Coef);

// X0 for category 1 - 12.66100299005275
// X1 for category 1 - -9.140426094131442
// X2 for category 1 - -5.628611253470368
// X3 for category 1 - -9.598161738533577
// X0 for category 2 - -12.6610029900526
// X1 for category 2 - 9.140426094131882
// X2 for category 2 - 5.62861125346997
// X3 for category 2 - 9.598161738533264

//Use `LR.LogitPredict(betas, matrixNF);` to predict new values after this line.

testData := DATASET([
    {1, 1, 1, .2},
    {1, 1, 2, .87}], Types.NumericField);
OUTPUT(LR.LogitPredict(betas, testData));