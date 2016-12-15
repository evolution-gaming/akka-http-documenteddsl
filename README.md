# AKKA-HTTP-DOCUMENTEDDSL

[![Build Status](https://travis-ci.org/evolution-gaming/akka-http-documenteddsl.svg?branch=develop)](https://travis-ci.org/evolution-gaming/akka-http-documenteddsl)
[![Coverage Status](https://coveralls.io/repos/evolution-gaming/akka-http-documenteddsl/badge.svg)](https://coveralls.io/r/evolution-gaming/akka-http-documenteddsl)
[![version](https://api.bintray.com/packages/evolutiongaming/maven/akka-http-documenteddsl/images/download.svg) ](https://bintray.com/evolutiongaming/maven/akka-http-documenteddsl/_latestVersion)

## The Problem
 Want to provide [Swaggerrish](http://swagger.io/) api documentation of your app? Or [RAML](http://raml.org/)?
 
 [Spray](http://spray.io) and [Akka-Http](http://doc.akka.io/docs/akka-http/current/scala.html) both have perfect routing dsl and at the first sight it will be easy to extract
 the data to build something like swagger on top of it. But looking deeper you see that these was designed
 being not introspective. In short you are unable to distinguish one directive from another
 in runtime (or in compile time using macros) and therefore you can't build the picture of your route behaviour.
 
 So not Spray nor Akka-Http have any mechanisms to extract documentation from their routing dsl.
 
## Provided Solution
 For our needs we selected number of directives which could form the api documentation
 and wrapped them into neatly documented directives. We then added a few of our own directives
 to make us able to describe specifics of our apis and now we are able to generate some internally
 invented format describing our api. It is not a Swagger or a Raml, it is something simpler but still
 rich enough to provide users with all the needed information about api. Generated documentation then could be
 available in json form, and/or presented via html5 frontend.
 
### Introduction
 To use documented directives you need to import couple things.
```scala
import akka.http.documenteddsl.directives.DDirectives._
```
 This is an entry point to documented dsl.
 
> Next important thing - you need to have _org.coursera.autoschema.AutoSchema_ which could be accessed implicitly in your scope.

 There are number of documented directives:
 - Method directives
   - GET
   - POST
   - PUT
   - DELETE
   - HEAD
   - OPTIONS
 - Path directives
   - Path
 - Parameter directives
   - Param
   - OptParam
 - Form directives
   - FormField
   - OptFormField
 - Marshalling directives
   - In
 - Unmarshalling directives
   - Out
 - Session directives
   - Session
 - Documentation directives
   - Category
   - Title
   - Description

  Here are some primitive example of how your code may look like if using documented dsl.

```scala
implicit object autoSchema extends AutoSchema with DocumentedTypeMappings

private val FindAll = Category("Api", "Resource") & Title("Find All") & Description("Returns all resource entries") &
                      Path("resources") & GET &
                      Out[Set[ExampleResource]]

private val Find    = Category("Api", "Resource") & Title("Find") & Description("Returns specified resource entrie") &
                      Path("resources" / Segment[String]) & GET &
                      Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")

private val Create  = Category("Api", "Resource") & Title("Create") & Description("Creates a new resource entry") &
                      Path("resources") & POST &
                      In(CreateExample) & Out[ExampleResource]

private val Update  = Category("Api", "Resource") & Title("Create") & Description("Updates specified resource entry") &
                      Path("resources" / Segment[String]) & PUT &
                      In(UpdateExample) & Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")

private val Delete  = Category("Api", "Resource") & Title("Create") & Description("Deletes specified resource entry") &
                      Path("resources" / Segment[String]) & DELETE &
                      Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")

lazy val route: DRoute = {
  FindAll {complete(collection)} |~|
  Find    {find} |~|
  Create  {create} |~|
  Update  {update} |~|
  Delete  {delete}
}

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

private def find(id: String): Route = ???
private def create(payload: CreateResource): Route = ???
private def update(id: String, payload: UpdateResource): Route = ???
private def delete(id: String): Route = ???
```

 And here is the documentation generated from this example
```json
GET http://localhost:8080/api.json

{
  "routes" : [ {
    "uid" : "14906C3B966588C5BAD6B46E",
    "method" : "GET",
    "path" : "resources",
    "out" : {
      "success" : [ {
        "status" : {
          "code" : 200,
          "detail" : "OK"
        },
        "contentType" : "application/json",
        "schema" : {
          "type" : "array",
          "items" : {
            "title" : "ExampleResource",
            "type" : "object",
            "required" : [ "name", "id" ],
            "properties" : {
              "description" : {
                "type" : "string"
              },
              "id" : {
                "type" : "string"
              },
              "name" : {
                "type" : "string"
              }
            }
          }
        }
      } ],
      "failure" : [ ]
    },
    "title" : "Find All",
    "description" : "Returns all resource entries",
    "category" : [ "Api", "Resource" ]
  }, {...} ]
}
```

 Default DocumentationRoutes also provide the way to decouple routes requests.
 You use OPTIONS to request api toc. And then you can request route by route using uids.
```json
OPTIONS http://localhost:8080/api.json

{
  "Api": {
    "Resource": {
      "uid": {
        "14906C3B8B40240130BFA42E": "Delete",
        "14906C3B95EB76C2E1EA5DEE": "Update",
        "14906C3B96287FC336629FAE": "Create",
        "14906C3B965646849338300E": "Find",
        "14906C3B966588C5BAD6B46E": "Find All"
      }
    }
  }
}

GET http://localhost:8080/api.json/14906C3B8B40240130BFA42E

{
  "uid" : "14906C3B966588C5BAD6B46E",
  "method" : "GET",
  "path" : "resources",
  "out" : {
    "success" : [ {
      "status" : {
        "code" : 200,
        "detail" : "OK"
      },
      "contentType" : "application/json",
      "schema" : {
        "type" : "array",
        "items" : {
          "title" : "ExampleResource",
          "type" : "object",
          "required" : [ "name", "id" ],
          "properties" : {
            "description" : {
              "type" : "string"
            },
            "id" : {
              "type" : "string"
            },
            "name" : {
              "type" : "string"
            }
          }
        }
      }
    } ],
    "failure" : [ ]
  },
  "title" : "Find All",
  "description" : "Returns all resource entries",
  "category" : [ "Api", "Resource" ]
}
```

 Project contain small api example built on documented directives to show you how does it look like.
 [Please take a look at it](https://github.com/evolution-gaming/akka-http-documenteddsl/src/examples/scala).
  
### Known issues
 - Only Play Json supported now
 - Your payloads always treated as json payloads
 - Lack of ability to describe route hierarchically  