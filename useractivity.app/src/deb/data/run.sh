#!/bin/bash

#init
if [ "$1" == "-init" ]
then
	echo "Running the initialization..."
	cp /opt/gti/perconik/uaca/perconik-uaca.desktop ~/.config/autostart/perconik-uaca.desktop
	echo "Initialization finished"
fi

#Executing 'uaca.jar'
java -Djava.library.path="/opt/gti/perconik/uaca/libs" -jar "/opt/gti/perconik/uaca/[JarName]" &