/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.plugin;

import org.apache.maven.plugin.*;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.helper.XmlBeansHelper;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.exporter.JdbcMetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.synchronoss.cpo.helper.CpoClassLoader;

/**
 * @goal convertsqltoxml
 */
public class ConvertSQLToXML extends AbstractMojo {

  /**
   * @parameter expression="${dbUrl}"
   * @required
   */
  private String dbUrl;

  /**
   * @parameter expression="${dbTablePrefix}" default-value=" "
   */
  private String dbTablePrefix;

  /**
   * @parameter expression="${dbDriver}" default-value="oracle.jdbc.OracleDriver"
   */
  private String dbDriver;

  /**
   * @parameter expression="${dbParams}"
   */
  private String dbParams;

  /**
   * @parameter expression="${filter}" default-value=".*"
   */
  private String filter;

  public static final File TARGET = new File("target");
  public static final String CPO_META_DATA_XML = "CpoMetaData.xml";

  private JdbcCpoMetaDescriptor metaDescriptor;

  public void execute(String dbUrl, String dbTablePrefix, String dbDriver, String dbParams, String filter) throws MojoExecutionException {
    this.dbUrl = dbUrl;
    this.dbTablePrefix = dbTablePrefix;
    this.dbDriver = dbDriver;
    this.dbParams = dbParams;
    this.filter = filter;
    execute();
  }

  @Override
	public void execute() throws MojoExecutionException {
		getLog().info("Converting SQL to XML...");

    getLog().info("dbUrl: " + dbUrl);
    getLog().info("dbTablePrefix: " + dbTablePrefix);
    getLog().info("Class: " + dbDriver);
    
    try {
      metaDescriptor = new JdbcCpoMetaDescriptor("Converter");
    } catch (Exception e) {
      throw new MojoExecutionException("Couldn't load the MetaDescriptor");
    }
    
    try {
      Class<?> driverClass = CpoClassLoader.forName(dbDriver);
    } catch (Exception e) {
      throw new MojoExecutionException("Couldn't location driver class");
    }

    Properties connectionProperties = new Properties();
    if (dbParams != null && !dbParams.equals("")) {
      StringTokenizer st = new StringTokenizer(dbParams, ";");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        StringTokenizer stNameValue = new StringTokenizer(token, "=");
        String name = null, value = null;
        if (stNameValue.hasMoreTokens())
          name = stNameValue.nextToken();
        if (stNameValue.hasMoreTokens())
          value = stNameValue.nextToken();
        connectionProperties.setProperty(name, value);
      }
    }

    Connection conn = null;
    try {
      conn = DriverManager.getConnection(dbUrl, connectionProperties);
      conn.setAutoCommit(false);

      List<CpoClass> classes = getClasses(conn);

      for (CpoClass cpoClass : classes) {
        for (CpoAttribute att : getAttributes(cpoClass,  conn)) {
          cpoClass.addAttribute(att);
        }

        for (CpoFunctionGroup fg : getFunctionGroups(cpoClass, conn)) {
          cpoClass.addFunctionGroup(fg);
        }
      }

      // Force the metaDescriptor class here, because we know it's from a database
      MetaXmlObjectExporter exporter = new JdbcMetaXmlObjectExporter(metaDescriptor);
      for (CpoClass cpoClass : classes) {
        cpoClass.acceptMetaDFVisitor(exporter);
      }
      CpoMetaDataDocument cpoMetaDataDocument = exporter.getCpoMetaDataDocument();

      // if target folder doesn't exist, make it
      if (!TARGET.exists()) {
        TARGET.mkdir();
      }

      // save to file
      cpoMetaDataDocument.save(new File(TARGET, CPO_META_DATA_XML), XmlBeansHelper.getXmlOptions());

    } catch (Exception ex) {
      getLog().error("Exception caught", ex);
      throw new MojoExecutionException(ex.getMessage(), ex);
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException ex) {
          // ignore
        }
      }
    }
	}

  private List<CpoClass> getClasses(Connection conn) throws SQLException {
    List<CpoClass> classes = new ArrayList<CpoClass>();

    StringBuilder sql = new StringBuilder();
    sql.append("select name from ");
    sql.append(dbTablePrefix);
    sql.append("cpo_class order by name");
    getLog().debug("getClasses() SQL: " + sql);

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql.toString());
      rs = ps.executeQuery();
      while (rs.next()) {
        String className = rs.getString(1);

        // check the filter, if there is one
        if (filter != null && className.matches(filter)) {
          CpoClass cpoClass = new CpoClass();
          cpoClass.setName(className);
          classes.add(cpoClass);
        }
      }
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          // ignore
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          // ignore
        }
      }
    }
    return classes;
  }

  private List<CpoAttribute> getAttributes(CpoClass cpoClass, Connection conn) throws Exception {
    List<CpoAttribute> attributes = new ArrayList<CpoAttribute>();

    StringBuilder sql = new StringBuilder();
    sql.append("select cam.column_name, cam.attribute, cam.column_type, cam.db_table, cam.db_column, cam.transform_class from ");
    sql.append(dbTablePrefix);
    sql.append("cpo_attribute_map cam, ");
    sql.append(dbTablePrefix);
    sql.append("cpo_class cc where cc.name = ? and cam.class_id = cc.class_id ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    getLog().debug("loadAttribute Sql <" + sql.toString() + ">");

    try {
      ps = conn.prepareStatement(sql.toString());
      ps.setString(1, cpoClass.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        JdbcCpoAttribute cpoAttribute = new JdbcCpoAttribute();
        cpoAttribute.setDataName(rs.getString(1));
        cpoAttribute.setJavaName(rs.getString(2));
        cpoAttribute.setDataType(rs.getString(3));
        cpoAttribute.setDbTable(rs.getString(4));
        cpoAttribute.setDbColumn(rs.getString(5));
        cpoAttribute.setTransformClassName(rs.getString(6));

        if (cpoAttribute.getTransformClassName()!=null) {
          try {
            cpoAttribute.loadRunTimeInfo(metaDescriptor, null);
          } catch (Exception e) {

          }
        }
        // figure out the java type
        cpoAttribute.setJavaType(metaDescriptor.getJavaTypeName(cpoAttribute));

        attributes.add(cpoAttribute);
      }
    } catch (Exception ex) {
      String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + cpoClass.getName();
      getLog().error(msg, ex);
      throw ex;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          // ignore
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          // ignore
        }
      }
    }

    return attributes;
  }

  private List<CpoFunctionGroup> getFunctionGroups(CpoClass cpoClass, Connection conn) {
    List<CpoFunctionGroup> functionGroups = new ArrayList<CpoFunctionGroup>();

    StringBuilder sql = new StringBuilder();
    sql.append("select cqg.group_type, cqg.name, cqt.sql_text, cqt.description, cam.attribute, cqp.param_type ");
    sql.append("from " + dbTablePrefix + "cpo_query_group cqg ");
    sql.append("join " + dbTablePrefix + "cpo_class cc on cqg.class_id = cc.class_id and cc.name = ? ");
    sql.append("left outer join " + dbTablePrefix + "cpo_query cq on cqg.group_id = cq.group_id ");
    sql.append("left outer join " + dbTablePrefix + "cpo_query_text cqt on cq.text_id = cqt.text_id ");
    sql.append("left outer join " + dbTablePrefix + "cpo_query_parameter cqp on cq.query_id = cqp.query_id ");
    sql.append("left outer join " + dbTablePrefix + "cpo_attribute_map cam on cqp.attribute_id = cam.attribute_id ");
    sql.append("order by cqg.group_type, cqg.name, cqg.group_id, cq.seq_no, cqp.seq_no ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    getLog().debug("functionGroup Sql <" + sql.toString() + ">");

    try {
      ps = conn.prepareStatement(sql.toString());
      ps.setString(1, cpoClass.getName());
      rs = ps.executeQuery();

      String lastFunctionGroupName = null;

      CpoFunctionGroup functionGroup = null;
      CpoFunction function = null;

      while (rs.next()) {
        String groupType = rs.getString(1);
        String groupName = rs.getString(2);
        String expression = rs.getString(3);
        String functionName = rs.getString(4);
        String attribute = rs.getString(5);
        String paramType = rs.getString(6);

        StringBuilder fgNamebuf = new StringBuilder();
        fgNamebuf.append(groupType);
        fgNamebuf.append("@");
        if (groupName != null) {
          fgNamebuf.append(groupName);
        }

        // if the group changed, make a new group
        if (functionGroup == null || !fgNamebuf.toString().equals(lastFunctionGroupName)) {
          functionGroup = new CpoFunctionGroup();
          functionGroup.setType(groupType);
          functionGroup.setName(groupName);

          functionGroups.add(functionGroup);

          // changed group, reset the function
          function = null;

          lastFunctionGroupName = fgNamebuf.toString();
        }

        // if the function changed, make a new one
        if (function == null || !function.getExpression().equals(expression)) {

          if (expression == null || expression.isEmpty()) {
            // ignore if the sql is null/empty
            getLog().warn("Query Group[" + groupName + "] contained no sql, so ignoring it");
          } else {
            if (functionName == null || functionName.isEmpty()) {
              // the name is null/empty
              getLog().warn("Function Name [" + functionName + "] was null, so using group name [" + groupName + "]");
              functionName = groupName;
            }
            function = new CpoFunction();
            function.setName(functionName);
            function.setExpression(expression);

            functionGroup.addFunction(function);
          }
        }

        if (attribute != null && function != null) {
          JdbcCpoArgument argument = new JdbcCpoArgument();
          argument.setAttributeName(attribute);
          argument.setScope(paramType);

          function.addArgument(argument);
        }
      }
    } catch (SQLException ex) {
      String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + cpoClass.getName();
      getLog().error(msg, ex);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          // ignore
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          // ignore
        }
      }
    }

    return functionGroups;
  }
}

