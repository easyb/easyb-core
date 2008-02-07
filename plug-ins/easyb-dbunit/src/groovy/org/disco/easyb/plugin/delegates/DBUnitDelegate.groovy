package org.disco.easyb.plugin.delegates

import groovy.lang.Closure

import java.io.ByteArrayInputStream
import java.io.IOException
import java.sql.DriverManager
import java.sql.SQLException

import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.DataSetException
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.operation.DatabaseOperation

import org.disco.easyb.core.delegates.Plugable

/**
 *
 * @author aglover
 *
 */
public class DBUnitDelegate implements Plugable{
	/**
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param pssword
	 * @param model
	 * @throws Throwable
	 */
	public void database_model(final String driver, final String url, final String user,
			final String pssword, final Closure model) throws Throwable{

		final IDatabaseConnection conn =
				this.getConnection(driver, url, user, pssword)
		final IDataSet data = this.getDataSet(model)
		try {
			DatabaseOperation.CLEAN_INSERT.execute(conn, data)
		} finally {
			conn.close()
		}
	}
	/**
	 * @param Closure model-- which could be anything that returns a String
	 * @return DbUnit's IDataSet type
	 * @throws IOException
	 * @throws DataSetException
	 */
	private IDataSet getDataSet(final Closure model) throws IOException, DataSetException {
		final String datastr = (String)model.call()
		final IDataSet data =
				new FlatXmlDataSet(new ByteArrayInputStream(datastr.getBytes()))
		return data
	}
	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private IDatabaseConnection getConnection(final String driver,
			final String url, final String user, final String pssword ) throws ClassNotFoundException,
			SQLException {
		Class.forName(driver)
		return new DatabaseConnection(DriverManager.getConnection(url, user, pssword))
	}

}
