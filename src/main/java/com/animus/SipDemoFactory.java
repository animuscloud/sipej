package com.animus;


import javax.sip.InvalidArgumentException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by tojopjose on 3/20/16.
 */
public class SipDemoFactory {

    private SipProvider tlsProvider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private String hostIp;
    private int listenPort;


    public  SipDemoFactory(SipProvider tlsProvider,
                           AddressFactory addressFactory,
                           HeaderFactory headerFactory,
                           String hostIp,
                           int listenPort){
        this.tlsProvider = tlsProvider;
        this.addressFactory = addressFactory;
        this.headerFactory = headerFactory;
        this.hostIp = hostIp;
        this.listenPort = listenPort;
    }

    public SipURI makeSipUri(String name,
                             String sipAddress) throws ParseException {

        SipURI sipURI;
        sipURI = addressFactory.createSipURI(name,sipAddress);
        return sipURI;
    }

    public SipURI makeRequestUri(String user,String sipAddress) throws ParseException {
       SipURI sipURI = addressFactory.createSipURI(user,sipAddress);
       return sipURI;
    }

    public FromHeader makeFromHeader(  SipURI fromAddress,
                                     String fromDisplayName) throws ParseException {
        Address fromNameAddress = addressFactory.createAddress(fromAddress);
        fromNameAddress.setDisplayName(fromDisplayName);
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,"xxxxxxx");
        return fromHeader;
    }
    public ToHeader makeToHeader( SipURI toAddress,
                                          String toDisplayName) throws ParseException {
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(toDisplayName);
        ToHeader toHeader = headerFactory.createToHeader(toNameAddress,null);
        return toHeader;
    }

    public ArrayList<ViaHeader> createViaHeader(String ip,int port,String transport) throws ParseException, InvalidArgumentException {
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader =  headerFactory.createViaHeader(ip,port,transport,null);
        return viaHeaders;
    }

    public ContentTypeHeader makeContentTypeHeader() throws ParseException {
      return headerFactory.createContentTypeHeader("application", "sdp");
    }

    public CallIdHeader getCallerid(){
        return tlsProvider.getNewCallId();
    }

    public CSeqHeader getCsqHeader(String command) throws ParseException, InvalidArgumentException {
        return headerFactory.createCSeqHeader(1l,command);
    }

    public MaxForwardsHeader getMaxForwardHeader(int hops) throws InvalidArgumentException {
        return headerFactory.createMaxForwardsHeader(hops);
    }

    public ContactHeader createContactHeader(String fromName) throws ParseException {
        SipURI contactURI = addressFactory.createSipURI(fromName, hostIp);
        contactURI.setPort(listenPort);
        contactURI.setTransportParam("tls");
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(fromName);
        return headerFactory.createContactHeader(contactAddress);
    }

    public RouteHeader  createRouteHeader(SipURI requestURI) throws ParseException {
        SipURI routeUri = (SipURI) requestURI.clone();
        routeUri.setLrParam();
        routeUri.setTransportParam("tls");
        Address peerAddress = addressFactory.createAddress(requestURI);
        return headerFactory.createRouteHeader(peerAddress);
    }


}
