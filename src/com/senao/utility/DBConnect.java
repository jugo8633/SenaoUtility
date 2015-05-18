package com.senao.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.sql.*;
import javax.naming.*;

public class DBConnect
{
	public static int ERR_UNKNOW = 0;
	public static int ERR_INVALID_PARAM = -1;
	public static int ERR_EXCEPTION = -2;
	public static int CONN_SUCCESS = 1;

	private Connection con = null;

	public DBConnect()
	{

	}

	@Override
	protected void finalize() throws Throwable
	{
		close();
		super.finalize();
	}

	public int connect(final String strJDBC) throws Exception
	{
		if (!StringUtility.isValid(strJDBC))
		{
			return ERR_INVALID_PARAM;
		}

		int nResult = ERR_UNKNOW;
		final String strJDBCCon = "jdbc/" + strJDBC;
		try
		{
			javax.naming.Context initContext = new InitialContext();
			javax.naming.Context envContext = (javax.naming.Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup(strJDBCCon);

			con = ds.getConnection();
			con.setAutoCommit(false);
			nResult = CONN_SUCCESS;
		}
		catch (Exception e)
		{
			if (con != null)
			{
				con.close();
				con = null;
			}
			throw new Exception("Exception:" + e.toString());
		}

		return nResult;
	}

	public void close() throws Exception
	{
		commit();
		if (con != null)
		{
			con.close();
			con = null;
		}
	}

	public void commit() throws Exception
	{
		if (con != null && !con.isClosed())
			con.commit();
	}

	public int insert(final String strTable, HashMap<String, String> column) throws Exception
	{
		int nResult = ERR_UNKNOW;
		
		if (con != null && StringUtility.isValid(strTable) && 0 < column.size())
		{
			StringBuffer strColume = new StringBuffer();
			StringBuffer strValue = new StringBuffer();
			List<String> listValue = new ArrayList<String>();
			
			PreparedStatement prestmt  	= null;
			
			int nIndex = 1;
			for (Object key : column.keySet())
			{
				System.out.println(key + " : " + column.get(key));
				
				strColume.append(key);
				strValue.append("?");
				listValue.add(column.get(key));
				
				if(nIndex < column.size())
				{
					strColume.append(",");
					strValue.append(",");
				}
				++nIndex;
			}
			
			String strSQL = "insert into " + strTable + "(" +  strColume + ") values(" + strValue + ");";
			prestmt = con.prepareStatement(strSQL);
			
			for(int i = 0; i < listValue.size(); ++i)
			{
				prestmt.setString(1 + i,listValue.get(i));
			}
			
			int nRet = prestmt.executeUpdate();
			prestmt.close();
			if(1 == nRet)
			{
				nResult = CONN_SUCCESS;
			}
		}

		return nResult;
	}

}
