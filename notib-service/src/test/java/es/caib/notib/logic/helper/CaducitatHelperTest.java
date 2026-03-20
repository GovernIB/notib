package es.caib.notib.logic.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class CaducitatHelperTest {

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void whenSumarDiesLaboralsSenseCapSetmana_ThenOK() throws Exception {
        // Given
        Date dia = df.parse("01/02/2022"); // Dimarts

        // When
        Date fi = CaducitatHelper.sumarDiesLaborals(dia, 2);
        System.out.println("Fi esperat: '03/02/2022', Fi: '" + df.format(fi) + "'");

        // Then
        Assert.assertEquals("03/02/2022", df.format(fi));
    }

    @Test
    public void whenSumarDiesLaboralsAmbCapSetmana_ThenOK() throws Exception {
        // Given
        Date dia = df.parse("01/02/2022"); // Dimarts

        // When
        Date fi = CaducitatHelper.sumarDiesLaborals(dia, 10);
        System.out.println("Fi esperat: '18/02/2022', Fi: '" + df.format(fi) + "'");

        // Then
        Assert.assertEquals("15/02/2022", df.format(fi));
    }

    @Test
    public void whenSumarDiesNaturalsSenseCapSetmana_ThenOK() throws Exception {
        // Given
        Date dia = df.parse("01/02/2022"); // Dimarts

        // When
        Date fi = CaducitatHelper.sumarDiesNaturals(dia, 2);
        System.out.println("Fi esperat: '03/02/2022', Fi: '" + df.format(fi) + "'");

        // Then
        Assert.assertEquals("03/02/2022", df.format(fi));
    }

    @Test
    public void whenSumarDiesNaturalsAmbCapSetmana_ThenOK() throws Exception {
        // Given
        Date dia = df.parse("01/02/2022"); // Dimarts

        // When
        Date fi = CaducitatHelper.sumarDiesNaturals(dia, 15);
        System.out.println("Fi esperat: '11/02/2022', Fi: '" + df.format(fi) + "'");

        // Then
        Assert.assertEquals("16/02/2022", df.format(fi));
    }
}