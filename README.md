# CGI Server

This project aims to be a fairly small implementation of an HTTP server based around Java sockets which forwards every request to the CGI scripts defined in the URL.

It executes the scripts which are in the provided CGI server folder without any additional check. The file executed is the one identified in the URL. For example http://localhost:8000/index.cgi will execute the index file. The extension must be provided in the URL as well, but not the CGI folder.

If there is no path provided, the CGI server will look for an `index` script automatically. The default index page can be configured.

## Configuration

You can provide configuration attributes during start the start of the application. Example of adding configuration:

`java -jar cgiserver.jar -cgiFolder /var/www/cgi-bin -port 50000` 

The following configuration options are possible:

- -cgiFolder - folder of the CGI scripts to be executed. Ideally this should be an absolute path.
- -execFolder - folder from which the scripts will be executed.
- -urlPrefix - part of the URL, which is not considered as part of the script to be executed
- -help - prints help
- -port - port on which the application will listen
- -threads - number of worker threads to be used for the socket handling
- -socketBacklog - socket backlog size set on the `ServerSocket`.
- -host - host to listen to. By default the localhost will be used.
- -index - the default index page, if no script is provided.

## Issues

There is currently no pre-forking happening, meaning that the performance of the server is highly dependent on the fork-performance of the underlying server.
