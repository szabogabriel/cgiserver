#!/bin/sh

echo "Content-type: text/html"
echo ""
echo "Yes! cgi-bin-1 $1<br/>"
#echo "$(env)"
echo "This is an error message." >&2
