package com.animus;

import junit.framework.TestCase;

import javax.sip.SipException;

public class DemoSipClientAgentTest extends TestCase {


    public void testinitSipStack(){
        DemoSipClientAgent  demoSipClientAgent  = new DemoSipClientAgent();
        try {
            demoSipClientAgent.initSipStack("LyncStack");
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    public void testTlsInt() {
        DemoSipClientAgent  demoSipClientAgent  = new DemoSipClientAgent();
        assertNotNull(System.getProperty( "javax.net.ssl.keyStore"));
        assertNotNull(System.getProperty( "javax.net.ssl.trustStore"));
        assertNotNull(System.getProperty( "javax.net.ssl.keyStorePassword"));
        assertNotNull(System.getProperty( "javax.net.ssl.keyStoreType"));
    }


}
