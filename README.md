

# How to run

From the command line, at the root of the project:

`mvn clean compile exec:java -Dexec.mainClass="Main" -Dexec.args="<my url>`

for example

`mvn clean compile exec:java -Dexec.mainClass="Main" -Dexec.args="http://wiprodigital.com"`


# Mechanism

The idea is to use a queue to process the urls
- poll from the queue until there are no more urls to crawl
- whenever a url is crawled, the Jsoup utility gathers the list of urls on this page and add them to the queue.


# TODO

- Run the app with multiple threads
- Report on the error status associated with pages that can not be crawled
- Improve the layout of the report, so it's more of a tree-like hierarchical report vs what is now a list




