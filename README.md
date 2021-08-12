# PMML2ECL

The PMML2ECL project is a converter for machine learning models for the
HPCC Systems platform. As of the first build, it only fully supports Linear Regression
and Logistic Regression. *See "How it works" for an explanation of what PMML and ECL
are.*

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
git clone https://github.com/hpccsystems-solutions-lab/pmml-ecl-ml.git
```
OR
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

First ensure your Thor cluster is running and configured correctly in the [bin/config.json](bin/config.json) file.

Your .ecl file must contain two OUTPUTs. The first will look like the following where
LinearRegression is replaced by the algorithm you wish to convert. *Currently
only 'LinearRegression' and 'LogisticRegression' are fully supported. Classification Forest was begun and will
give you output going from ECL to PMML, but was not finished and not verified.*
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

## How PMML2ECL works

The converter has a pretty standard data flow that is different for going to and from
PMML. In retrospect, this project should've been named ECL2PMML because it was the basis
of understanding the other way, and it is the more useful (and intense) operation.

What is ECL and what is PMML? *ECL* is the language used for the HPCC Datalake system.
It's declarative and you can read more about it at the 
[HPCC Systems Documentation](https://hpccsystems.com/training/documentation) page. The
converter uses .ecl files that contain information and commands regarding data manipulation,
which is used in the form of machine learning models. *PMML* is a standard XML format
for storing machine learning models. It supports a variety of algorithms that you can read about
and see examples of at the [PMML Specification](http://dmg.org/pmml/v4-4/GeneralStructure.html) page.

For converting ECL to PMML, the steps are as follows:
- Intake a .ecl file containing data manipulations.
- [Compile the .ecl file](src/main/java/com/hpccsystems/pmml2ecl/ecl/ECLCompiler.java) using a Thor 
cluster specified in the [bin/config.json](bin/config.json)
- Get the manipulated ML model in the form of an XML file
- [Parse the returned XML](src/main/java/com/hpccsystems/pmml2ecl/ecl/ECLParser.java), sending it to an algorithm for conversion
- Algorithm transforms and converts the data into a [PMMLElement(s)](src/main/java/com/hpccsystems/pmml2ecl/pmml/PMMLElement.java)
- The PMMLElement(s) are written to a file

For converting PMML to ECL, the steps are as follows:
- Intake the .xml/.pmml file containing the stored model.
- [Parse the .xml/.pmml file](src/main/java/com/hpccsystems/pmml2ecl/pmml/PMMLParser.java) into a PMMLElement.
- Iterate over the PMMLElement and look for useful values to use in an ECL Dataset
- Write ECL to a file containing the correct dataset using an ML model format.

## Contributing to PMML2ECL

As mentioned before, not all HPCC ML models are currently supported. They can *somewhat* easily
be added. This is a two-way converter so of course, there will need to be work done on both ways.
I (Alex Parra), recommend starting with ECL to PMML.


**To convert ECL to PMML** you should first create a class for the Algorithm in `com.hpccsystems.pmml2ecl.ecl.algorithms`
(not to be confused with its counterpart PMML package). This class should implement `com.hpccsystems.pmml2ecl.ecl.algorithms.Algorithm`.
Add the following lines to the switch statement in the `convertToPMML()` method inside the
[ECLParser.java](src/main/java/com/hpccsystems/pmml2ecl/ecl/ECLParser.java) class. Change the case String and LinearRegression
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
Then add the following lines to the switch statements in the constructors in the 
[PMMLConverter.java](src/main/java/com/hpccsystems/pmml2ecl/PMMLConverter.java) class. Switch the
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