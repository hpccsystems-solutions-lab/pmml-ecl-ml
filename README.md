# PMML2ECL

The PMML2ECL project is a converter for machine learning models for the
HPCC Systems platform. As of the first build, it only supports Linear Regression
and Logistic Regression.

## Usage

You must first configure the settings in the config.json package. It is necessary
in order to compile .ecl files using a proper HPCC cluster. The converter compiles to
Thor as of the first build.

Currently the .jar is available in the /bin folder for use. It takes 1 or 2 arguments
for input: the first being the input .ecl/.xml (or .pmml) file, and the second being
an optional output file path.

*When converting ECL to PMML, one must have two OUTPUTs in the .ecl file. The first
OUTPUT must include a string of the model. Right now there is only support for
'LinearRegression' and 'LogisticRegression'. Please use those two strings. The
second OUTPUT must be the result of .GetModel() on the ECL ML model. This is
only interpretable by the converter.*

