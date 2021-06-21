IMPORT ML_Core;
IMPORT ML_Core.Types;
IMPORT ML_Core.ModelOps2 as ModelOps2;
IMPORT LogisticRegression as LR;

classifications := DATASET(
       [{1, 1, 1, 0}, {1, 1, 2, 1},
        {1, 2, 1, 0}, {1, 2, 2, 1},
        {1, 3, 1, 0}, {1, 3, 2, 1},
        {1, 4, 1, 0}, {1, 4, 2, 1},
        {1, 5, 1, 0}, {1, 5, 2, 1},
        {1, 6, 1, 0}, {1, 6, 2, 1},
        {1, 7, 1, 1}, {1, 7, 2, 0},
        {1, 8, 1, 1}, {1, 8, 2, 0},
        {1, 9, 1, 1}, {1, 9, 2, 0},
        {2, 1, 1, 0}, {2, 1, 2, 1}, {2, 1, 3, 0},
        {2, 2, 1, 0}, {2, 2, 2, 1}, {2, 2, 3, 0},
        {2, 3, 1, 0}, {2, 3, 2, 1}, {2, 3, 3, 0},
        {2, 4, 1, 0}, {2, 4, 2, 0}, {2, 4, 3, 1},
        {2, 5, 1, 0}, {2, 5, 2, 0}, {2, 5, 3, 1},
        {2, 6, 1, 0}, {2, 6, 2, 0}, {2, 6, 3, 1},
        {2, 7, 1, 1}, {2, 7, 2, 0}, {2, 7, 3, 0},
        {2, 8, 1, 1}, {2, 8, 2, 0}, {2, 8, 3, 0},
        {2, 9, 1, 1}, {2, 9, 2, 0}, {2, 9, 3, 0},
        {3, 1, 1, 0},
        {3, 2, 1, 0},
        {3, 3, 1, 0},
        {3, 4, 1, 0},
        {3, 5, 1, 0},
        {3, 6, 1, 0},
        {3, 7, 1, 1},
        {3, 8, 1, 1},
        {3, 9, 1, 1}], Types.DiscreteField);
observations := DATASET(
    [{1, 1, 1, .6}, {1, 1, 2, .7}, {1, 1, 3, .8},
     {1, 2, 1, .8}, {1, 2, 2, .7}, {1, 2, 3, .7},
     {1, 3, 1, .7}, {1, 3, 2, .8}, {1, 3, 3, .6},
     {1, 4, 1, .9}, {1, 4, 2, .7}, {1, 4, 3, .9},
     {1, 5, 1, .8}, {1, 5, 2, .9}, {1, 5, 3, .6},
     {1, 6, 1, .8}, {1, 6, 2, .5}, {1, 6, 3, .8},
     {1, 7, 1, .2}, {1, 7, 2,  0}, {1, 7, 3, .3},
     {1, 8, 1, .3}, {1, 8, 2, .4}, {1, 8, 3, .4},
     {1, 9, 1, .4}, {1, 9, 2, .7}, {1, 9, 3,  0},
     {2, 1, 1, .9}, {2, 1, 2, .7}, {2, 1, 3, .8},
     {2, 2, 1, .8}, {2, 2, 2, .7}, {2, 2, 3, .7},
     {2, 3, 1, .7}, {2, 3, 2, .8}, {2, 3, 3, .9},
     {2, 4, 1, .6}, {2, 4, 2, .5}, {2, 4, 3, .6},
     {2, 5, 1, .6}, {2, 5, 2, .6}, {2, 5, 3, .6},
     {2, 6, 1, .6}, {2, 6, 2, .5}, {2, 6, 3, .5},
     {2, 7, 1, .2}, {2, 7, 2, .1}, {2, 7, 3, .3},
     {2, 8, 1, .3}, {2, 8, 2, .4}, {2, 8, 3, .4},
     {2, 9, 1, .4}, {2, 9, 2, .7}, {2, 9, 3, .3},
     {3, 1, 1, .6}, {3, 1, 2, .7}, {3, 1, 3, .8},
     {3, 2, 1, .8}, {3, 2, 2, .7}, {3, 2, 3, .7},
     {3, 3, 1, .7}, {3, 3, 2, .8}, {3, 3, 3, .6},
     {3, 4, 1, .9}, {3, 4, 2, .7}, {3, 4, 3, .9},
     {3, 5, 1, .8}, {3, 5, 2, .9}, {3, 5, 3, .6},
     {3, 6, 1, .8}, {3, 6, 2, .5}, {3, 6, 3, .8},
     {3, 7, 1, .2}, {3, 7, 2, .1}, {3, 7, 3, .3},
     {3, 8, 1, .3}, {3, 8, 2, .4}, {3, 8, 3, .4},
     {3, 9, 1, .4}, {3, 9, 2, .7}, {3, 9, 3, .3}], Types.NumericField);

testData := DATASET([
    {1, 1, 1, 9.2},
    {1, 1, 2, .87}], Types.NumericField);


regressionModel := LR.BinomialLogisticRegression(max_iter:=4).getModel(observations, classifications);
betas := LR.ExtractBeta(regressionModel);

OUTPUT('LinearRegression');
OUTPUT(regressionModel);
//OUTPUT(LR.LogitPredict(betas, testData));

//matrix := DATASET([{1, 1, 1, 5},
//    {1, 1, 2, 1},
//    {1, 1, 3, 4},
//    {1, 1, 4, 1},
//    {1, 1, 5, 2},
//    {1, 1, 6, 4},
//    {1, 1, 7, 8}], Types.NumericField);
//OUTPUT(linearRegression.Predict(matrix, model));