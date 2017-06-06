#!/bin/bash

echo -e "Publishing javadoc...\n"

cp -R $TRAVIS_BUILD_DIR/javadoc $HOME/javadoc-latest

cd $HOME
git config --global user.email "blue@happening.blue"
git config --global user.name "happening-blue"
git clone --quiet --branch=master https://${GH_TOKEN}@github.com/htw-happening/Website master > /dev/null

cd master
git rm -rf ./javadoc
cp -Rf $HOME/javadoc-latest ./javadoc
git add -f .
git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to master"
git push -fq origin master > /dev/null

echo -e "Published Javadoc to master.\n"
