#!/bin/bash
cd $(dirname $0)

build_locations=($(
find ../ -type f -name 'build.gradle' |sed 's#\(.*\)/.*#\1#' |sort -u
));

ORIG=`pwd`

echo "Testing projects ..."

OPTIONAL_CONTAINER_BUILD_TO_CHECK="task run"

for loc in "${build_locations[@]}";
do
  cd $loc
  echo `pwd`
  if grep -q "$OPTIONAL_CONTAINER_BUILD_TO_CHECK" build.gradle;
  then
    echo "Tests rely on a container, skipping for now..."
    ./gradlew clean classes testClasses
  else
    ./gradlew clean build
  fi
  ret=$?
  if [ $ret -ne 0 ]; then
  exit $ret
  fi
  rm -rf build
  cd $ORIG
done

exit
