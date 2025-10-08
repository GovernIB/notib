package es.caib.notib.back.helper;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EmailValidHelper {

    private EmailValidHelper() {
        throw new IllegalStateException("EmailValidHelper no es pot instanciar");
    }

//    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
//    public static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern EMAIL_REGEX = Pattern.compile(
            "^(?![\\.-])[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +  // local part
                    "@" +  // @ symbol
                    "(?![\\.-])[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)*" +  // domain part
                    "(?:\\.[A-Za-z]{2,})$" +  // top-level domain
                    "(?![\\.-])", Pattern.CASE_INSENSITIVE);

    public static boolean isEmailValid(String email) {

        if (Strings.isNullOrEmpty(email)) {
            return false;
        }
        try {
            Matcher matcher = EMAIL_REGEX.matcher(email);
            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }


    private static int hear( BufferedReader in ) throws IOException {

        String line = null;
        var res = 0;
        String pfx;
        while ( (line = in.readLine()) != null ) {
            pfx = line.substring( 0, 3 );
            try {
                res = Integer.parseInt( pfx );
            }
            catch (Exception ex) {
                res = -1;
            }
            if ( line.charAt( 3 ) != '-' ) break;
        }
        return res;
    }

    private static void say(BufferedWriter wr, String text ) throws IOException {

        wr.write( text + "\r\n" );
        wr.flush();
    }

    private static ArrayList<String> getMX( String hostName ) throws NamingException {

        // Perform a DNS lookup for MX records in the domain
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext( env );
        Attributes attrs = ictx.getAttributes( hostName, new String[] { "MX" });
        Attribute attr = attrs.get( "MX" );

        // if we don't have an MX record, try the machine itself
        if (( attr == null ) || ( attr.size() == 0 )) {
            attrs = ictx.getAttributes( hostName, new String[] { "A" });
            attr = attrs.get( "A" );
            if( attr == null )
                throw new NamingException( "No match for name '" + hostName + "'" );
        }
        // Huzzah! we have machines to try. Return them as an array list
        // NOTE: We SHOULD take the preference into account to be absolutely
        //   correct. This is left as an exercise for anyone who cares.

        ArrayList<String> res = new ArrayList<>();
        var en = attr.getAll();
        String mailhost;
        String x;
        String[]  f;
        while (en.hasMore()) {
            x = (String) en.next();
            f = x.split( " " );
            //  THE fix *************
            mailhost = f.length == 1 ? f[0] : f[1].endsWith( "." ) ? f[1].substring( 0, (f[1].length() - 1)) : f[1];
            //  THE fix *************
            res.add( mailhost );
        }
        return res;
    }

    public static boolean isAddressValid( String address ) {
        // Find the separator for the domain name
        int pos = address.indexOf( '@' );

        // If the address does not contain an '@', it's not valid
        if ( pos == -1 ) return false;

        // Isolate the domain/machine name and get a list of mail exchangers
        String domain = address.substring( ++pos );
        ArrayList mxList = null;
        try {
            mxList = getMX( domain );
        }
        catch (NamingException ex) {
            return false;
        }

        // Just because we can send mail to the domain, doesn't mean that the
        // address is valid, but if we can't, it's a sure sign that it isn't
        if (mxList.isEmpty()) return false;

        // Now, do the SMTP validation, try each mail exchanger until we get
        // a positive acceptance. It *MAY* be possible for one MX to allow
        // a message [store and forwarder for example] and another [like
        // the actual mail server] to reject it. This is why we REALLY ought
        // to take the preference into account.
        for (Object o : mxList) {
            boolean valid = false;
            try (var skt = new Socket((String) o, 25)) {
                //
                BufferedReader rdr = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));
                var res = hear(rdr);
                if (res != 220) throw new Exception("Invalid header");
                say(wtr, "EHLO rgagnon.com");

                res = hear(rdr);
                if (res != 250) throw new Exception("Not ESMTP");

                // validate the sender address
                say(wtr, "MAIL FROM: <tim@orbaker.com>");
                res = hear(rdr);
                if (res != 250) throw new Exception("Sender rejected");

                say(wtr, "RCPT TO: <" + address + ">");
                res = hear(rdr);

                // be polite
                say(wtr, "RSET");
                hear(rdr);
                say(wtr, "QUIT");
                hear(rdr);

                if (res != 250)
                    throw new Exception("Address is not valid!");

                valid = true;
                rdr.close();
                wtr.close();
            } catch (Exception ex) {
                // Do nothing but try next host
                log.error("Error validant el mail", ex);
            }
            if (valid) {
                return true;
            }
        }
        return false;
    }

//    public static void main( String args[] ) {
//        String testData[] = {
//                "sandreu@limit.es",
//                "siona@limit.es",
//                "sion.limit@gmail.com",
//                "jvazquez@dgtic.caib.es",
//                "monsalut@hotmail.es"
//        };
//
//        for ( int ctr = 0 ; ctr < testData.length ; ctr++ ) {
//            System.out.println( testData[ ctr ] + " is valid? " + isAddressValid( testData[ ctr ] ) );
//        }
//        return;
//    }

}
