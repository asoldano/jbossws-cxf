/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.jaxws.cxf.interop.wstrust10;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.jboss.test.ws.jaxws.cxf.interop.wstrust10.interopbaseaddress.interop.IPingService;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * WS-Trust 1.0 - Symmetric binding interop test case
 *
 * This is basically the WS-Trust client used by Apache-CXF
 * for the WCF interop plugfest adapted for running with JBoss.
 * @author alessio.soldano@jboss.com
 * @since 05-May-2009
 */
public class SymmetricBindingClientTestCase extends JBossWSTest
{
   private Bus bus;
   
   public static Test suite()
   {
      return new JBossWSTestSetup(SymmetricBindingClientTestCase.class, "jaxws-cxf-interop-wstrust10-client.jar");
   }
   
   public void testScenario2() throws Exception
   {
      this.loadBus("scenario2");
      QName serviceName = new QName("http://tempuri.org/", "SymmetricFederatedService");
      Service service = Service.create(getServiceURL(), serviceName);
      QName portName = new QName("http://tempuri.org/", "Scenario_2_IssuedToken_MutualCertificate10");
      IPingService proxy = service.getPort(portName, IPingService.class);
      try
      {
         assertEquals("Hi!", proxy.echo("Hi!"));
      }
      catch (SOAPFaultException e)
      {
         throw new Exception("Please check that the Bouncy Castle provider is installed.", e);
      }
   }
   
   public void testScenario5() throws Exception
   {
      this.loadBus("scenario5");
      QName serviceName = new QName("http://tempuri.org/", "SymmetricFederatedService");
      Service service = Service.create(getServiceURL(), serviceName);
      QName portName = new QName("http://tempuri.org/", "Scenario_5_IssuedTokenForCertificate_MutualCertificate11");
      IPingService proxy = service.getPort(portName, IPingService.class);
      try
      {
         assertEquals("Hi!", proxy.echo("Hi!"));
      }
      catch (SOAPFaultException e)
      {
         throw new Exception("Please check that the Bouncy Castle provider is installed.", e);
      }
   }
   
   public void testScenario6() throws Exception
   {
      this.loadBus("scenario6");
      QName serviceName = new QName("http://tempuri.org/", "SymmetricFederatedService");
      Service service = Service.create(getServiceURL(), serviceName);
      QName portName = new QName("http://tempuri.org/", "Scenario_6_IssuedTokenForCertificateSecureConversation_MutualCertificate11");
      IPingService proxy = service.getPort(portName, IPingService.class);
      try
      {
         assertEquals("Hi!", proxy.echo("Hi!"));
      }
      catch (SOAPFaultException e)
      {
         throw new Exception("Please check that the Bouncy Castle provider is installed.", e);
      }
   }
   
   private void loadBus(String scenario) throws Exception
   {
      System.out.println("Loading bus for " + scenario + "...");
      SpringBusFactory busFactory = new SpringBusFactory();
      URL cxfConfig = getResourceURL("jaxws/cxf/interop/wstrust10/META-INF/" + scenario + "-client-config.xml");
      bus = busFactory.createBus(cxfConfig);
      BusFactory.setDefaultBus(bus);
   }
   
   @Override
   protected void tearDown() throws Exception
   {
      System.out.println("... bus teardown");
      if (bus != null)
         bus.shutdown(true);

      super.tearDown();
   }
   
   private URL getServiceURL() throws Exception
   {
      return getResourceURL("jaxws/cxf/interop/wstrust10/META-INF/wsdl/WsTrustSym.wsdl");
   }
}
