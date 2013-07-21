package com.allenrothschild.dataaccess

import com.marklogic.client.DatabaseClient
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.Transaction
import com.marklogic.client.document.XMLDocumentManager
import com.marklogic.client.io.DOMHandle
import com.marklogic.client.io.SearchHandle
import com.marklogic.client.io.StringHandle
import com.marklogic.client.query.KeyValueQueryDefinition
import com.marklogic.client.query.MatchDocumentSummary
import com.marklogic.client.query.QueryManager
import com.marklogic.client.query.StringQueryDefinition
import org.w3c.dom.Document
import javax.xml.namespace.QName

class MarkLogicDocumentManager {
    private DatabaseClient _databaseClient
    private XMLDocumentManager _documentManager
    private QueryManager _queryManager
    private Transaction _transaction

    String host, user, password
    DatabaseClientFactory.Authentication authType
    int port

    def getDatabaseClient() {
        if (!_databaseClient) {
            _databaseClient = DatabaseClientFactory.newClient(host, port, user, password, authType)
        }

        _databaseClient
    }

    def getDocumentManager() {
        if (!_documentManager) {
            _documentManager = databaseClient.newXMLDocumentManager()
        }

        _documentManager
    }

    def getQueryManager() {
        if (!_queryManager) {
            _queryManager = databaseClient.newQueryManager()
        }

        _queryManager
    }

    def getTransaction() {
        if (!_transaction) {
            _transaction = databaseClient.openTransaction()
        }

        _transaction
    }

    def startTransaction() {
        transaction
    }

    def commitTransaction() {
        transaction.commit()
        _transaction = null
    }

    def rollbackTransaction() {
        transaction.rollback()
    }

    def createDocument(String uri, String documentXml) {
        StringHandle content = new StringHandle(documentXml)

        // TODO: should we be passing a metadata parameter?
        documentManager.write(uri, content, transaction)
    }

    String readDocument(String uri) {
        DOMHandle handle = new DOMHandle()
        documentManager.read(uri, handle)

        //---------------------------------------------------------------------------
        def rootName = handle.get().getDocumentElement().getTagName()
        println 'YO, THE ROOT IS: ' + rootName
        //---------------------------------------------------------------------------

        return handle.toString()
    }

    def deleteDocument(String uri) {
        documentManager.delete(uri)
    }

    MatchDocumentSummary[] searchDocumentsByElementValue(String elementName, String elementValue) {
        SearchHandle resultsHandle = new SearchHandle()
        KeyValueQueryDefinition query = queryManager.newKeyValueDefinition()
        query.put(queryManager.newElementLocator(new QName(elementName)), elementValue)
        queryManager.search(query, resultsHandle)

        resultsHandle.getMatchResults()
    }

    MatchDocumentSummary[] searchDocumentsBySearchString(String searchString) {
        SearchHandle resultsHandle = new SearchHandle()
        StringQueryDefinition query = queryManager.newStringDefinition()
        query.setCriteria(searchString)
        queryManager.search(query, resultsHandle)

        resultsHandle.getMatchResults()
    }

    def close() {
        if (documentManager) {
            _databaseClient.release()
        }
    }
}
