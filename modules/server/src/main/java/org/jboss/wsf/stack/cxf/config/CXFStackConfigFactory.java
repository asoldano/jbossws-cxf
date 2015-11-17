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
package org.jboss.wsf.stack.cxf.config;

import static org.jboss.wsf.stack.cxf.Loggers.ROOT_LOGGER;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.ws.security.WSSConfig;
import org.jboss.wsf.spi.classloading.ClassLoaderProvider;
import org.jboss.wsf.spi.management.StackConfig;
import org.jboss.wsf.spi.management.StackConfigFactory;
import org.jboss.wsf.stack.cxf.addressRewrite.SoapAddressRewriteHelper;

/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 25-May-2009
 *
 */
public class CXFStackConfigFactory extends StackConfigFactory
{
   @Override
   public StackConfig getStackConfig()
   {
      return new CXFStackConfig();
   }
}

class CXFStackConfig implements StackConfig
{
   public CXFStackConfig()
   {
      final ClassLoader orig = getContextClassLoader();
      //try early configuration of xmlsec engine through WSS4J:
      //* to avoid doing this later when the TCCL won't have visibility over the xmlsec internals
      //* to make sure any ws client will also have full xmlsec functionalities setup (BC enabled, etc.)
      try
      {
         setContextClassLoader(ClassLoaderProvider.getDefaultProvider().getServerIntegrationClassLoader());
         WSSConfig.init();
      }
      catch (Exception e)
      {
         ROOT_LOGGER.couldNotInitSecurityEngine();
         ROOT_LOGGER.errorGettingWSSConfig(e);
      }
      finally
      {
         setContextClassLoader(orig);
      }
   }

   public String getImplementationTitle()
   {
      return getClass().getPackage().getImplementationTitle();
   }

   public String getImplementationVersion()
   {
      return getClass().getPackage().getImplementationVersion();
   }
   
   /**
    * Get context classloader.
    * 
    * @return the current context classloader
    */
   private static ClassLoader getContextClassLoader()
   {
      SecurityManager sm = System.getSecurityManager();
      if (sm == null)
      {
         return Thread.currentThread().getContextClassLoader();
      }
      else
      {
         return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run()
            {
               return Thread.currentThread().getContextClassLoader();
            }
         });
      }
   }
   
   /**
    * Set context classloader.
    *
    * @param classLoader the classloader
    */
   private static void setContextClassLoader(final ClassLoader classLoader)
   {
      if (System.getSecurityManager() == null)
      {
         Thread.currentThread().setContextClassLoader(classLoader);
      }
      else
      {
         AccessController.doPrivileged(new PrivilegedAction<Object>()
         {
            public Object run()
            {
               Thread.currentThread().setContextClassLoader(classLoader);
               return null;
            }
         });
      }
   }

   @Override
   public void validatePathRewriteRule(String rule)
   {
      SoapAddressRewriteHelper.validatePathRewriteRule(rule);
   }
}