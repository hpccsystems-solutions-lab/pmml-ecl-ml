IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LogisticRegression as LR;

observations := DATASET([
    {1, 1, 1, 11},
    {1, 2, 1, 14},
    {1, 3, 1, 9},
    {1, 4, 1, 5},
    {1, 1, 2, 1.1},
    {1, 2, 2, 1.5},
    {1, 3, 2, .25},
    {1, 4, 2, .9}], Types.NumericField);

classifications := DATASET([
    {1, 1, 1, 1},
    {1, 2, 1, 1},
    {1, 3, 1, 0},
    {1, 4, 1, 0},
    {1, 1, 2, 1},
    {1, 2, 2, 1},
    {1, 3, 2, 0},
    {1, 4, 2, 0}], Types.DiscreteField);

testData := DATASET([
    {1, 1, 1, 9.2},
    {1, 1, 2, .87}], Types.NumericField);


regressionModel := LR.BinomialLogisticRegression(max_iter:=4).getModel(observations, classifications);
betas := LR.ExtractBeta(regressionModel);

OUTPUT(regressionModel);
OUTPUT(betas);
OUTPUT(LR.LogitPredict(betas, testData));

//matrix := DATASET([{1, 1, 1, 5},
//    {1, 1, 2, 1},
//    {1, 1, 3, 4},
//    {1, 1, 4, 1},
//    {1, 1, 5, 2},
//    {1, 1, 6, 4},
//    {1, 1, 7, 8}], Types.NumericField);
//OUTPUT(linearRegression.Predict(matrix, model));