#Partially based on http://nsis.sourceforge.net/A_simple_installer_with_start_menu_shortcut_and_uninstaller

RequestExecutionLevel admin

!include MUI2.nsh

#!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

OutFile "PerConIK_UACA_Install.exe"
installDir "$PROGRAMFILES\Gratex International\PerConIK\UserActivity"
Name "PerConIK User Activity Central Application (UACA)"

#------------------------------------------------------------------------------------------------------------
# INSTALL
#------------------------------------------------------------------------------------------------------------
Function .onInit
	SetShellVarContext all
	writeUninstaller $INSTDIR\PerConIK_UACA_Uninstall.exe
FunctionEnd

Section "User Activity" UserActivity
	#adding files and folders
	setOutPath $INSTDIR
	File UACA.jar
	File Run.vbs
	setOutPath $INSTDIR\libs
	File ..\libs\*.*
	
	#port reservation
	#ExecWait 'netsh http add urlacl url=http://+:16375/ user=\Everyone'	
	
	#Adding UACA to startup
	WriteRegStr HKCU Software\Microsoft\Windows\CurrentVersion\Run PerConIkUserActivity '"$SYSDIR\wscript.exe" "$INSTDIR\Run.vbs"'
	
	#Run UACA after installation
	Exec '"$SYSDIR\wscript.exe" "$INSTDIR\Run.vbs"'
SectionEnd

#------------------------------------------------------------------------------------------------------------
# UNINSTALL
#------------------------------------------------------------------------------------------------------------
Function un.onInit
	SetShellVarContext all
 	
	MessageBox MB_OKCANCEL "PerConIK User Activity Central Application (UACA) is going to be removed permanently. Please exit UACA if it is running (located in the system tray)." IDOK next
		Abort
	next:
FunctionEnd

Section "Uninstall"
	Delete $INSTDIR\PerConIK_UACA_Uninstall.exe
	
	#removing files and folders
	Delete $INSTDIR\UACA.jar
	Delete $INSTDIR\Run.vbs
	RMDir /r $INSTDIR\libs
	RMDir $INSTDIR #remove the installation directory if it is empty
	
	#removing port reservation
	#ExecWait 'netsh http delete urlacl url=http://+:16375/'
	
	#Removing UserActivity.App from startup
	DeleteRegValue HKCU Software\Microsoft\Windows\CurrentVersion\Run PerConIkUserActivity
SectionEnd





!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${UserActivity} "UserActivity"
!insertmacro MUI_FUNCTION_DESCRIPTION_END