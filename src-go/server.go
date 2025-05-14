package main

import (
        "log"
        "net/http"
        "net/http/cgi"
        "path/filepath"
)

func main() {
        // Serve CGI scripts from ./cgi-bin/
        http.Handle("/", http.StripPrefix("/", http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
                scriptPath := filepath.Join("/home/gszabo/Projects/Temp/cgiserver/cgi-bin", r.URL.Path)
                h := &cgi.Handler{
                        Path: scriptPath,
                        Dir:  "./cgi-bin",
                }
                h.ServeHTTP(w, r)
        })))

        log.Println("Serving CGI on http://localhost:8080")
        log.Fatal(http.ListenAndServe(":8080", nil))
}
