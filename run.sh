#!/bin/sh

cd `dirname $0`
screen -S poifullchan -d -m java -jar ./target/scala-2.10/poifullchan.jar
