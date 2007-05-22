/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.wsf.stack.xfire.metadata.services;

//$Id$

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.wsf.spi.deployment.WSDeploymentException;
import org.jboss.wsf.spi.utils.IOUtils;

//$Id$

/**
 * Metadata model for xfire services.xml 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 21-May-2007
 */
public class DDBeans
{
   // The Required services.
   private List<DDService> services = new ArrayList<DDService>();
   // Optional additional beans.
   private List<DDBean> beans = new ArrayList<DDBean>();
   // The derived temp file
   private File tmpFile;

   public List<DDService> getServices()
   {
      return services;
   }

   public void addService(DDService service)
   {
      services.add(service);
   }

   public List<DDBean> getBeans()
   {
      return beans;
   }

   public void addBean(DDBean bean)
   {
      beans.add(bean);
   }

   public URL createFileURL()
   {
      destroyFileURL();

      try
      {
         File tmpDir = IOUtils.createTempDirectory();
         tmpFile = File.createTempFile("jbossws-xfire-services", ".xml", tmpDir);
         Writer writer = new OutputStreamWriter(new FileOutputStream(tmpFile));
         writeTo(writer);
         writer.close();

         return tmpFile.toURL();
      }
      catch (IOException ex)
      {
         throw new WSDeploymentException(ex);
      }
   }

   public void destroyFileURL()
   {
      if (tmpFile != null)
      {
         tmpFile.delete();
         tmpFile = null;
      }
   }

   public void writeTo(Writer writer) throws IOException
   {
      writer.write("<beans xmlns='http://xfire.codehaus.org/config/1.0'>");
      for (DDService service : services)
      {
         service.writeTo(writer);
      }
      for (DDBean bean : beans)
      {
         bean.writeTo(writer);
      }
      writer.write("</beans>");
   }

}