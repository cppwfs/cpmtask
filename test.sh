#!/bin/bash

function build() {
  cd $1
  ./mvnw clean test
  ret=$?
  if [ $ret -ne 0 ]; then
  exit $ret
  fi
  cd ../
}

cd $(dirname $0)

build "BananaAlert"
build "filterbadcpm"
build "transformcpm"
build "logbananaaverage"

exit
