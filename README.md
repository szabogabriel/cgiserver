# CGI Server

This project aims to be a fairly small implementation of an HTTP server based around Java sockets which forwards every request to the CGI scripts defined in the URL.

It executes the scripts which are in the procided CGI server folder without any additional check. The file executed is the one identified in the URL. For example http://localhost:8000/index.cgi will execute the index file. The extension must be provided in the URL as well, but not the CGI folder.

If there is no path provided, the CGI server will look for an index script automatically. This is currently not configurable.
