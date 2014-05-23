#!/bin/bash

#init
if [ "$1" == "-init" ]
then
	echo "Running the initialization..."
	rm -rf ~/.config/autostart/perconik-uaca.desktop #remove old invalid directory (see c 43f78188376f571e8e8156c8a5d029355fc26c92)
	mkdir -p ~/.config/autostart 
	cp /opt/gti/perconik/uaca/perconik-uaca.desktop ~/.config/autostart
	echo "Initialization finished"
fi

#Executing 'uaca.jar'
java -Djava.library.path="/opt/gti/perconik/uaca/libs" -jar "/opt/gti/perconik/uaca/[JarName]" &