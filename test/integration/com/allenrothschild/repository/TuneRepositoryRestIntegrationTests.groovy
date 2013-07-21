package com.allenrothschild.repository

import com.allenrothschild.dataaccess.MarkLogicDocumentManager
import com.allenrothschild.mixins.XmlCapabilities
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.query.MatchDocumentSummary
import org.junit.After
import org.junit.Before
import org.junit.Test

@grails.util.Mixin(XmlCapabilities)
class TuneRepositoryRestIntegrationTests extends GroovyTestCase {
    TuneRepository tuneRepository

    static final TUNE_BASE_URI = '/example/checklist/'
    static final TUNE_ROOT_NAME = 'tune'
    static final ARTIST_NAME_01 = 'Mad Cadddies'
    static final ARTIST_NAME_02 = 'Dam-Funk'
    static final TUNE_TITLE_01 = '10 West'
    static final TUNE_XML_01 = '<tune><id>1</id><title>'+TUNE_TITLE_01+'</title><artist>'+ARTIST_NAME_01+'</artist><album>Just One More</album></tune>'
    static final TUNE_XML_02 = '<tune><id>2</id><title>'+TUNE_TITLE_01+'</title><artist>'+ARTIST_NAME_02+'</artist><album>Toeachizown</album></tune>'

    @Before
    void before() {
        tuneRepository = new TuneRepository()
        tuneRepository.markLogicDocumentManager = new MarkLogicDocumentManager()
        tuneRepository.markLogicDocumentManager.with {
            host = 'localhost'
            port = 8011
            user = 'rest-admin'
            password = 'x'
            authType = DatabaseClientFactory.Authentication.DIGEST
        }
    }

    @After
    void after() {
        tuneRepository.markLogicDocumentManager.close()
    }

    private void cleanUpTune(String newChecklistUri) {
        boolean errorCaught = false;
        if (tuneRepository) {
            tuneRepository.deleteUsingRestApi(newChecklistUri)
            try {
                tuneRepository.getUsingRestApi(newChecklistUri)
            } catch (Exception e) { errorCaught = true }
            assert errorCaught == true
        } else {
            throw IllegalStateException
        }
    }

    @Test
    void shouldCreateNewTuneUsingTransaction() {
        String newChecklistUri = TUNE_BASE_URI + 'SampleNewChecklist.xml'

        tuneRepository.saveUsingRestApi(TUNE_XML_01, newChecklistUri)

        //---------------------------------------------------------------------------
        def documentXml = tuneRepository.getUsingRestApi(newChecklistUri)
        println 'YO, THE XML IS: ' + documentXml
        //---------------------------------------------------------------------------

        def model = XmlCapabilities.slurpXml(documentXml)

        final actualRootName = model.name()
        final expectedRootName = TUNE_ROOT_NAME
        assertEquals(expectedRootName, actualRootName)

        final actualArtistName = model.artist.text()
        final expectedArtistName = ARTIST_NAME_01
        assertEquals(expectedArtistName, actualArtistName)

        cleanUpTune(newChecklistUri)
    }

    @Test
    void shouldFindDocumentsByElementValue() {
        String newChecklistUri01 = TUNE_BASE_URI + 'SampleNewChecklist01.xml'
        String newChecklistUri02 = TUNE_BASE_URI + 'SampleNewChecklist02.xml'
        String searchElementName = 'title'
        String searchElementValue = '10 West'

        tuneRepository.saveUsingRestApi(TUNE_XML_01, newChecklistUri01)
        tuneRepository.saveUsingRestApi(TUNE_XML_02, newChecklistUri02)

        MatchDocumentSummary[] searchResults = tuneRepository.searchDocumentsByElementValue(searchElementName, searchElementValue)

        assert searchResults.size() == 2

        cleanUpTune(newChecklistUri01)
        cleanUpTune(newChecklistUri02)
    }

    @Test
    void shouldFindDocumentsBySearchString() {
        String newChecklistUri01 = TUNE_BASE_URI + 'SampleNewChecklist01.xml'
        String newChecklistUri02 = TUNE_BASE_URI + 'SampleNewChecklist02.xml'
        String searchString = '10 We'

        tuneRepository.saveUsingRestApi(TUNE_XML_01, newChecklistUri01)
        tuneRepository.saveUsingRestApi(TUNE_XML_02, newChecklistUri02)

        MatchDocumentSummary[] searchResults = tuneRepository.searchDocumentsBySearchString(searchString)

        assert searchResults.size() == 2

        cleanUpTune(newChecklistUri01)
        cleanUpTune(newChecklistUri02)
    }

    @Test
    void shouldRollbackTransaction() {
        String newChecklistUri = TUNE_BASE_URI + 'SampleNewChecklist.xml'
        Integer badPortNumber = 4321
        Boolean errorCaught = false

        tuneRepository.markLogicDocumentManager.port = badPortNumber
        try {
            tuneRepository.saveUsingRestApi(TUNE_XML_01, newChecklistUri)
        } catch (Exception e) {
            errorCaught = true
        }
        assert errorCaught == true
    }
}
