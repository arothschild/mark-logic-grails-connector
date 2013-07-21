package com.allenrothschild.repository

import com.allenrothschild.dataaccess.MarkLogicDocumentManager
import com.marklogic.client.query.MatchDocumentSummary
import org.w3c.dom.Document

class TuneRepository {
    MarkLogicDocumentManager markLogicDocumentManager

    def saveUsingRestApi(String checklistXml, String checklistUri) {
        markLogicDocumentManager.startTransaction()
        try {
            markLogicDocumentManager.createDocument(checklistUri, checklistXml)
        } catch(Exception e) {
            markLogicDocumentManager.rollbackTransaction()
            throw(e)
        }
        markLogicDocumentManager.commitTransaction()
    }

    String getUsingRestApi(String checklistUri) {
        markLogicDocumentManager.readDocument(checklistUri)
    }

    def deleteUsingRestApi(String checklistUri) {
        markLogicDocumentManager.deleteDocument(checklistUri)
    }

    MatchDocumentSummary[] searchDocumentsByElementValue(String elementName, String elementValue) {
        MatchDocumentSummary[] results = markLogicDocumentManager.searchDocumentsByElementValue(elementName, elementValue)

        results
    }

    MatchDocumentSummary[] searchDocumentsBySearchString(String searchString) {
        MatchDocumentSummary[] results = markLogicDocumentManager.searchDocumentsBySearchString(searchString)

        results
    }
}
