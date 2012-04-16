/**
 * JdbcAttribute.java
 * 
 *  Copyright (C) 2006  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */

package org.synchronoss.cpo.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synchronoss.cpo.CpoByteArrayInputStream;
import org.synchronoss.cpo.CpoCharArrayReader;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;



/**
 * JdbcAttribute is a class that maps traditional java classes to tables in a 
 * jdbc database. 
 * 
 * @author david berry
 */

public class JdbcAttribute extends CpoAttribute implements java.io.Serializable, java.lang.Cloneable {

    private static Logger logger = LoggerFactory.getLogger(JdbcAttribute.class.getName());

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
    private String dbTable_ = null;
    private String dbColumn_ = null;
    private int javaSqlType_ = Types.NULL;
    
    //Transform attributes
    private Object transformObject_=null;
    private Method transformIn_ = null;
    private Method transformPSOut_ = null;
    private Method transformCSOut_ = null;
    private boolean hasTransformIn = false;
    private boolean hasTransformPS = false;
    private boolean hasTransformCS = false;
    
    /**
     * @param jmc
     * @param name
     */
    public <T> JdbcAttribute(CpoClass<T> jmc, String name, String javaSqlTypeName, String dataName, String dbTable, String dbColumn, String transformClass)
    throws CpoException {
      super(jmc,name,dataName,transformClass);
        LoggerFactory.getLogger(jmc.getMetaClass().getName()).debug("Adding Attribute for class "+jmc.getMetaClass().getName()+": "+name+"("+dataName+","+dbTable+","+dbColumn+","+transformClass+")");
        setDbTable(dbTable);
        setDbColumn(dbColumn);
        setJavaSqlType(JavaSqlTypes.getJavaSqlType(javaSqlTypeName));
    }

    protected void setDbTable(String dbTable){
        dbTable_ = dbTable;
    }

    protected void setDbColumn(String dbColumn){
        dbColumn_ = dbColumn;
    }
 
    protected String getDbTable(){
        return dbTable_;
    }

    protected String getDbColumn(){
        return dbColumn_;
    }
    
    public void invokeSetter(Object obj, ResultSet rs, int idx) throws CpoException {
        JavaSqlMethod<?> jdbcMethod = null;
        Object param = null;
        Class<?> paramClass = null;
        Logger localLogger = obj==null?logger:LoggerFactory.getLogger(obj.getClass().getName());
        
        if (getSetters().length==0) 
            throw new CpoException("There are no setters");
        
        if (hasTransformIn){
    		localLogger.info("Calling Transform In:"+transformIn_.getDeclaringClass().getName());
    		
                // Get the JavaSqlMethod for the class that we are passing into the transform
                jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformIn_.getParameterTypes()[0]);
                
                try {
                    // Get the getter for the ResultSet
                    param = jdbcMethod.getRsGetter().invoke(rs,new Object[]{new Integer(idx)});
                    param = transformIn(param);
                    paramClass = transformIn_.getReturnType();
                } catch (IllegalAccessException iae){
                	localLogger.debug("Error Invoking ResultSet Method: "+ExceptionHelper.getLocalizedMessage(iae));
                    throw new CpoException(iae);
                } catch (InvocationTargetException ite){
                	localLogger.debug("Error Invoking ResultSet Method: "+ExceptionHelper.getLocalizedMessage(ite));
                    throw new CpoException(ite.getCause());
                }

        }
        
        for (int i=0; i<getSetters().length; i++){
            try{
                if (!hasTransformIn){
                    // Get the JavaSqlMethod for the class that we are passing in as the Setter parameter
                    jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getSetters()[i].getParameterTypes()[0]);
                    
                    // Get the getter for the ResultSet
                    param = jdbcMethod.getRsGetter().invoke(rs,new Object[]{new Integer(idx)});
                    paramClass = jdbcMethod.getJavaSqlMethodClass();
                }
                if (getSetters()[i].getParameterTypes()[0].isAssignableFrom(paramClass) || isPrimitiveAssignableFrom(getSetters()[i].getParameterTypes()[0], paramClass)){
                    getSetters()[i].invoke(obj, new Object[]{param});
                    return;
                }
            } catch (IllegalAccessException iae){
            	localLogger.debug("Error Invoking Setter Method: "+ExceptionHelper.getLocalizedMessage(iae));
            } catch (InvocationTargetException ite){
            	localLogger.debug("Error Invoking Setter Method: "+ExceptionHelper.getLocalizedMessage(ite));
            }
        }
        
        throw new CpoException("invokeSetter: Could not find a Setter for "+obj.getClass()+": Column<"+this.getDataName()+"> Attribute<"+this.getJavaName()+">");
    }
    
    public void invokeSetter(Object obj, CallableStatement cs, int idx) throws CpoException {
        JavaSqlMethod<?> jdbcMethod = null;
        Object param = null;
        Class<?> paramClass = null;
        Logger localLogger = obj==null?logger:LoggerFactory.getLogger(obj.getClass().getName());
        
        if (getSetters().length==0) 
            throw new CpoException("There are no setters");

        if (hasTransformCS){
    		localLogger.info("Calling Transform In:"+transformIn_.getDeclaringClass().getName());
    		
            // Get the jdbcType for the class that we are passing into the transform
            jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformIn_.getParameterTypes()[0]);
            
            try {
                // Get the getter for the Callable Statement
                param = jdbcMethod.getCsGetter().invoke(cs,new Object[]{new Integer(idx)});
                param = transformIn(param);
                paramClass = transformIn_.getReturnType();
            } catch (IllegalAccessException iae){
            	localLogger.debug("Error Invoking CallableStatement Method: "+ExceptionHelper.getLocalizedMessage(iae));
                throw new CpoException(iae);
            } catch (InvocationTargetException ite){
            	localLogger.debug("Error Invoking CallableStatement Method: "+ExceptionHelper.getLocalizedMessage(ite));
                throw new CpoException(ite.getCause());
            }
        }

        for (int i=0; i<getSetters().length; i++){
            try{
                
                if (!hasTransformCS){
                    // Get the jdbcType for the class that we are passing in as the Setter parameter
                    jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getSetters()[i].getParameterTypes()[0]);
                    
                    // Get the getter for the CallableStatement
                    // What we get from the cs will be set in the value object
                    param = jdbcMethod.getCsGetter().invoke(cs,new Object[]{new Integer(idx)});
                    paramClass = jdbcMethod.getJavaSqlMethodClass();
                }
 
                if (getSetters()[i].getParameterTypes()[0].isAssignableFrom(paramClass)){
                    getSetters()[i].invoke(obj, new Object[]{param});
                    return;
                }   
            } catch (IllegalAccessException iae){
            	localLogger.debug("Error Invoking Setter Method: "+ExceptionHelper.getLocalizedMessage(iae));
            } catch (InvocationTargetException ite){
            	localLogger.debug("Error Invoking Setter Method: "+ExceptionHelper.getLocalizedMessage(ite));
            }
        }
        
        throw new CpoException("invokeSetter: Could not find a Setter for "+obj.getClass());
    }
    
    public void invokeGetter(JdbcCallableStatementFactory jcsf, Object obj, int idx) throws CpoException {
        Object param = null;
        JavaSqlMethod<?> jdbcMethod = null;
        Logger localLogger = obj==null?logger:LoggerFactory.getLogger(obj.getClass().getName());
         
        try{
            if (hasTransformPS){
            	localLogger.info("Calling Transform Out:"+transformCSOut_.getDeclaringClass().getName());
                param = transformOut(jcsf, getGetters()[0].invoke(obj, (Object[])null));
                jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformCSOut_.getReturnType());
            } else {
                jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getGetters()[0].getReturnType());
                param = getGetters()[0].invoke(obj, (Object[])null);
            }
        	int length = 0;
        	
        	localLogger.info(this.getDataName()+"="+param);

        	switch (jdbcMethod.getMethodType()) {
        	case JavaSqlMethod.METHOD_TYPE_BASIC:
                jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(),new Object[]{new Integer(idx),param});
        		break;
        	case JavaSqlMethod.METHOD_TYPE_STREAM:
       			CpoByteArrayInputStream cbis = CpoByteArrayInputStream.getCpoStream((InputStream)param);
        	    // Get the length of the InputStream in param
                jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(),new Object[]{new Integer(idx),(InputStream) cbis, new Integer(length)});
                break;
        	case JavaSqlMethod.METHOD_TYPE_READER:
        		CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader)param);
        	    // Get the length of the Reader in param
                jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(),new Object[]{new Integer(idx),(Reader) ccar, new Integer(length)});
                break;
        	}
            return;
        } catch (IllegalAccessException iae){
        	localLogger.debug("Error Invoking Getter Method: "+ExceptionHelper.getLocalizedMessage(iae));
        } catch (InvocationTargetException ite){
        	localLogger.debug("Error Invoking Getter Method: "+ExceptionHelper.getLocalizedMessage(ite));
        }
        
        throw new CpoException("invokeGetter: Could not find a Getter for "+obj.getClass());
    }
    
    public void invokeGetter(JdbcPreparedStatementFactory jpsf, Object obj, int idx) throws CpoException {
        Object param = null;
        JavaSqlMethod<?> jdbcMethod = null;
        String msg = null;
        Logger localLogger = obj==null?logger:LoggerFactory.getLogger(obj.getClass().getName());
        try{
            if (hasTransformPS){
            	localLogger.info("Calling Transform Out:"+transformPSOut_.getDeclaringClass().getName());
                    param = transformOut(jpsf, getGetters()[0].invoke(obj, (Object[])null));
                    jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformPSOut_.getReturnType());
                    if (jdbcMethod==null)
                        throw new CpoException("Error Retrieveing Jdbc Method for type: "+transformPSOut_.getReturnType().getName());
            } else {
                    jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getGetters()[0].getReturnType());
                   param = getGetters()[0].invoke(obj, (Object[])null);
                    if (jdbcMethod==null) {
                    	localLogger.debug("jdbcMethod is null");
                        throw new CpoException("Error Retrieveing Jdbc Method for type: "+getGetters()[0].getReturnType().getName());
                    }
           }
        } catch (Exception e){
            msg = "Error Invoking Getter Method: "+getGetters()[0].getReturnType().getName()+" "+getGetters()[0].getName()+"():"+ExceptionHelper.getLocalizedMessage(e);
        } 
        
        if (msg==null){
        	localLogger.info(this.getDataName()+"="+param);
            try{
            	switch (jdbcMethod.getMethodType()) {
            	case JavaSqlMethod.METHOD_TYPE_BASIC:
                    jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(),new Object[]{new Integer(idx),param});
            		break;
            	case JavaSqlMethod.METHOD_TYPE_STREAM:
            		CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream((InputStream)param);
            	    // Get the length of the InputStream in param
                    jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(),new Object[]{new Integer(idx),(InputStream)cbais, new Integer(cbais.getLength())});
                    break;
            	case JavaSqlMethod.METHOD_TYPE_READER:
            		CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader)param);
            	    // Get the length of the Reader in param
                    jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(),new Object[]{new Integer(idx),(Reader)ccar, new Integer(ccar.getLength())});
                    break;
            	}
                return;
            } catch (Exception e){
                msg="Error Invoking Jdbc Method: "+jdbcMethod.getPsSetter().getName()+":"+ExceptionHelper.getLocalizedMessage(e);
            }
        }
        
        if (msg!=null){
        	localLogger.error(msg);
            throw new CpoException(msg);
        }
    }
    
    protected void setJavaSqlType(int type){
        javaSqlType_ = type;
        
    }
    
    protected int getJavaSqlType(){
        return this.javaSqlType_;
    }

// TODO: Get this working
//    protected void setTransformClass(String className) throws CpoException {
//        Class<?> transformClass=null;
//        Logger localLogger = className==null?logger:LoggerFactory.getLogger(className);
//        
//        try{
//        	if (className!=null && className.length()>0){
//	            try {
//	            	transformClass = Class.forName(className);
//	            } catch (Exception e){
//	            	String msg=ExceptionHelper.getLocalizedMessage(e);
//	            	
//	            	localLogger.error("Invalid Transform Class specified:<"+className+">");
//	                throw new CpoException("Invalid Transform Class specified:<"+className+">:");
//	            }
//	            
//	            this.transformObject_ = transformClass.newInstance();
//	
//              // Lets walk the hierarchy to find the transform methods
//              while (transformClass != null){
//                // go find the transformIn and transformOut classes.
//                for (Method m : transformClass.getDeclaredMethods()){
//                  // Only look at methods that we created. Ignore compiler generated methods.
//                  if (!m.isSynthetic() && !m.isBridge()){
//                    // The method must be on an implementing class not an interface and the name must match as well as the number of parameters and return types
//                    //dumpMethod(m);
//                    if (!hasTransformIn && m.getName().equals("transformIn")){
//                        this.transformIn_ = m;
//                        hasTransformIn = true;
//                    } else if (m.getName().equals("transformOut")){
//                      if (!hasTransformPS && m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory")){
//                        this.transformPSOut_ = m;
//                        hasTransformPS=true;
//                      } else if (!hasTransformCS && m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory")){
//                        this.transformCSOut_ = m;
//                        hasTransformCS=true;
//                      }
//                    }
//                  }
//                }
//                transformClass=transformClass.getEnclosingClass();
//              }
//	            
//	            if (transformIn_==null && transformPSOut_==null&&transformCSOut_==null){
//	            	localLogger.error("Invalid Transform Class specified:<"+className+">: Abstract Methods not Found");
//	                throw new CpoException("Invalid Transform Class specified:<"+className+">: Abstract Methods not Found");
//	            }
//	            
//        	}
//        } catch (CpoException ce ){
//        	throw ce;
//        } catch (Exception e){
//        	localLogger.debug("Error Setting Transform Class: "+ExceptionHelper.getLocalizedMessage(e));
//            this.transformObject_ = null;
//            this.transformIn_ = null;
//            this.transformCSOut_=null;
//            this.transformPSOut_=null;
//            throw new CpoException (e);
//        }
//        
//    }
    
    private void dumpMethod(Method m){
      logger.debug("========================");
      logger.debug("===> Declaring Class: "+m.getDeclaringClass().getName());
      logger.debug("===> Method Signature: "+m.toString());
      logger.debug("===> Generic Signature: "+m.toGenericString());
      logger.debug("===> Method isBridge: "+m.isBridge());
      logger.debug("===> Method isSynthetic: "+m.isSynthetic());
      logger.debug("========================");
    }

    protected Object transformIn(Object datasourceObject) throws CpoException{
        Object retObj = datasourceObject;
        
        if (transformObject_!=null&&transformIn_!=null){
            try{
                retObj = transformIn_.invoke(transformObject_,new Object[] {datasourceObject});
            } catch (IllegalAccessException iae){
                LoggerFactory.getLogger(transformIn_.getName()).error("Error Invoking transformIn: "+transformIn_.getName()+ExceptionHelper.getLocalizedMessage(iae));
                throw new CpoException(iae);
            } catch (InvocationTargetException ite){
            	LoggerFactory.getLogger(transformIn_.getName()).error("Error Invoking transformIn: "+transformIn_.getName()+ExceptionHelper.getLocalizedMessage(ite));
                throw new CpoException(ite.getCause());
            }
        }
        return retObj;
    }
    
    protected Object transformOut(JdbcPreparedStatementFactory jpsf, Object attributeObject) throws CpoException{
        Object retObj = attributeObject;
        
       if (transformObject_!=null&&transformPSOut_!=null){
            try{
                retObj = transformPSOut_.invoke(transformObject_,new Object[] {jpsf, attributeObject});
            } catch (IllegalAccessException iae){
            	LoggerFactory.getLogger(transformPSOut_.getName()).error("Error Invoking transformOut: "+transformPSOut_.getName()+ExceptionHelper.getLocalizedMessage(iae));
                throw new CpoException(iae);
            } catch (InvocationTargetException ite){
            	LoggerFactory.getLogger(transformPSOut_.getName()).error("Error Invoking transformOut: "+transformPSOut_.getName()+ExceptionHelper.getLocalizedMessage(ite));
                throw new CpoException(ite.getCause());
            }
        }
        return retObj;
    }
    
    protected Object transformOut(JdbcCallableStatementFactory jcsf, Object attributeObject) throws CpoException{
        Object retObj = attributeObject;
        
        if (transformObject_!=null&&transformCSOut_!=null){
            try{
                retObj = transformCSOut_.invoke(transformObject_,new Object[] {jcsf, attributeObject});
            } catch (IllegalAccessException iae){
            	LoggerFactory.getLogger(transformCSOut_.getName()).error("Error Invoking transformOut: "+transformCSOut_.getName()+ExceptionHelper.getLocalizedMessage(iae));
                throw new CpoException(iae);
            } catch (InvocationTargetException ite){
            	LoggerFactory.getLogger(transformCSOut_.getName()).error("Error Invoking transformOut: "+transformCSOut_.getName()+ExceptionHelper.getLocalizedMessage(ite));
                throw new CpoException(ite.getCause());
            }
        }
        return retObj;
    }
    
}