#!/bin/sh

SRC=generator-test/build/generated/source/kaptKotlin/main/resources/foundation-settings.json
DEST=foundation-generator/src/test/resources/settings/generator-test.settings.json

./gradlew generator-test:clean generator-test:build --info

cp ${SRC} ${DEST}
