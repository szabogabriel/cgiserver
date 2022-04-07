#!/bin/bash

ver=$(cat pom.xml | grep version | head -n 1 | tr '>' '\n' | tr '<' '\n' | grep -v version)

clean() {
        if [ -e Dockerfile ]; then
                rm -f Dockerfile
        fi
        if [ -e target ]; then
                rm -rf target
        fi
}

build() {
        mvn clean install
}

package() {
        cat Dockerfile.template | sed "s/##version##/${ver}/g" > Dockerfile

        docker build --tag cgiserver .
}

help() {
        cat<<EOF
Build and package the application. Usage:

./build.sh [clean|build|package|all]

For example:

./build.sh clean build package

./build.sh all
EOF
}

main() {
        if [ "x$1" = "x" ]; then
                help
                exit 0
        fi
        if [ "$1" = "all" ]; then
                clean
                build
                install
                exit 0
        fi
        if [ "$1" = "clean" ]; then
                clean
                shift
        fi
        if [ "$1" = "build" ]; then
                build
                shift
        fi
        if [ "$1" = "package" ]; then
                build
                shift
        fi
}

main $@
