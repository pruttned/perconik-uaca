Dim currentDirectory
currentDirectory = CreateObject("Scripting.FileSystemObject").GetParentFolderName(Wscript.ScriptFullName)

Dim command
command = "java -Djava.library.path=""" & currentDirectory & "\libs"" -jar """ & currentDirectory & "\UACA.jar"""
CreateObject("WScript.Shell").Run(command), 0