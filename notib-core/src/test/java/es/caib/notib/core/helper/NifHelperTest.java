package es.caib.notib.core.helper;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class NifHelperTest {

    protected enum DocumentTipus {DNI, NIF, NIE, CIF}

    @Test
    @Parameters({
            "NIF, 000000A, false",
            "NIF, B, false",
            "NIF, 60697841J, true",
            "NIF, 46222996b, true",
            "NIF, 02802576A, true",
            "NIF, 59123507X, false",
            "NIF, 2802576A, false",
            "NIF, 70542415, false",
            "NIE, Y4628017V, true",
            "NIE, X8902926v, true",
            "NIE, x2551670G, true",
            "NIE, x2578755h, true",
            "NIE, Y4529519X, false",
            "NIE, Z1309772, false",
            "CIF, U23185713, true",
            "CIF, a25349325, true",
            "CIF, Y47708730, false",
            "CIF, 72362999, false"
    })
    public void isvalid(DocumentTipus tipus,
                        String document,
                        boolean expectedResult) {
        boolean valid = NifHelper.isvalid(document);
        assertThat("El " + tipus.name() + " " + document + " " + (expectedResult ? "no " : "") + "és vàlid.", expectedResult == valid);
    }

    @Test
    @Parameters({
            "CIF, U23185713, true",
            "CIF, a25349325, true",
            "CIF, Y47708730, false",
            "CIF, 72362999, false",
            "NIF, 60697841J, false",
            "NIE, Y4628017V, false"
    })
    public void isValidCif(DocumentTipus tipus,
                           String document,
                           boolean expectedResult) {
        boolean valid = NifHelper.isValidCif(document);
        assertThat("El CIF " + document + " " + (expectedResult ? "no " : "") + "és vàlid.", expectedResult == valid);
    }

    @Test
    @Parameters({
            "NIF, 60697841J, true",
            "NIF, 46222996b, true",
            "NIF, 02802576A, true",
            "NIF, 59123507X, false",
            "NIF, 2802576A, false",
            "NIF, 70542415, false",
            "NIE, Y4628017V, true",
            "NIE, X8902926v, true",
            "NIE, x2551670G, true",
            "NIE, x2578755h, true",
            "NIE, Y4529519X, false",
            "NIE, Z1309772, false",
            "CIF, A25349325, false"
    })
    public void isValidNifNie(DocumentTipus tipus,
                              String document,
                              boolean expectedResult) {
        boolean valid = NifHelper.isValidNifNie(document);
        assertThat("El NIF/NIE " + document + " " + (expectedResult ? "no " : "") + "és vàlid.", expectedResult == valid);
    }

}