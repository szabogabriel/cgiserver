#!/bin/bash

# Parameters
readonly tracing=${1:-false}
#readonly hostname="http://localhost:8000"
readonly hostname="http://localhost:8080"
readonly thisFile="log.cgi"
chunkSize="1048576"
maxLinkNumberPerPage=32
logFontType="Courier New"
searchRegexp="."
filterRegexp="."
showLineNumbers="false"
changed="false"

trace(){
	if [[ $tracing == "true" ]]; then
		echo $1
	fi
}

# Several parameters can be set in the query strings at the moment.
#   projectList - it is the name of the project for which the logfiles should be listed.
#   logfile - the name of the logfile which should be listed. The page attribute must follow this one.
#   page - the page of the logfile which should be sent back.
parseQueryString(){
	trace "parseQueryString ENTRY"
	declare -a QUERYSTRING=$(echo "$QUERY_STRING" | sed 's/QUERY_STRING=//g' | sed 's/&/ /g')
	# Loop the array "QUERYSTRING" and save each
	# form name as variable with its value.
	for element in ${QUERYSTRING[@]}; do
		trace "Parsing element $element"
        	name=$( echo $element|cut -d= -f1 )
	        value=$( echo $element|cut -d= -f2 )
        	eval $name=\'$value\'
	done
	trace "parseQueryString EXIT"
}

parseCookieParams(){
	trace "parseCookieParams ENTRY"
	declare -a COOKIES=$(echo "$HTTP_COOKIE" | sed 's/;//g')
	for element in ${COOKIES[@]}; do
		name=$( echo $element|cut -d= -f1 )
		value=$( echo $element|cut -d= -f2 )
		eval $name=\'$value\'
	done
	logFontType=$(echo $logFontType | tr '+' ' ')
	trace "parseCookieParams EXIT"
}

printHeader(){
	trace "printHeader ENTRY"
	echo "Content-type: text/html"
	echo ""
	trace "printHeader EXIT"
}

printBootstrap(){
	trace "printBootstrap ENTRY"
	cat <<EOF
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

	<!-- jQuery library -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
EOF
	trace "printBootstrap EXIT"
}

printHtmlHeader(){
	trace "printHtmlHeader ENTRY"
	#title=${1:-Logviewer}
	cat <<EOF
	<html>
	<head><title>$1</title></head>
	<body>
EOF
	printBootstrap
	cat <<EOF
		<div class="container">
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<div class="navbar-header"><a class="navbar-brand" href="$hostname/$thisFile">Log Viewer</a></div>
					<ul class="nav navbar-nav">
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="$hostname/$thisFile?settings=x">Settings</a></li>
					</ul>
				</div>
			</nav>
EOF
	trace "printHtmlHeader EXIT"
}

printHtmlFooter(){
	trace "printHtmlFooter ENTRY"
	cat <<EOF
		</div>
	</body>
</html>
EOF
	trace "printHtmlFooter EXIT"
}

listSettings(){
	trace "listSettings ENTRY"
	printHeader
	printHtmlHeader Logviewer
	cat <<EOF
	<h1>Settings</h1><br>
	<form name="settings" action="log.cgi" method="get"><table>
	<tr><td>Search (marks the given string in the logfile): </td><td><input type="text" name="c_searchRegexp" value="$searchRegexp"></td></tr>
	<tr><td>Filter (filters the file content for the given string): </td><td><input type="text" name="c_filterRegexp" value="$filterRegexp"></td></tr>
	<tr><td>Page size (in bytes): </td><td><input type="text" name="c_chunkSize" value="$chunkSize"></td></tr>
	<tr><td>Logfile font type: </td><td>
		<select name="c_logFontType">
			<option value="arial" >Arial</option>
			<option value="arial black" >Arial Black</option>
			<option value="comic sans ms" >Comic Sans MS</option>
			<option value="courier" >Courier</option>
			<option value="courier new" >Courier New</option>
			<option value="georgia" >Georgia</option>
			<option value="helvetica" >Helvetica</option>
			<option value="impact" >Impact</option>
			<option value="palatino" >Palatino</option>
			<option value="times new roman" >Times New Roman</option>
			<option value="trebuchet ms">Trebuchet MS</option>
			<option value="verdana">Verdena</option>
		</select></td></tr>
	<tr><td/><td><input type="checkbox" name="showLineNumbers" value="true">Line numbers</td></tr>
	<tr><td/><td><input type="submit" value="Save"/></td></tr>
EOF
	printHtmlFooter
	trace "listSettings EXIT"
}

saveSettings(){
	trace "saveSettings ENTRY"
	echo "Content-type: text/html"
	echo "Set-Cookie: searchRegexp=$c_searchRegexp"
	echo "Set-Cookie: filterRegexp=$c_filterRegexp"
	echo "Set-Cookie: chunkSize=$c_chunkSize"
	echo "Set-Cookie: logFontType=$c_logFontType"
	echo "Set-Cookie: showLineNumbers=$c_showLineNumbers"
	echo ""
	trace "saveSettings EXIT"
}

listProjectsWithoutHeader(){
	trace "listProjectsWithoutHeader ENTRY"
	printHtmlHeader "Projects list"
	cat <<EOF
        <h1>Projects</h1>
	<p><a href="$hostname/$thisFile?projectList=Project1">Project 1</a></p>
	<p><a href="$hostname/$thisFile?projectList=Project2">Project 2</a></p>
EOF
	printHtmlFooter
	trace "listProjectsWithoutHeader EXIT"
}

listProjects(){
	trace "listProjects ENTRY"
	printHeader
	listProjectsWithoutHeader
	trace "listProjects EXIT"
}

setProjectFolder() {
	trace "setProjectFolder ENTRY"
	local projectName=$1
        case $projectName in
                Project1) projectFolder="/home/gabor/Temp/log/1/" ;;
                Project2) projectFolder="/home/gabor/Temp/log/2/" ;;
                *) projectFolder="/dev/null" ;;
        esac
	trace "setProjectFolder EXIT"
}

listProjectFiles(){
	trace "listProjectFiles ENTRY"
	printHeader
	local projectFolder="/tmp"
	local projectName=$1
	
	setProjectFolder $projectName

	printHtmlHeader "Projects list"

	echo "<h1>$projectName logfiles</h1>"
	echo "<table>"
	local content=$(ls -hlt $projectFolder | grep -v 'total' | awk -v h=$hostname -v s=$thisFile -v f=$projectFolder -v n=$projectName '{print "<tr><td>"$1"&nbsp;&nbsp;</td><td>"$2"&nbsp;&nbsp;</td><td>"$3"&nbsp;&nbsp;</td><td>"$4"&nbsp;&nbsp;</td><td>"$5"&nbsp;&nbsp;</td><td>"$6"&nbsp;&nbsp;</td><td>"$7"&nbsp;&nbsp;</td><td>"$8"&nbsp;&nbsp;</td><td><a href=\""h"/"s"?logfile="$9"&page=0&project="n"\">"$9"</a>&nbsp;&nbsp;</td><td><a href=\""h"/"s"?logfile="$9"&page=-1&project="n"\">(full)&nbsp;&nbsp;</a></td><td><a href=\""h"/"s"?downloadProject="n"&downloadFile="$9"\">Download</a></td></tr>"}' )
	echo "$content"
	echo "</table>"
	printHtmlFooter
	trace "listProjectFiles EXIT"
}

listTargetLogfile(){
	trace "listTargetLogfile ENTRY"
	local fileName=$1
	local pageOrder=$2
	local projectName=$3

	setProjectFolder $projectName	

	local size=$(stat -c%s $projectFolder/$fileName)
	local pageCount=$(( size / chunkSize ))
	local prev=$(( pageOrder - 1 ))
	local next=$(( pageOrder + 1 ))
	local prevLink="<a href=\"$hostname/$thisFile?logfile=$1&page=$prev&project=$projectName\">prev</a>"
	local nextLink="<a href=\"$hostname/$thisFile?logfile=$1&page=$next&project=$projectName\">next</a>"
	local midLink=""

	if [ "$prev" -lt "0" ]; then
		prevLink=""
	fi
	if [ "$next" -gt "$pageCount" ]; then
		nextLink=""
	fi
	local i=0
	while [ $i -le $pageCount ]; do
		midLink="$midLink <a href=\"$hostname/$thisFile?logfile=$1&page=$i&project=$projectName\">$(( i + 1 ))</a>&nbsp;"
		let i=i+1
		let shouldBeNewLine=$i%$maxLinkNumberPerPage
		if [ "$shouldBeNewLine" -eq "0" ]; then
			midLink="$midLink <br/>"
		fi
	done

	#Print the logfile in chunks.
	printHeader
	printHtmlHeader "Info $server_name"
	echo "<h1>$1</h1>"
	if [[ "$pageOrder" -ne "-1" ]]; then
		echo "<h2>($(( pageOrder + 1 ))/$(( pageCount + 1 )) ) $prevLink&nbsp;$midLink&nbsp;$nextLink</h2>"
		echo "<font face=\"$logFontType\">"
		if [[ "$searchRegexp" != "." ]]; then
			dd if="$projectFolder/$fileName" bs=$chunkSize count=1 skip=$pageOrder | grep -i "$filterRegexp" | sed ':a;N;$!ba;s/\n/\<br\/\>/g' | sed "s/$searchRegexp/\<b\>$searchRegexp\<\/b\>/g"
		else
			dd if="$projectFolder/$fileName" bs=$chunkSize count=1 skip=$pageOrder | grep -i "$filterRegexp" | sed ':a;N;$!ba;s/\n/\<br\/\>/g'
		fi
		echo "</font>"
		echo "<h2>($2/$pageCount) $prevLink&nbsp;$midLink&nbsp;$nextLink</h2>"
	else
		echo "<font face=\"$logFontType\">"
		if [[ "$searchRegexp" != "." ]]; then
			cat $projectFolder/$fileName | grep -i "$filterRegexp" | sed ':a;N;$!ba;s/\n/\<br\/\>/g' | sed "s/$searchRegexp/\<b\>$searchRegexp\<\/b\>/g"
		else
			cat $projectFolder/$fileName | grep -i "$filterRegexp" | sed ':a;N;$!ba;s/\n/\<br\/\>/g' 
		fi
		echo "</font>"
	fi
	printHtmlFooter
	trace "listTargetLogfiles EXIT"
}

checkFile(){
	trace "checkFile ENTRY"
	if [[ "$logfile" == *".."* ]]; then
		paramStatus="error"
	else
		paramStatus="ok"
	fi
	trace "checkFile EXIT"
}

download(){
        trace "download ENTRY"
	setProjectFolder $1
	local fileName=$2
	local absFile=$projectFolder/$fileName
	local mimeType=$(xdg-mime query filetype $absFile)
        echo "Content-type: $mimeType"
	echo "Content-disposition: attachment; filename=\"$fileName\""
        echo ""
	cat $absFile
        trace "download EXIT"
}

internalRouting(){
	trace "internalRouting ENTRY"
	if [[ "$downloadProject" != "" ]]; then
		download $downloadProject $downloadFile
		return
	fi
	if [[ "$projectList" != "" ]]; then
		listProjectFiles $projectList
		return
	fi
	if [[ "$logfile" != "" ]]; then
		listTargetLogfile $logfile $page $project
		return
	fi
	if [[ "$settings" != "" ]]; then
		listSettings
		return
	fi
	if [[ "$c_searchRegexp" != "" ]]; then
                saveSettings
                listProjectsWithoutHeader
                return
        fi
	listProjects
	trace "internalRouting EXIT"
}

main() {
	trace "main ENTRY"
	parseQueryString
	parseCookieParams
	checkFile
	trace "paramStatus set to $paramStatus"
	if [[ "$paramStatus" == "ok" ]]; then
		internalRouting
	else
		trace "error!!! using default page."
		listProjects
	fi
	trace "main EXIT"
}

main $@
exit 0


