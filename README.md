mark-logic-grails-connector
===========================

This project reprsents a simple Groovy connector for MarkLogic's Java REST API, for usage in a Grails project. The file structure is similar to how you might set up the artifacts in a Grails project. The sample documents that are being crated are Tune documents, representing song information.

The connector class itself can be found at [src/groovy/com/allenrothschild/dataaccess/MarkLogicDocumentManager.groovy](src/groovy/com/allenrothschild/dataaccess/MarkLogicDocumentManager.groovy).

A respository for Tune data can be found at [src/groovy/com/allenrothschild/repository/TuneRepository.groovy](src/groovy/com/allenrothschild/repository/TuneRepository.groovy).

Integration tests for the repository can be found at [test/integration/com/allenrothschild/repository/TuneRepositoryRestIntegrationTests.groovy](test/integration/com/allenrothschild/repository/TuneRepositoryRestIntegrationTests.groovy).
