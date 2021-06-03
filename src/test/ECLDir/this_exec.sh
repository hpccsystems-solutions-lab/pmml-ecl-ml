#!/usr/bin/env bash
set -e

function Usage() {

    echo "$0 [ecl command line options]+"
    exit 1

}

 

function FullDirPath() {

    echo "$(cd "$(dirname "$1")"; pwd -P)"

}

function FullFilePath() {

    echo "$(cd "$(dirname "$1")"; pwd -P)/$(basename "$1")"

}

ECL_CMD=$(which ecl)

if [ -z "${ECL_CMD}" ]; then

    echo "Error: ecl binary not found in PATH"

    exit 1

fi

SCRIPT_DIR=$(FullDirPath $(dirname "$0"))
TOP_DIR=$(FullDirPath $(dirname "${SCRIPT_DIR}/../"))

TMP_PATH="${SCRIPT_DIR}/tmp_exec.ecl"

REAL_TEMP_PATH=$(FullFilePath "${TMP_PATH}")
RETURN_PATH=$(FullFilePath "$SCRIPT_DIR/Return.xml")

####  Create the ECL at ${REAL_TEMP_PATH} and make sure it has a .ecl extension #####

# Execute the temporary ECL, capturing results

cd "${TOP_DIR}"
XML_RESULTS=$(ecl run thor "${REAL_TEMP_PATH}" -s=play.hpccsystems.com -u=aparra -pw=\"\")


if [ -f "${RETURN_PATH}" ]; then
    rm "${RETURN_PATH}"
fi
echo "$XMLRESULTS" >> ${RETURN_PATH}
echo "hello"