#!/bin/bash

# Parameters
readonly tracing=${1:-false}
readonly hostname="http://localhost:8080"
readonly thisFile="kvstore.cgi"
readonly storeFolder="/tmp/store"
readonly tmpFolder="/tmp/store/tmp"

trace(){
	if [[ ${tracing} == "true" ]]; then
		echo $1
	fi
}

init(){
	if [[ ! -d ${storeFolder} ]]; then
		mkdir ${storeFolder}
	fi
	if [[ ! -d ${tmpFolder} ]]; then
		mkdir ${tmpFolder}
	fi
}

parseQueryString(){
	trace "parseQueryString ENTRY"
	declare -a QUERYSTRING=$(echo "${QUERY_STRING}" | sed 's/QUERY_STRING=//g' | sed 's/&/ /g')
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

sendEmpty(){
	echo "Content-type: text/plain"
	echo ""
}

handleGet(){
	trace "handleGet ENTRY"
	local fileToReturn=${storeFolder}/${key}

	if [[ -f ${fileToReturn} ]]; then
		echo "Content-type: $(file -b --mime-type ${fileToReturn})"
		echo ""
		cat ${fileToReturn}
	else
		sendEmpty
	fi
	trace "handleGet EXIT"
}

handlePost(){
	trace "handlePost ENTRY"
	local fileToSave=${storeFolder}/${key}

	mv ${tmpFile} ${fileToSave}

	sendEmpty

	trace "handlePost EXIT"
}

handleEmpty(){
	trace "handleEmpty ENTRY"
	echo "Content-type: text/plain\n"
	echo "No data."
	trace "handleEmpty EXIT"
}

main() {
	trace "main ENTRY"
	parseQueryString
	case ${METHOD} in
		GET) handleGet ;;
		POST) handlePost ;;
		PUT) handlePut ;;
		*) handleEmpty ;;
	esac
	
	trace "main EXIT"
}

init

if [[ ${METHOD} == "POST" || ${METHOD} == "PUT" ]]; then
	tmpFile=$(mktemp -q ${tmpFolder}/tmp_file_XXXXXX)
	while read line; do
		echo ${line} >> ${tmpFile}
	done
fi
main $@
exit 0