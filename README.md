# Web-Server
Simple Multi-threaded Web Server

In this program I followed the steps of capturing the http stream between existing clients and servers, and write a web server that supports this same protocol. It builds on the JokeServer, which application does much of the same work. While the text of the assignment is quite long, the application itself is quite straightforward, and you might be surprised at how easily it can be written.

There are four+ phases in the development process:

1. Capture the HTTP protocol first-hand by developing some hacking / debugging skills (hacking in the good sense).
2. Return simple, static files on request from a browser client.
3. Return dynamically created HTML (build a directory HTML page dynamically)
4. Accept FORM input from the user and do back-end processing on the server to return computed values in (simple!) dynamically-created HTML.
