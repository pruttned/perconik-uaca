#!/bin/bash

###########################################[ FUNCTIONS ]##############################################################

#Executes 'uaca.jar'
function runUaca {
	echo "Running User Activity Central Application (UACA)"
	java -Djava.library.path="/opt/gti/perconik/uaca/libs" -jar "/opt/gti/perconik/uaca/[JarName]" &
}

#Prints the help
function printHelp {
	echo "Usage: run.sh [-options]"
	echo "If no options are specified, runs the User Activity Central Application (UACA)."
	echo "Options:"
	echo -e "\t-init"
	echo -e "\t\t runs the 'first run' initializations and runs UACA"
	echo -e "\t-window [-port <portNumber>]"
	echo -e "\t\t if UACA is running, shows its main window (GUI); specify the port if it has been changed in the UACA settings (the Local Services Port field)"
	echo -e "\t-help"
	echo -e "\t\t prints this help"
}

#Prints, how to print help
function printHowToPrintHelp {
	echo "run with '-help' for help"
}

#Determines the port for UACA REST API (local services port)
#INPUT (two arguments): '"-port" OR null'; 'port number OR null'
function getUacaPort {
	if [ "$1" == "-port" ] && [ -n "$2" ]
	then
		echo "$2"
	else
		echo "16375" #default
	fi
}

###########################################[ MAIN ]##############################################################

if [ $# -eq 0 ] ##### NO ARGUMENTS #####
then
	runUaca
	printHowToPrintHelp
elif [ "$1" == "-init" ] ##### -init #####
then
	echo "Running the initialization..."
	rm -rf ~/.config/autostart/perconik-uaca.desktop #remove old invalid directory (see c 43f78188376f571e8e8156c8a5d029355fc26c92)
	mkdir -p ~/.config/autostart 
	cp /opt/gti/perconik/uaca/perconik-uaca.desktop ~/.config/autostart
	echo "Initialization finished"
	
	runUaca
elif [ "$1" == "-window" ] ##### -window #####
then	
	curl -X POST http://localhost:$(getUacaPort $2 $3)/uaca/window
elif [ "$1" == "-help" ] ##### -help #####
then
	printHelp
else
	printHowToPrintHelp
fi


