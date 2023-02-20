package es.caib.notib.back.helper;

import com.google.common.base.Strings;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidHelper {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
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

        String line;
        int res = 0;
        String pfx;
        while ((line = in.readLine()) != null) {
            pfx = line.substring( 0, 3 );
            try {
                res = Integer.parseInt( pfx );
            }
            catch (Exception ex) {
                res = -1;
            }
            if ( line.charAt( 3 ) != '-' ) {
                break;
            }
        }
        return res;
    }

    private static void say(BufferedWriter wr, String text ) throws IOException {

        wr.write( text + "\r\n" );
        wr.flush();
    }

    private static ArrayList getMX( String hostName ) throws NamingException
    {
        // Perform a DNS lookup for MX records in the domain
        var env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        var ictx = new InitialDirContext( env );
        var attrs = ictx.getAttributes(hostName, new String[] { "MX" });
        var attr = attrs.get( "MX" );
        // if we don't have an MX record, try the machine itself
        if (( attr == null ) || ( attr.size() == 0 )) {
            attrs = ictx.getAttributes( hostName, new String[] { "A" });
            attr = attrs.get( "A" );
            if( attr == null ) {
                throw new NamingException("No match for name '" + hostName + "'");
            }
        }
        // Huzzah! we have machines to try. Return them as an array list
        // NOTE: We SHOULD take the preference into account to be absolutely
        //   correct. This is left as an exercise for anyone who cares.

        var res = new ArrayList();
        var en = attr.getAll();
        String mailhost, x;
        String [] f;
        while ( en.hasMore() ) {
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
        var pos = address.indexOf( '@' );
        // If the address does not contain an '@', it's not valid
        if ( pos == -1 ) {
            return false;
        }
        // Isolate the domain/machine name and get a list of mail exchangers
        var domain = address.substring( ++pos );
        ArrayList mxList;
        try {
            mxList = getMX( domain );
        } catch (NamingException ex) {
            return false;
        }

        // Just because we can send mail to the domain, doesn't mean that the
        // address is valid, but if we can't, it's a sure sign that it isn't
        if ( mxList.size() == 0 ) {
            return false;
        }

        // Now, do the SMTP validation, try each mail exchanger until we get
        // a positive acceptance. It *MAY* be possible for one MX to allow
        // a message [store and forwarder for example] and another [like
        // the actual mail server] to reject it. This is why we REALLY ought
        // to take the preference into account.
        boolean valid;
        int res;
        Socket skt;
        BufferedReader rdr;
        BufferedWriter wtr;
        for (var mx = 0 ; mx < mxList.size() ; mx++ ) {
            valid = false;
            try {
                //
                skt = new Socket( (String) mxList.get( mx ), 25 );
                rdr = new BufferedReader(new InputStreamReader( skt.getInputStream()));
                wtr = new BufferedWriter(new OutputStreamWriter( skt.getOutputStream()));
                res = hear( rdr );
                if ( res != 220 ) {
                    throw new Exception( "Invalid header" );
                }
                say( wtr, "EHLO rgagnon.com" );
                res = hear( rdr );
                if ( res != 250 ) {
                    throw new Exception( "Not ESMTP" );
                }
                // validate the sender address
                say( wtr, "MAIL FROM: <tim@orbaker.com>" );
                res = hear( rdr );
                if ( res != 250 ) {
                    throw new Exception( "Sender rejected" );
                }
                say( wtr, "RCPT TO: <" + address + ">" );
                res = hear( rdr );
                // be polite
                say( wtr, "RSET" ); hear( rdr );
                say( wtr, "QUIT" ); hear( rdr );
//                System.out.println("Response for address " + address + ": " + res);
                if ( res != 250 ) {
                    throw new Exception("Address is not valid!");
                }
                valid = true;
                rdr.close();
                wtr.close();
                skt.close();
            } catch (Exception ex) {
                // Do nothing but try next host
                ex.printStackTrace();
            } finally {
                if (valid){
                    return true;
                }
            }
        }
        return false;
    }

    public static void main( String args[] ) {

        String testData[] = {"sandreu@limit.es", "siona@limit.es", "sion.limit@gmail.com", "jvazquez@dgtic.caib.es", "monsalut@hotmail.es"};
        for ( var ctr = 0 ; ctr < testData.length ; ctr++ ) {
            System.out.println( testData[ ctr ] + " is valid? " + isAddressValid( testData[ ctr ] ) );
        }
    }

}
