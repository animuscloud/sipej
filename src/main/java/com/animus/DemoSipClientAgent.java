package com.animus;

import gov.nist.javax.sip.ClientTransactionExt;
import gov.nist.javax.sip.TlsSecurityPolicy;
import org.apache.log4j.ConsoleAppender;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sip.address.SipURI;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.message.Request;
import javax.sip.header.Header;

import java.util.ArrayList;


public class DemoSipClientAgent implements SipListener, TlsSecurityPolicy {

    private SipProvider tlsProvider;
    private SipStack sipStack;
    private SipFactory sipFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;
    private AddressFactory addressFactory;
    private ListeningPoint tlsListeningPoint;
    private String hotsIp;
    private int tlsListnenPort =  5061;
    private String transport = "tls";



    private String fromName = "tjose";
    private String fromSipAddress = "unifiedconferencingtechnologies.com";
    private String fromDisplayName = "Tojo Jose";

    private String toUser = "jmckinney";
    private String toSipAddress = "unifiedconferencingtechnologies.com";
    private String toDisplayName = "John McKinney";



    private DemoSipClientAgent listener;

    protected ClientTransaction clientTransaction;

    public DemoSipClientAgent(){
        initTls();
        listener = this;
    }


    public void initSipStack(String stackName) throws SipException {
          sipFactory  = SipFactory.getInstance();
          sipFactory.setPathName("gov.nist");
          Properties properties = new Properties();
          properties.setProperty("gov.nist.javax.sip.DEBUG_LOG","DemoSipClientDebuglog.txt");
          properties.setProperty("gov.nist.javax.sip.SERVER_LOG","DemoSipClientServerlog.txt");
          //properties.setProperty("gov.nist.javax.sip.TLS_SECURITY_POLICY", this.getClass().getName());
          properties.setProperty("javax.sip.STACK_NAME",stackName);

        try {
            sipStack = sipFactory.createSipStack(properties);
            System.out.println("createSipStack " + sipStack);
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(0);
        }

        try {
            hotsIp =     java.net.InetAddress.getLocalHost().getHostAddress();
            System.out.println("Host IP " + hotsIp);
            tlsListeningPoint = sipStack.createListeningPoint(hotsIp, tlsListnenPort, "tls");
            System.out.println("tlsListeningPoint " + tlsListeningPoint);
        } catch (TransportNotSupportedException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            tlsProvider = sipStack.createSipProvider(tlsListeningPoint);
        } catch (ObjectInUseException e) {
            e.printStackTrace();
        }

        try {
            tlsProvider.addSipListener(listener);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        try {
            addressFactory = sipFactory.createAddressFactory();
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        }

        SipDemoFactory sipDemoFactory = new SipDemoFactory(tlsProvider,
                addressFactory,
                headerFactory,
                hotsIp,
                tlsListnenPort
                );

        SipURI fromAddress = null;

        try {
            fromAddress = sipDemoFactory.makeSipUri(fromName,fromSipAddress);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SipURI toAddress = null;

        try {
            toAddress = sipDemoFactory.makeSipUri(toUser,toSipAddress);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FromHeader fromHeader = null;

        try {
            fromHeader = sipDemoFactory.makeFromHeader(fromAddress,fromDisplayName);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ToHeader toHeader = null;

        try {
            toHeader = sipDemoFactory.makeToHeader(toAddress,toDisplayName);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SipURI requestURI = null;

        try {
            requestURI = sipDemoFactory.makeRequestUri(fromName,fromSipAddress);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<ViaHeader> viaHeaders = null;

        try {
            viaHeaders = sipDemoFactory.createViaHeader(
                    hotsIp,
                    tlsListnenPort,
                    transport
            );
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        CSeqHeader cSeqHeader = null;

        try {
            cSeqHeader = sipDemoFactory.getCsqHeader(Request.INVITE);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        MaxForwardsHeader maxForwards = null;

        try {
            maxForwards = sipDemoFactory.getMaxForwardHeader(70);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        Request request = null;

        try {
            request =
                    messageFactory.createRequest(
                            requestURI,
                            Request.INVITE,
                            sipDemoFactory.getCallerid(),
                            cSeqHeader,
                            fromHeader,
                            toHeader,
                            viaHeaders,
                            maxForwards);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            request.addHeader(sipDemoFactory.createContactHeader(fromName));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Header extensionHeader =
                null;
        try {
            extensionHeader = headerFactory.createHeader("My-Header", "test value");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        request.addHeader(extensionHeader);

        String sdpData =
                "v=0\r\n"
                        + "o=4855 13760799956958020 13760799956958020"
                        + " IN IP4  129.6.55.78\r\n"
                        + "s=mysession session\r\n"
                        + "p=+46 8 52018010\r\n"
                        + "c=IN IP4  129.6.55.78\r\n"
                        + "t=0 0\r\n"
                        + "m=audio 6022 RTP/AVP 0 4 18\r\n"
                        + "a=rtpmap:0 PCMU/8000\r\n"
                        + "a=rtpmap:4 G723/8000\r\n"
                        + "a=rtpmap:18 G729A/8000\r\n"
                        + "a=ptime:20\r\n";
        byte[]  contents = sdpData.getBytes();

        try {
            request.setContent(contents, sipDemoFactory.makeContentTypeHeader());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            request.setHeader(sipDemoFactory.createRouteHeader(requestURI));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            listener.clientTransaction = tlsProvider.getNewClientTransaction(request);
        } catch (TransactionUnavailableException e) {
            e.printStackTrace();
        }

        listener.clientTransaction.sendRequest();

        System.out.println("sss");

    }


    private void initTls(){
        org.apache.log4j.BasicConfigurator.configure(new ConsoleAppender());
        String path= DemoSipClientAgent.class.getResource("testkeys").getPath();
        System.setProperty( "javax.net.ssl.keyStore",  DemoSipClientAgent.class.getResource("testkeys").getPath() );
        System.setProperty( "javax.net.ssl.trustStore", DemoSipClientAgent.class.getResource("testkeys").getPath() );
        System.setProperty( "javax.net.ssl.keyStorePassword", "passphrase" );
        System.setProperty( "javax.net.ssl.keyStoreType", "jks" );
    }

    public void processRequest(RequestEvent requestEvent) {

    }

    public void processResponse(ResponseEvent responseEvent) {

    }

    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    public void processIOException(IOExceptionEvent ioExceptionEvent) {

    }

    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }

    public void enforceTlsPolicy(ClientTransactionExt clientTransactionExt) throws SecurityException {

    }
}
