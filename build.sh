#!/bin/bash

BINARIES_DIR="binaries"

function build() {
  cd $1
  ./mvnw clean install -DskipTests
  ret=$?
  if [ $ret -ne 0 ]; then
  exit $ret
  fi
  cp target/*.jar ../${BINARIES_DIR}/
  cd ../
}

cd $(dirname $0)

rm -rf ${BINARIES_DIR}
mkdir ${BINARIES_DIR}

build "BananaAlert"
build "filterbadcpm"
build "transformcpm"
build "logbananaaverage"

exit
