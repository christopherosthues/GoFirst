#!/usr/bin/env bash
./gradlew :composeApp:createDistributable
# ./gradlew :composeApp:package # all
# ./gradlew :composeApp:packageDmg # Mac
# Windows -> Wix toolset
./gradlew :composeApp:packageMsi
# ./gradlew :composeApp:packageDeb # Linux