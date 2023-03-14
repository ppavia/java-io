package fr.cimut.ged.entrant.utils;

import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import org.apache.log4j.Logger;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GedeIdUtilsTest {
    private static final Logger LOGGER = Logger.getLogger(GedeIdUtilsTest.class);

    @Test
    public void getEddocIdFromIdsTest() throws CimutDocumentException {
        Long idstar = 12346L, tsstar = 12345678912365L;
        String formatedId = "";
        formatedId = GedeIdHelper.getEddocIdFromIds(idstar, tsstar);
        assertTrue(formatedId.matches("([0-9]{12})[_]{1}(\\d{14})"));
    }

    @Test
    public void getEddocIdFromIdsBadIdstarArgTest() {
        Long idstar = 1234600000000000L, tsstar = 12345678912365L;
        try {
            GedeIdHelper.getEddocIdFromIds(idstar, tsstar);
            fail();
        } catch (CimutDocumentException e) {
            assertThat(e.getMessage(), is(new StringBuilder().append("idstar invalid : ").append(idstar).toString()));
        }
    }

    @Test
    public void getEddocIdFromIdsBadTsstarArgTest() {
        Long idstar = 12346L, tsstar = 12345678365L;
        try {
            GedeIdHelper.getEddocIdFromIds(idstar, tsstar);
            fail();
        } catch (CimutDocumentException e) {
            assertThat(e.getMessage(), is(new StringBuilder().append("tsstar invalid : ").append(tsstar).toString()));
        }
    }
}
