#!/bin/bash
rm -rf build .gradle bin
find . -name "*.class" -delete
echo "Projekt wyczyszczony (build, klas i cache Gradle)."
