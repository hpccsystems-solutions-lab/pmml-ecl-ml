# PMML2ECL

The PMML2ECL project is a converter for machine learning models for the
HPCC Systems platform. As of the first build, it only supports Linear Regression
and Logistic Regression.

## Prerequisites

You need an HPCC Systems cluster that you have access to use for the Converter. One
step of converting ECL to PMML is that it must be compiled. You can get started at
[https://hpccsystems.com/download#HPCC-Platform](https://hpccsystems.com/download#HPCC-Platform).

You also need Java installed. You can do so at [https://www.java.com/en/download/](https://www.java.com/en/download/).

## Installation

To install the PMML2ECL converter, you must clone this repository to your device.
The `bin` folder has the necessary .jar and bash/shell files to run the Converter. Run the following line in
your terminal to clone the repository.

```git
gh repo clone hpccsystems-solutions-lab/pmml-ecl-ml
```

If you would like to further integrate the tool into your device, you may
add the scripts to your PATH.

## Usage

You must first configure the settings in the `bin/config.json` file. It is necessary
in order to compile .ecl files using a proper HPCC cluster. It will use the given
data in order to compile to your Thor cluster.

For Windows, you will use the `bin/pmml2ecl.bat` file and for Mac/Linux users, you
will use the `bin/pmml2ecl.sh` file.

### For converting ECL to PMML

First ensure your Thor cluster is running and configured correctly in the `bin/config.json` file.

Your .ecl file must contain two OUTPUTs. The first will look like the following where
LinearRegression is replaced by the algorithm you wish to convert. *Currently
only 'LinearRegression', 'LogisticRegression', and 'ClassficationForest' are supported.*
The second line will be an OUTPUT containing the actual model. Examples below.

```ecl
...
myModel := LR.OLS(dependentPoints, independentPoints);

OUTPUT('LinearRegression');
OUTPUT(myModel.GetModel);
```

You must be in the `bin` folder of the cloned repository. 
Then you will go ahead and run the batch/bash script in your terminal where the first
argument is the filepath of the .ecl file. Example (Mac/Linux) below:

```shell script
cd ~/Documents/pmml-ecl-ml/bin
./pmml2ecl.sh /Users/Alex/Documents/MyHPCCMLProject/ExampleModel.ecl
```

The outputted file will be in the `bin/output/` directory, but the script will also tell you where it is.

*Optional: give the script a second argument (with .pmml/.xml ending) to have a custom
filepath for the outputted file.*

### For converting PMML to ECL

To convert PMML to ECL, the simple two steps are to navigate to the bin folder and
run the script on the desired XML/PMML file. You may include a second argument
of the desired output file path.

```shell script
cd ~/Documents/pmml-ecl-ml/bin
./pmml2ecl.sh /Users/Alex/Documents/MyOtherMLProject/ExampleModel.xml
```

The outputted file will be at `bin/output/ECLOutput.ecl`, but the script will also tell you where it is.

## Contributing to PMML2ECL

As mentioned before, not all HPCC ML models are currently supported. They can *somewhat* easily
be added. This is a two-way converter so of course, there will need to be work done on both ways.
I (Alex Parra), recommend starting with ECL to PMML.


**To convert ECL to PMML** you should first create a class for the Algorithm in `com.hpccsystems.pmml2ecl.ecl.algorithms`
(not to be confused with its counterpart PMML package. This class should implement `com.hpccsystems.pmml2ecl.ecl.algorithms.Algorithm`.
Add the following lines to the switch statement in the `convertToPMML()` method. Change the case String and LinearRegression
class to your desired algorithm.

```java
case "LinearRegression":
                        output = new LinearRegression(model).writeStoredModel(outputPath);
                        break;
```

You will receive all the data of the
model in the new class, and you can use it to create new `PMMLElement`s. It is at this point that you will be on your own
to find out what data is needed for the model in PMML and how to find it in the opaque data structure in ECL. Once done with
that, you can directly write the `PMMLElement`s using `PMMLElement().writeToFile(String filePath, [String identifier])`.
Please return a String that contains all of the file paths used by your writes. See previous models for inspiration.


**To convert PMML to PMML** you first need to create a class for the Algorithm in `com.hpccsystems.pmml2ecl.pmml.algorithms`.
It should implement `com.hpccsystems.pmml2ecl.pmml.algorithms.Algorithm`.
Then add the following lines to the switch statements in the constructors in the PMMLConverter class. Switch the
case String and LinearRegression class to the desired algorithms. 

```java
case "LinearRegression":
                ecl.addAll(new LinearRegression(model).getEclFromModel());
                break;
```

You'll then start receiving all the data in your new
class and can transform it line by line, returning a List<ECLElement>. At this point, you should find a stored model and
see what data you can pick out of it and feed to ECL. See previous models for inspiration.

Good luck, I had a lot of fun with this and hopefully you do too :)