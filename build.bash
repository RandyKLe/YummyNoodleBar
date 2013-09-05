#!/bin/bash
# Used to rebuild all the templated docs

#this finds all directories containing README.ftl.md and creates a bash array
doc_locations=($(
find . -type f -name 'README.ftl.md' |sed 's#\(.*\)/.*#\1#' |sort -u
));

echo "Converting ..."

echo "SIDEBAR.ftl.md -> SIDEBAR.md"
cat SIDEBAR.ftl.md | fpp > SIDEBAR.md

ORIG=`pwd`

for loc in "${doc_locations[@]}";
do
  echo " $loc/README.ftl.md -> $loc/README.stage2.md -> $loc/README.md"
  cd $loc
  cat README.ftl.md | fpp > README.stage2.md
  $ORIG/stage2 README.stage2.md > README.md
  rm -f README.stage2.md
  cd $ORIG
done

