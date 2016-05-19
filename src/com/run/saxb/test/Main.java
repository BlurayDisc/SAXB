package com.run.saxb.test;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * asd
 * <br/>
 * <br/>
 * @author RuN
 *
 * @param <B>
 */
public class Main {

	public static void main(String[] args) throws SQLException {
		try {
			Double.parseDouble("sadasd");
		} catch (Exception exception) {
			System.out.println(1);
			throw exception;
		} finally {
			System.out.println(2);
		}
		
		ResultSet rs = null;
		rs.deleteRow();
	}
}
