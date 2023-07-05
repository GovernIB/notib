package es.caib.notib.core.service.ws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;
import es.caib.notib.core.api.dto.AdviserResponseDto;
import es.caib.notib.core.api.dto.adviser.Acuse;
import es.caib.notib.core.api.dto.adviser.EnviamentAdviser;
import es.caib.notib.core.api.dto.adviser.Receptor;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/** Prova del WS Adviser des de Notific@ que rep notificacions sobre
 * canvis d'estat de notificacions o de certificat.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificaAdviserRestTest {
	
	private static final String BASE_ADDRESS = "http://localhost:8280/notib";
	private static final String ENDPOINT_ADDRESS = BASE_ADDRESS + "/adviser/sincronitzar";
	private static final String USER = "admin";
	private static final String PASS = "admin";
	private static final String EMISOR_DIR3 = "EA0004518";
	private static final String CERIFICACIO_B64 =
			  "JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURl"
			+ "Y29kZT4+CnN0cmVhbQp4nDPQM1Qo5ypUMFAwALJMLU31jBQsTAz1LBSKUrnCtRTyIHJAWJTO5RTC"
			+ "ZWoGlDI3NwEqDklR0HczVDA0UghJi7YxMLTTNbQxMDIwNjAxMLWLDfHicg3hCuQKVAAADMAVAgpl"
			+ "bmRzdHJlYW0KZW5kb2JqCgozIDAgb2JqCjk4CmVuZG9iagoKNSAwIG9iago8PC9MZW5ndGggNiAw"
			+ "IFIvRmlsdGVyL0ZsYXRlRGVjb2RlL0xlbmd0aDEgNzIwOD4+CnN0cmVhbQp4nOVYfXBTV3Y/9z59"
			+ "25ae/IVBYD3xMNiRLRkLgwFjC1uSZQxY/kplE9sSlmwLsCUk2WzYzeBslyQjwoZmExISpqTbdCez"
			+ "m5k8A906O9ngzCTT7rRpsp00k91A4rY70z8WLyRbtp1ssHvu07PBbD5mup3pH32W3v2dz3vuOec+"
			+ "vet0ciIKuTAFHLiHxsKJ1WoVBYB/ACD5Q5NpYbHJLSCeA6DFw4mRsXLXh78B4P4LQKseOfLg8HsP"
			+ "//smgBw0Ub84Gg1HZr75LzUA/IvI2DqKjL0LD2qRvor0htGx9Dca6NkCADOSUHIkPhSeVPMc0iVI"
			+ "G8fC30j8peZhFdLlSAvj4bHo/uj3n0LaD6Dbm4in0hHYsAiw+ikmTySjiVt9z+iQvozxpZFH8I9d"
			+ "uQg1jKacSg3/jy/1aSgCv3oXmCAh31dc3Muwmo2L11feF/Yufva/GYUuOzwLP4DLcBp+Af2KwAcB"
			+ "iMEEcu6+3oCfI5ddAeiDH0LmS9y+DDMoz+qF4Ak49yV6AXgGLsHfrpglAGPwTYzlr+EXZDP8DFsl"
			+ "Dp8SHTwMb6HXT5G374tcUSPehmU4fBf3Q3ienoI99FdInGMS6qQ8vAnnyQB6TuM6Ty+vuP4PnD4K"
			+ "D+G9C0ZhErF8qXd9/kvQL/4WV/UQ7IFvw244cpfFa+QCZ8D6dcMFzOkbMs+5JNT6uUP0x5Te/h4S"
			+ "fwYj+A0TXDs9ze0Gj9pMcLe4vb3Bnu6uzo5A+/59e9v2tPpbfF5Pc9Nud2PDrvqdO7bXbdtau7na"
			+ "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"
			+ "rkfDLVqZpedgWbkvNOi2DqLSgb5qa19LhbXAld+jxmBVqGjirFwj187FuSe4K5xW1xkotXbgdy5w"
			+ "M0BN7dZ2Z7v8fh1us6GjPYk9U3u4Vl+F1d9SZzW1WFucLe+0fNxyo0Uz2EIu4Mf3iu+Kj3P7Kpw+"
			+ "t6/U5lvrt/QUu4p6eJephxLoIS7ocZoWTdRkGjSdMHEmaAQ6VUzUZIacme7ustvbZrSLnW2SLnBA"
			+ "Io9JZV3s7u7okzSPSdDTdyA4Tch3e0+ePg1N69qkmq6gFFrX2yZFELgZmELAr5suhqbeVCrNziJ2"
			+ "PJEgnMA72CeQNZDKMsG+JAZ7iqRSkEoRO5PJEDmQsjM24zAbgpYDKWA3JrXLWgylUiUD/w02VnuI"
			+ "CmVuZHN0cmVhbQplbmRvYmoKCjYgMCBvYmoKNDI4OAplbmRvYmoKCjcgMCBvYmoKPDwvVHlwZS9G"
			+ "b250RGVzY3JpcHRvci9Gb250TmFtZS9CQUFBQUErTGliZXJhdGlvblNlcmlmCi9GbGFncyA0Ci9G"
			+ "b250QkJveFstMTc2IC0zMDMgMTAwNSA5ODFdL0l0YWxpY0FuZ2xlIDAKL0FzY2VudCA4OTEKL0Rl"
			+ "c2NlbnQgLTIxNgovQ2FwSGVpZ2h0IDk4MQovU3RlbVYgODAKL0ZvbnRGaWxlMiA1IDAgUgo+Pgpl"
			+ "bmRvYmoKCjggMCBvYmoKPDwvTGVuZ3RoIDI0Ni9GaWx0ZXIvRmxhdGVEZWNvZGU+PgpzdHJlYW0K"
			+ "eJxdUMtuhDAMvOcrfNw9rBLYsr0gpGorJA59qLQfEBJDI5UkCuHA39cJ21bqIdGM7bFHw6/dY2dN"
			+ "5K/BqR4jjMbqgItbg0IYcDKWFSVoo+KN5V/N0jNO2n5bIs6dHV1dM/5GvSWGDQ4P2g14ZPwlaAzG"
			+ "TnD4uPbE+9X7L5zRRhCsaUDjSHuepH+WM/KsOnWa2iZuJ5L8DbxvHqHMvNitKKdx8VJhkHZCVgvR"
			+ "QN22DUOr//WqXTGM6lMGmixoUohKNITLjO/LhM8ZX9qE7/b6JeFqrxd5921LupJi+HEPag2BnOes"
			+ "suVk1lj8jdM7n1T5fQOwu3a7CmVuZHN0cmVhbQplbmRvYmoKCjkgMCBvYmoKPDwvVHlwZS9Gb250"
			+ "L1N1YnR5cGUvVHJ1ZVR5cGUvQmFzZUZvbnQvQkFBQUFBK0xpYmVyYXRpb25TZXJpZgovRmlyc3RD"
			+ "aGFyIDAKL0xhc3RDaGFyIDUKL1dpZHRoc1szNjUgNTU2IDMzMyA1MDAgNTAwIDQ0MyBdCi9Gb250"
			+ "RGVzY3JpcHRvciA3IDAgUgovVG9Vbmljb2RlIDggMCBSCj4+CmVuZG9iagoKMTAgMCBvYmoKPDwv"
			+ "RjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERG"
			+ "L1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291"
			+ "cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kv"
			+ "Q1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8"
			+ "L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9L"
			+ "aWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cv"
			+ "UGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoY2Et"
			+ "RVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQXV0aG9yPEZFRkYwMDczMDA2OTAwNkYwMDZFMDAy"
			+ "MD4KL0NyZWF0b3I8RkVGRjAwNTcwMDcyMDA2OTAwNzQwMDY1MDA3Mj4KL1Byb2R1Y2VyPEZFRkYw"
			+ "MDRDMDA2OTAwNjIwMDcyMDA2NTAwNEYwMDY2MDA2NjAwNjkwMDYzMDA2NTAwMjAwMDM1MDAyRTAw"
			+ "MzE+Ci9DcmVhdGlvbkRhdGUoRDoyMDE3MTAyMDA5NDgxOCswMicwMCcpPj4KZW5kb2JqCgp4cmVm"
			+ "CjAgMTQKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDA1Mzc2IDAwMDAwIG4gCjAwMDAwMDAwMTkg"
			+ "MDAwMDAgbiAKMDAwMDAwMDE4OCAwMDAwMCBuIAowMDAwMDA1NTE5IDAwMDAwIG4gCjAwMDAwMDAy"
			+ "MDcgMDAwMDAgbiAKMDAwMDAwNDU3OSAwMDAwMCBuIAowMDAwMDA0NjAwIDAwMDAwIG4gCjAwMDAw"
			+ "MDQ3OTUgMDAwMDAgbiAKMDAwMDAwNTExMCAwMDAwMCBuIAowMDAwMDA1Mjg5IDAwMDAwIG4gCjAw"
			+ "MDAwMDUzMjEgMDAwMDAgbiAKMDAwMDAwNTYxOCAwMDAwMCBuIAowMDAwMDA1NzE1IDAwMDAwIG4g"
			+ "CnRyYWlsZXIKPDwvU2l6ZSAxNC9Sb290IDEyIDAgUgovSW5mbyAxMyAwIFIKL0lEIFsgPDJBQUU5"
			+ "MThDM0MxNzI5QkRBRDVGNDdDNTQyQ0JBMkVFPgo8MkFBRTkxOEMzQzE3MjlCREFENUY0N0M1NDJD"
			+ "QkEyRUU+IF0KL0RvY0NoZWNrc3VtIC9BNUVBQUYyOTA3RkY3OTZBQTVFNkM0MDQ5Mzk4Nzc2MAo+"
			+ "PgpzdGFydHhyZWYKNTkyNAolJUVPRgo=";
	private static final String CERIFICACIO_SHA1 = "b081c7abf42d5a8e5a4050958f28046bdf86158c";

	private List<String> enviamentsIds= Arrays.asList(
			"YHzpGhV7sxyue6OHGpPJ",
			"Df05GmjkEkIArer5XcHf",
			"9d09iyByDCcLts7yXaxD",
			"kjFPylzurHuRp7k91n1n",
			"21sRyeX61TEAJ2mWpLVB",
			"Ku28QFbjHIANHWaQxf5Q",
			"jZ8QD3URdLtQHdjYP7Nm",
			"W2P5lt6dxyg7I5xIYEmX",
			"SSRkoTv0N2yUduCXJyat",
			"TNbR4znqfrRMx2X0rYaT",
			"bLGkofGeYXoxQY3DkJgr",
			"BqazMsdNy55InIcT7hPo",
			"DALCmlWU9Tz4HYbHV4hO",
			"VeBp88MQ53bf5oYXsTG3",
			"KIKumdpORc5B2rFDbgiJ",
			"NMteGWehZcnLr1NqqrvX",
			"9z9vZTQAM2hAoiPTD2IT",
			"Y22CIyWekZeIEvl6fSr6",
			"HP0NTifbGiyodKLVYDca",
			"pbn3yntZE00dcO9HN4pk",
			"zGbg8gsSrX034LqPSaIG",
			"7Y24UHgcOSR2i1HCVGaB",
			"BWcdlIyKH30KV6nljvFn",
			"gdJiYnmV984X98lSXv2T",
			"elSKLKdkHsYXPhQgCB1J",
			"XWqHbG7Gv48Q7crFQlla",
			"PcKMrr5viC5SW1ulNOM4",
			"Lpc9u3KMdePzZvrKO6nh",
			"vPckh8fCpBkARggPTduV",
			"MoEGFXoEXKOaV6oopdM1",
			"ZpVD5plmp5nP1YlHe8LT",
			"5EMqOsir8PW7dXHtpxyj",
			"udHIFOCOSKY77btQCVcP",
			"z7EtvgPjbmEdff7dzs3B",
			"TX37MWGmF5j38LcbiGR5",
			"ZrRgRKokaH4pQ7lVz7pJ",
			"VK5seD8gVv5inqGx1MCu",
			"iUBLb6X9b3NVZ7h2vlbb",
			"FMuSeB3nLgHh5liuIOQx",
			"UHxpEHkL2h5f2CfEhOg8",
			"KXFfD2CWdtu0NhuIojA2",
			"Gs6bYKdKx02XiMhPpo4k",
			"kzRAoTu27hGaxlsh1cO1",
			"KpBauqulhoF3V92SNWUc",
			"QqitWvCWki1YleMfpXYx",
			"6W8kX4SoQ7n9rtBArcKu",
			"ligaEqQsns3rUF5F0OFa",
			"kAg30kDENZP20tSEAaHe",
			"3nF9BGfBGBkOPYfXJXR8",
			"5h0lasZM7d5SBhB3Kg8W",
			"livVA4A35SNL5bkI0Llx",
			"Bc6vbIzvmrfAd3kSzBZI",
			"g7TvUGEt5g2syKyyJaXr",
			"Lm2zJdECVNsT9c8dAciD",
			"roVpxuDWl23Y7iSZaMl0",
			"pB7jtaHIcjWdeFjMBucS",
			"USx9A0YiLVKuqxZQm8IX",
			"YR3zK3atPZJsHFlfJh6I",
			"rHBYspRMa7iX20J8CDXu",
			"DKdnPyt1i0aOIt5HsdtZ",
			"LoO50pMrLqLEPXtWOZc1",
			"7JWxiDnnUBulFjhyrhW7",
			"9DkMkGUeXabrmj9ZsqUg",
			"pzCHUztO81Xb825KlOxn",
			"pyVWpFVM2IQFr20YAZ6r",
			"ECOaWIPEiZUmAApgJ0uN",
			"i0uJo21WMleH042lLcoa",
			"WfDsQgzFk01BMqv9zkVr",
			"iyPDAYyzVjKPLDxRGjMj",
			"ytoGYv9OPNLxnIUZ1A8d",
			"nPL9Wp530sjKA1PkWps4",
			"4F3LEzqOl9HXIrzk8RvS",
			"iECl92qLX4sMffS5Pmvj",
			"aJHUEofSR8FOEMv3cJdJ",
			"YMK94WXQWMEEx7JhRCEI",
			"t6RnONITHbyYGr29eXO6",
			"Z1nUHyXFymgsns5sC57r",
			"0Uue0zPR0Jbkm5Esm5Iy",
			"incMn6RcsT0Gd7lWE0mV",
			"jzziSyafj7MUAKQc6Rui",
			"LAMMRH0bcboZtnZ9KUSE",
			"CRe9uW3E5zyUbofTHgBf",
			"ucyCJ30bpIge7i55yZRW",
			"DOTen6jRzFrCQQRibXtJ",
			"7Frrt7UXm5uBeVProkGM",
			"fH3JclPjAhaDrmlCJLCY",
			"XypqiRAiQAcbNIpvS3Da",
			"3t6aGW41xTX7237XsUMt",
			"GGmxfnfKtBW0zXxy7RNc",
			"6uXch4BQkE65VtqgCdaL",
			"Y2h3I6gjWRXIuIGa1WO3",
			"Aie4rTuUbV7CCQD2GIoZ",
			"N5ZKMoHcg2dQMI2o0Emk",
			"3kdMQLrmjomiWWe0LQaI",
			"BPLenSNFfr9PhOxhf5lX",
			"AWgtZG8pPxPuxh5VcID8",
			"lZgk6Of2U7p7MfFsqFGu",
			"6mLJHxGZyhKII0uEUxYQ",
			"p1H9u7g9cHQPlxtKC6xW",
			"SVMTzSTut91GKWhhGPAL",
			"iuvMtoch7psD6AZmjRFX",
			"qrYKVCgJBaRVQe6i75Va",
			"MpsS5vVQc6nrlfFKQvX4",
			"QUv7CJX2Zp4UuIbirHoo",
			"nnOHtbs2EZhaVTFd9EuT",
			"ah93fHTGvQHjlkMQXj5C",
			"2aIQEnOrIVTamkMEXLAi",
			"Me2K1hM0Pe2ivbpqhU0q",
			"z4Oa2M5HFznAa5oxai0F",
			"33niyQfhHTNUWvDCm9JI",
			"9fYhuf5CH3EfZ5YlvUla",
			"IvsQTA853WJFcqpju3Gh",
			"PEsP9aESetg40DknpsQy",
			"QMeyWsqghK7p7XRVxMq9",
			"tq5qUyuTauOZIbcmAbFu",
			"kTfC728g36sVSMh3J34e",
			"tI0AUJ8NB6duOSGS0qO4",
			"tQri3IyAYauz41is2903",
			"2oYMhgQoN1Js1Ff5b2S7",
			"41p1l7xdJJ9pdxunzXms",
			"g4CxYl3hssGfc0Nr6gND",
			"rGuIJvoz9BzgbpCivFlp",
			"J3aRqlSFaA8ovHiZDTTN",
			"8Mx4S26AD8tQ00joDXYe",
			"c7xSONkZh4fS9uMAKUVl",
			"4F2h4lzX3x2JzF4ntlBn",
			"p9n1TdhjGyCLTW5cLeng",
			"FXL9hLHjtiDeVfrsvLKy",
			"ydLqG4LYG6oeXMiuzWk0",
			"Yz6nyrzbk2X73ZPYQ2cR",
			"IDFi4ubxqSoD4GcADV7t",
			"hHAZWxja6GGuBEEGJyEL",
			"rubVnixCk2ySVHbSEApc",
			"dbZBjKdl4O9vcXELSbKg",
			"fqK3qQ9hxZaWR4iqQ5ku",
			"7uuEP3rEig0qDAJ9LZ04",
			"NznD7DdvoxgvTjVH6BOC",
			"tGRb9cO5QCHpBrf7axqx",
			"dPbHJIeL6EOYWulKF6ut",
			"GRcSFvjnMdifhPMfsX39",
			"Wfjabs0CI1mKVZQ8ogfB",
			"oc3x5r7XSvdaGR1sDi9h",
			"fa0OZlCUk16tRgOmpWPD",
			"4ps2cDEhHnkN5inKG2Qe",
			"r2l3TgPWRoExzzVRuCLI",
			"yL1Md6IO2gAzHPgOX9px",
			"qWNgNhmP6Je3RC0xS4e5",
			"Nfv4rM1LZimgaydM8014",
			"14XKTzoIXrdQ4c7pZWAE",
			"hKPk6U8V5KkLSUK1BnWn",
			"32jg30yvINRjTzCcXeKg",
			"6WvOKqjHJ5y8qslNil8A",
			"Q5lPbdRFJOtberU6ZSUk",
			"TgnISriz8WRhyRgqCLJj",
			"x61lYzzmOrlAseRoX1li",
			"WCm1p9FcXiL3bUGMvoH8",
			"2fWFDuX5dhTeTTtV3Onx",
			"jAgJjiEuSLDlWsx826q9",
			"2ifsBL8yNY7jToYqmubC",
			"EjOHPx5SM5F29EPdDFLQ",
			"U75LGxqQ7UFyH8pXuf01",
			"vN6PP48RSkfMsSaBudCU",
			"nvapdF13i5yBkZc9l8kq",
			"1H3bk1fSAhueimjDqkP8",
			"HGGMSduqzpkQXpJ9KidU",
			"USLNJGZYgGcyFBzCTT5d",
			"XVAsZvcgEXeL4J3KkgXr",
			"hN30mFnIDDCKTtlDmHTm",
			"6ZGOTPSIc5Fretno4GgF",
			"kvdDoibBoGnvDg9EFFVi",
			"2NPILNf8M3G7fb3oP2aW",
			"GYEuoaQULAvmk1JVyrUf",
			"udTQYIHmPYRDYPcedkve",
			"Xi7axYaUzfEkN6oQYjif",
			"7MPk97vrreZgn80uotLM",
			"JnVq7A50nDn8XRgijG4F",
			"NLCfq9EsU2cUsoRvnVsb",
			"F7lHZilsHAvLWURp4HBu",
			"zKm49G6Po63IKhbrh3H0",
			"bLUFcCI6EQaF7XiTkZnR",
			"7FAlozBK4ld7yk7BDrGB",
			"sHHpBEr6Jb2m35biF0jc",
			"6ZLHYA0qKA4cnddKbnB9",
			"WFtgpgRW13LBV9iDJnsH",
			"dykvkq00Jp8J3kC3P1JE",
			"lERtEViyN4mJgma5nu4e",
			"Ofn9ZOOJEDzccElt22U6",
			"DuzCcRW3GvLzE4duUGhp",
			"7eTNP4n2rf6opVNz8WjJ",
			"HF5u1QHxfInOSU622fSk",
			"aut0kSBAttmmzfZQu5yJ",
			"niuE1kXgr5rXKF7U4Ocd",
			"jcmgipPkIBg1qZEIenKy",
			"KDYjm1FsI9Jq8fVqdubA",
			"Z0nNt88bnU6cQcAFg5Ug",
			"XWHFpN4MiPOse3tDHZBk",
			"cFF1PED9V2FNoISjHUWb",
			"vuQnm5fCO9Y23QZuIuEY",
			"KbWPu8MGtKanFkKi99Ok",
			"3vR3za8Xag3pvpMxhi6m",
			"8ooRjRsKsTKdsNnDuDuy",
			"PR3gJSFgl0OsuAJKDdht",
			"BlMvBl2Qy50zvx4gMadx",
			"xUOmr6djyFHVLynkuuQP",
			"JvH8fxWUWR6ysEFuTzU9",
			"5FYRIviGm7eJIgkFXJnj",
			"b5t2LvJjTPexqOsI51Y1",
			"cymmaMA0n7QxFfoUkM2V",
			"TJ4xBOAJk3ApxpMQj3Lj",
			"o5bgGhv4vZyzdf0hWLTE",
			"jzUMMD5bncBun6UaeCZL",
			"AnMVe9Nx4rWNIfy9pL4m",
			"QUJQNyj2IpfjjoILpW6I",
			"C62WZyE2z89IiBjSvOf1",
			"2KS7AaDLKGtB2K2U0Xme",
			"dIVG7TtP0V9qsMoY5CBj",
			"RdUsJqY44ZqIAxTqPvBj",
			"vEEGkGgN9CDiEoUMp4JS",
			"bQIkktsGKeg6bClEYUux",
			"VmgeLjqefvtYYHy2CF9k",
			"QCma7cULBfFbL2bkm0dc",
			"M1iqm0p4qfkN5KnXbStO",
			"RPb6MgWQNQRcvogAI7hE",
			"xZULHQ83TLLcp2KD4zfz",
			"DQxHY1TXTiIbB4noT2kH",
			"veAcyTKi0bjailrSWOe7",
			"BHCX5X5pPHONnJi5O9k5",
			"OgUEE8DpAcnI4PjjBPhK",
			"7OnWLRU01K3DlPKUvgrf",
			"KZEqJGKVB7j2rtALRzLI",
			"nQFl32GljZc8NYb5aovZ",
			"KT3r7sxyR0mBxIdXgzrR",
			"kkjCtMS0A3JSHJfjMI93",
			"3PEpWEavSt0FFqH9dfll",
			"tkkxVCW4jlsU6F78GmO1",
			"l6ezp3pKrKk26HlpCkHi",
			"uLG34l7uEAvTyAQHFDQs",
			"5c2Bde7OBe5RdGbxHyVl",
			"p9jatszQSdpYqG6AkEuJ",
			"VVPKNHxthYV3T0Rn7oP1",
			"Hb24VkpJH76BNcmKGHHE",
			"kRSYadS9j3eAh2DUZG2D",
			"YiiciFObtjhnE7SUUUrO",
			"mWqI9Fe6TLjqdeH269P2",
			"LkvB1DyO12tTsuSsY0sQ",
			"8dtxGqPOUZdEUc1Vge62",
			"hdEZoyOxZuvFo6zoCF0d",
			"UxQKoBSpmqJqGC1KJTm9",
			"AjnXOgMC3Eqya9YxlZT1",
			"EgclGykkGhX99z2nFuuk",
			"7d0vQSNDkKiqUnqmWufe",
			"jKAI3v9P7ZfSuposS6nm",
			"IcQ2SO7NH18JyteCQBVP",
			"fqA2QxcWKIlI9dVyhS3Z",
			"QEZBaUyoVosBzDTQ3s0G",
			"9HvZLqUDG0BXFXpaU0bA",
			"Vrvx0lBJtlj2f5fpIrKq",
			"zpYmpAnKQnKDM2U44ffG",
			"GhTmCUbd0DGSBfg74vWB",
			"y3dpkChLglUMuaFZFChO",
			"ZIJefH13ZW0Ko83rlSml",
			"gqzyoKmY0yCxNgiKG5Le",
			"jv5qHNqBENXOGsleQTi5",
			"y6Whgl94pa9UMX6ddkcp",
			"xdV7LskTuFlzl5kmaWil",
			"mabGDO7Wlf6XEoDrBSnL",
			"7x1hzBhVqf9WA12s6jo5",
			"7qfyidW2rKMmucVZV7Z8",
			"s1tz8uYQgNoT5FgiK6BX",
			"HOFt5bnk205aQBixHFom",
			"Px628BbAGRqhmHBynq68",
			"R8ZW8uBnUNzC1nMgpCcN",
			"Gq4ZpuBFAe3E2ESpZM8k",
			"csUBrWIUvWhhEjHKWyeC",
			"DSSsh6tT9Gpl7XceHjhN",
			"5jpiG5jg8iX0F1QH8VqQ",
			"eGDTLB9o7nUmRdxLixrh",
			"0s0yfRO48Ng5zkJyI4at",
			"cuMx7kWCSD5cIjx2VpEi",
			"VZQhWyfxP5xkHkBPf3rf",
			"Ya70D5HtqLB4h6tDor6h",
			"F9LB5fa87UDMIuAEdtXa",
			"E4anuGrFD1sApmDdHlEN",
			"oKuuocVYnLJc1p275HyT",
			"Wiu0NS1vvHoRudas8V1I",
			"ztjAn2acABTAWhH6JqdC",
			"fKBAkg5S1QeFOVyNEX7t",
			"MnAmHqodjWMCZ1DuM3CN",
			"9XM6lcqkYZorPSMWYWqA",
			"fkQoIezmV0bYzQ7rMKQF",
			"0SaEji57GiCtfU14CMf2",
			"X1quztjla3ZpU4f4VrUj",
			"Fo7GseymryR0GUvb7uTu",
			"WOrrply9KYV2YvPt2GCG",
			"fWLJ9zfbtLuFT3QXepI8",
			"HknxXGPy15VtZvE63AfD",
			"fTfTyqkvd1H0tn05bTsU",
			"D8XUl22i20GCEOCszca9",
			"NkarmIhoIEGI6sdVzhg9",
			"oAosiSnyj6O7n5VdAGbb",
			"vZaiz2XCnPXYj02e6oqd",
			"1nfll6njUijNe4kcph6d",
			"o6RD9TmIQl4W1IEMYp8g",
			"zRcfCU1zj3rZFYjq7g4P",
			"76iMUvPDLpi3AeO9aOP3",
			"CIznCcu8UbaHALeRsLYR",
			"EbmL3NFqjg9bUjSsDkqj",
			"1rIgRiqNEuiA9ijREDsq",
			"V4ipdNaCgHdVQAxnqXum",
			"fIhbT67nXqos34ua14AM",
			"rXIeCSSpJYQgPnsuS5IU",
			"U3XkJSNrel9B5FHF0LKf",
			"H4jvUc6UZ8qX3NLffS8i",
			"OBjXpZA7t4aAIdXFOAoh",
			"i9OhaYue1NKzBzLlCVvX",
			"0VB8SNn8afpVyYCRpq2B",
			"dgakMclL96SWYCkg6rgR",
			"IE8YdXyDeAJl7L4V64fr",
			"RAvbWqp91fkQu6nuErAV",
			"CVTF7ynR6tc1U0IFhskp",
			"2p8F0szWebWJZLCLW2QV",
			"ix1FeRpUpV346E8IjImp",
			"FA37hqayNLK8Eky6kq2j",
			"iyGpZFXxZzDrD522FBfV",
			"4hZjU9l1yUhMiF5IGA0f",
			"Ep1qnmh8si72MSVZzI7f",
			"BtdpG9svmchX89bf7oj5",
			"GHQo7erxCXlthfNjD6kk",
			"MdB0xKaC2R61GzIkPzmW",
			"iEIcJ7fIzlVLyB03q34A",
			"s3DI995iQ9MnlJ5sOEuA",
			"MWo0LRl7xNAcmAYtSbd5",
			"ZFuA3Gfe3H9GgX9p5mB3",
			"sXeCYORFKNOITKvhJZQp",
			"CjbPTdlS9prUCJc70VO4",
			"uS4eIBXCr3HfN12EYieu",
			"lyjO9BLAv1TFXBJM0osa",
			"kz077Qu7Jr18fZMOxrUi",
			"I38JHIn29czXeNlXXKiA",
			"WtKGtq146szKohQtsgf3",
			"MYTB5s7jhxrMhVCMG1Mg",
			"0igajyjFxjN5g0o1erog",
			"Kr7ejfI0Jch8zEOWAYAd",
			"guIZQ0xFyBZgZDrnPFNj",
			"PkdWmREdQ9cpMXTz6cYJ",
			"2fDmkYGkeXksC9CGfFYu",
			"V18NQHD157mSzp7i6y2L",
			"7gamIDgh2aEoOLc6OyqJ",
			"LFZhEQAGi0x9dTx1C4Zn",
			"Mgtgg7yp5LGVjD9Jqhgu",
			"BVs4MXRTA24OjhRER7fp",
			"voWg9g3vXszNIqrCbFrh",
			"UmOxK05svHJYvEjE8tPB",
			"EUYdd6quFsYr4OJ6CCK2",
			"VtyVlegnFvLsErunsFMx",
			"1e7hvP35TUXOBu6LyqUm",
			"qc5eUNdBlAuZBBasV9W3",
			"ml8WKyQeC9YocqMQLu5f",
			"ccuDRNOxsoy5ZysXkj00",
			"0VRMBjbBxl7mKuHK0fWJ",
			"IP6I2EJMJUUBoQphzCii",
			"aYraAJsgNsQdJh6FtUNf",
			"kFMOztjiYkhKiFj6kpBf",
			"jgvIKM1y6L0V9DbqAWmc",
			"FhJ3YgP0eU2FvvjM2Nmo",
			"IN6UxLr2CsyUi7RQ7AP6",
			"RosvIz4PQNrzQNTtFWq2",
			"iOZ985lqNpn8FZKADGRl",
			"s32OTxepjrCRLctGjWHo",
			"WcFdsj4x7KX0Dc1nzbs8",
			"PrtWpHJhXc6t7ovuLZXP",
			"0bBuXC5YgSBtcaePD3HW",
			"8sakqcrHXUz8ZqSj3vF6",
			"f1A4Qr7bNsCknx5oYpAy",
			"rkZuIkGjM2iQmZ1aXrSt",
			"BTqAs5UyskPjoLdiToJH",
			"KZP9n5h4pFFHrOWfgiOD",
			"VIsRJLTgxVmeHtR9mP4A",
			"cKgxXRMhNbhx2UQGOGuP",
			"OtOMkBvLMIe4t9ZqS5IA",
			"VEuzNQ9GCp9FnnBG7IzV",
			"D1hhtItxx8cVHFMuY7RT",
			"lox0aV0toPNPuJqXNonY",
			"gOSGDngs6j9SlWL3h1Bb",
			"O0CTThbgnrvNiqWXuvC4",
			"PUAtVCLzMJo3Ne76fDYV",
			"h9p5rZzVevkT4yr3cCWh",
			"jrIKEi98n7TTdkqZAVKK",
			"HSL97Q2gr5pzaIsCFlHC",
			"JbD1ND8MdNB0W8WEZmln",
			"Fie36xtydHbWSIdExRlB",
			"B7slkJ05SKzNXWNBViFa",
			"hLL5bVtkqnOpCNEFsnoi",
			"UWMizx7nksLOmzg0BV5S",
			"jyTK1ojA4j95hZP1AbHO",
			"EOTqgnaQlX3y4xqCURgI",
			"lnCIROv9eEiXnSgVr6hC",
			"HUf0KAmOliVW0cmuD8dy",
			"GyxdqRVjVkiEgxqIWWvj",
			"OoNOpqMxVLMsFKmUcLOy",
			"HvHMznRQfc2fVaUHIzMC",
			"JL03rjWa9NCDBxPt5r08",
			"0WTWE5HJTpR3kVS2oefg",
			"aTQoUiCm5KvlKXSekKvt",
			"hkezzhpayDeVCGBiR3eU",
			"uB9t4VCDtrHx4WBdWt9V",
			"2yMT7U3QC8drNLG3mnFu",
			"jYmFW8g7oeJhY9x9SsLI",
			"WMVGPPji43Yv4thESptk",
			"zlimMPcVQjPlggtipI4i",
			"K4trgps7I6EtulIS8Bvu",
			"Tj9Bnek246LoGnf5IZs3",
			"8J9KVxOjcXYQ908SLKl7",
			"D3OUlf1QORMXaHci3JDi",
			"5X01nnINMqxUqiDtEzHT",
			"ITFvx46v8lYX899FjEgv",
			"Sh28avNq56GFKnHvteY1",
			"66BrsH3pbjJsF62uIUTf",
			"01vxYSofOys5LVLju0vX",
			"rz1W79BMEsk94pIsIBsu",
			"dqAKvCvUIn2zTurxzbrQ",
			"mQgEUX7e9fYbUnT6JAIs",
			"TTCUT88LO3RS2CGHZk9r",
			"z4S0rJTMZEKS02sNiFzQ",
			"L4q6H549uer10gWNxObY",
			"aOLBL2868C0iuJQFKH5W",
			"XjGxFXqXJeeeKPioMFTY",
			"XTz00R0UT5fRiP0pI2le",
			"ofSz9n7S7h9sykpp3JFH",
			"eUMpIjBfzfBpW3oAjhv7",
			"bXzm63BciF0uF658PECS",
			"eBYvlokasxyubA4FGHt8",
			"LmeyuVqF8hJBSRR9Q4ET",
			"jkIZNOX1K7m8T69barDd",
			"SPnPpHPGdgFLdgrI9b57",
			"D3tq8ibWdmHxYyS6bXRz",
			"DozOsksoEgdOcvksb0A2",
			"51kOcaZx2gWh0hJvbZGo",
			"dSkUnGgzvJlrUgyWZq8V",
			"NUKu2y9nagHx1JszNn8n",
			"DeafpuofaSEOvSbqs7FA",
			"4H3ifWM2dBE2VXOqginY",
			"edB6nQ1iUv9XONNYHGF3",
			"xkzGEitvY3b1BazhsIAb",
			"uHWxvWJO1czgrmKhzfkJ",
			"Xt2M8UtyM7WBt1Ft1mXY",
			"oOsbXrVrbDsRcUrkbbqI",
			"9ERQbbckx2hvF6knHjkT",
			"mnsBIPckqi6MjJfNpK9c",
			"fAxeQf9k1gkZiZ0EmsQ2",
			"TizuKHkFNjENf6fRWZik",
			"yOaki6R6n5c79JOsivVB",
			"2lFpTS7EP7CWtcNQrEtd",
			"cmDsOy6bbeqLYPHNbMWC",
			"lIfZUzNPTbfaXk9cOehS",
			"OTuMq5g9y4BIeeljm5Rc",
			"Zgxl8CWNr86y3Py5ruAX",
			"ZngTokgG7eAu20kKGQkG",
			"AN4tnZqdatkOQzKyYkpF",
			"uFriZFap0zNBKKv064hf",
			"jcDlkrpszaeQQUfWVsM5",
			"czYyvyeQhKGUdfys1aII",
			"N8aFtUx4jqX9BjCaM3ek",
			"n6aFjGzj2WFh7tMXCJSs",
			"lbNWyHR2F2gzToTJqxBK",
			"mHhScPnlRtjAzsQ9FU0q",
			"vD0lvBrM31yTO2jTS2BI",
			"HYamzSAqzGKjuxoNLvtj",
			"bfHYBgQC8x9VxzT8Cbov",
			"mSh3RDa3uquC98bzfFRX",
			"QelTxW2fSAgG6RyfSUaW",
			"QWMs3NgkG5LlTrFbtrEi",
			"du7AtSkKh1Kgd662ELLC",
			"fchcTNSCePONL8b48Ccq",
			"SEAAH6MEsGLvKp01X57y",
			"UZrvkBTEDOihoL7J0RNi",
			"CGKTYspaDSESvl5WpNvi",
			"SaO6XdtyMQWrrpAHG68O",
			"zQRTDjyrg8vTciybS76B",
			"1jkTBGhIBPk39UrDmxGr",
			"GCDbvMYpSO7b1DzbKTZ1",
			"MAOe4IorKq3GWZfJhP3V",
			"ChBek4Yb2Ar9Hr4PfPtA",
			"2COSgFTEftLXyKm1aBri",
			"RDZ9hg2LZxpJCBGZu2BB",
			"c8crXsh9FfYUpfXVQLa2",
			"d1jT7DLyN6DrnIZn7s9G",
			"5zVqPin7uESgSo3ZpoPN",
			"dm6sPTYPb4VI3ea3gYKA",
			"oORKlFedvSKSI4vQxl6c",
			"RSPKBdM81gFjfNMIEX3B",
			"erIRckSCKme2X8MO51ye",
			"PAP8K3PMkVgHcfg1ChIA",
			"CnHhOj6Up5FlebCiNHrD",
			"OBZKEub2XQM6Q9jlvZAm",
			"VdVpqSOF18TnLlHCut5q",
			"2mjzN7ypQQb8YZf4VqcO",
			"XkPWhB25g77lSc5tizC7",
			"beLuXKHXUMPqlhhH6E1f",
			"KkL6rZmP385WDpQ17lui");

	private Client jerseyClient;

	@Test
	public void testSynchronized() throws Exception{

		EnviamentAdviser a = crearAdviser("pendiente_sede");
		EnviamentAdviser b = crearAdviser("notificada");

		ObjectMapper mapper  = getMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String body = mapper.writeValueAsString(a);

		String jsonA = jerseyClient.resource(ENDPOINT_ADDRESS).type("application/json").post(String.class, body);
		System.out.println("Missatge REST rebut: " + jsonA);
		AdviserResponseDto respostaA = mapper.readValue(jsonA, AdviserResponseDto.class);

		String jsonB = jerseyClient.resource(ENDPOINT_ADDRESS).type("application/json").post(String.class, body);
		System.out.println("Missatge REST rebut: " + jsonB);
		AdviserResponseDto respostaB = mapper.readValue(jsonB, AdviserResponseDto.class);

		assertNotNull(respostaA.getCodigoRespuesta());
		assertNotNull(respostaA.getDescripcionRespuesta());

		assertNotNull(respostaB.getCodigoRespuesta());
		assertNotNull(respostaB.getDescripcionRespuesta());
	}

	private EnviamentAdviser crearAdviser(String estat) throws Exception {

		jerseyClient = generarClient(USER, PASS);

		// Data
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		// Receptor enviament
		Receptor receptor = new Receptor();
		receptor.setNifReceptor("12345678Z");
		receptor.setNombreReceptor("destinatariNom0 destLlinatge1_0 destLlinatge2_0");

		// Acuse PDF
		Acuse acusePDF = new Acuse();
		acusePDF.setContenido(CERIFICACIO_B64.getBytes());
		acusePDF.setCsvResguardo("dasd-dsadad-asdasd-asda-sda-das");
		acusePDF.setHash(CERIFICACIO_SHA1);

		return EnviamentAdviser.builder().organismoEmisor(EMISOR_DIR3).hIdentificador("39128285cf121cb00453")
				.tipoEntrega(unmarshal("2")).modoNotificacion(unmarshal("5")).estado(estat)
				.fechaEstado(date).receptor(receptor).acusePDF(acusePDF).build();
	}
	
//	@Test
	public void a_datadoOrganismoTest() throws Exception {

		jerseyClient = generarClient(USER, PASS);

		// Data
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		// Receptor enviament
		Receptor receptor = new Receptor();
		receptor.setNifReceptor("12345678Z");
		receptor.setNombreReceptor("destinatariNom0 destLlinatge1_0 destLlinatge2_0");

		// Acuse PDF
		Acuse acusePDF = new Acuse();
		acusePDF.setContenido(CERIFICACIO_B64.getBytes());
		acusePDF.setCsvResguardo("dasd-dsadad-asdasd-asda-sda-das");
		acusePDF.setHash(CERIFICACIO_SHA1);

		EnviamentAdviser adviser = EnviamentAdviser.builder()
				.organismoEmisor(EMISOR_DIR3)
				.hIdentificador("39128285cf121cb00453")
				.tipoEntrega(unmarshal("2"))
				.modoNotificacion(unmarshal("5"))
				.estado("notificada")
				.fechaEstado(date)
				.receptor(receptor)
				.acusePDF(acusePDF)
				.build();

		ObjectMapper mapper  = getMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String body = mapper.writeValueAsString(adviser);

		String json = jerseyClient
				.resource(ENDPOINT_ADDRESS)
				.type("application/json")
				.post(String.class, body);
		System.out.println("Missatge REST rebut: " + json);
		AdviserResponseDto resposta = mapper.readValue(json, AdviserResponseDto.class);

		assertNotNull(resposta.getCodigoRespuesta());
		assertNotNull(resposta.getDescripcionRespuesta());
	}

//	@Test
	public void b_datadosOrganismoTest() throws Exception {

		jerseyClient = generarClient(USER, PASS);

		// Data
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		// Receptor enviament
		Receptor receptor = new Receptor();
		receptor.setNifReceptor("12345678Z");
		receptor.setNombreReceptor("destinatariNom0 destLlinatge1_0 destLlinatge2_0");

		// Acuse PDF
		Acuse acusePDF = new Acuse();
		acusePDF.setContenido(CERIFICACIO_B64.getBytes());
		acusePDF.setCsvResguardo("dasd-dsadad-asdasd-asda-sda-das");
		acusePDF.setHash(CERIFICACIO_SHA1);

		ObjectMapper mapper = getMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		for (String enviamentId: enviamentsIds) {

			EnviamentAdviser adviser = EnviamentAdviser.builder()
					.organismoEmisor(EMISOR_DIR3)
					.hIdentificador(enviamentId)
					.tipoEntrega(unmarshal("2"))
					.modoNotificacion(unmarshal("5"))
					.estado("notificada")
					.fechaEstado(date)
					.receptor(receptor)
					.acusePDF(acusePDF)
					.build();

			String body = mapper.writeValueAsString(adviser);

			String json = jerseyClient
					.resource(ENDPOINT_ADDRESS)
					.type("application/json")
					.post(String.class, body);
			System.out.println("Missatge REST rebut: " + json);
			AdviserResponseDto resposta = mapper.readValue(json, AdviserResponseDto.class);

			assertNotNull(resposta.getCodigoRespuesta());
			assertNotNull(resposta.getDescripcionRespuesta());
		}
	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

//	@Test
//	public void b_certificacionOrganismoTest() throws Exception {
//		AdviserWS ws = this.getWS();
//		
//		CertificadoRequest certificado = new CertificadoRequest();
//		
//		certificado.setAcuseOSobre("acuse");
//		certificado.setCertificacion(CERIFICACIO_B64);
//		certificado.setHashSha1(CERIFICACIO_SHA1);
//		certificado.setIdentificadorDestinatario("39128285cf121cb00453");
//		certificado.setOrganismoEmisor(EMISOR_DIR3);
//		
//		ws.certificacionOrganismo(
//				certificado, 
//				new Holder<String>(), 
//				new Holder<String>());
//	}
	
	public BigInteger unmarshal(String s) throws Exception {
		try {
			return new BigInteger(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}



	// Client REST
	private Client generarClient(String username, String password) throws Exception {

		if (jerseyClient != null) {
			return jerseyClient;
		}

		jerseyClient = Client.create();
		jerseyClient.setConnectTimeout(10000);
		jerseyClient.setReadTimeout(30000);
		jerseyClient.addFilter(new LoggingFilter(System.out));
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
						}
						return response;
					}
				}
		);
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
						ClientResponse resp = ch.handle(request);

						if (resp.getStatus()/100 != 3) {
							return resp;
						} else {
							String redirectTarget = resp.getHeaders().getFirst("Location");
							request.setURI(UriBuilder.fromUri(redirectTarget).build());
							return ch.handle(request);
						}
					}
				}
		);
		// Autenticar
		jerseyClient.resource(ENDPOINT_ADDRESS).get(String.class);
		Form form = new Form();
		form.putSingle("j_username", username);
		form.putSingle("j_password", password);
		jerseyClient.
				resource(BASE_ADDRESS + "/j_security_check").
				type("application/x-www-form-urlencoded").
				post(form);
		jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
		return jerseyClient;
	}

}
