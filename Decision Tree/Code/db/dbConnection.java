package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;

import java.util.List;
import java.util.ArrayList;

public class dbConnection {

	String url;
	String username;
	String password;

	Statement statement;
	Connection connection;

	double countValue;
	List<String> attributes;
	List<String>[] trainingExample;
	List<List<String>> trainingExampleList;
	String[] distinctColumnValues;

	public dbConnection() throws SQLException, ClassNotFoundException {

		username = "ML";
		password = "ML";
		url = "jdbc:oracle:thin:@localhost:1521:xe";
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection(url, username, password);

		countValue = 0;
		attributes = new ArrayList<String>();
		statement = connection.createStatement();
	}

	public double rowCount(String tableName) throws SQLException {

		String query = "select COUNT(*) from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			countValue = resultSet.getInt(1);

		return countValue;
	}

	public void deleteTable(String tableName) throws SQLException {
		String query = "drop table " + tableName;
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void createTableIris(String tableName) throws SQLException {
		String query = "CREATE TABLE " + tableName
				+ " ( sepalLength varchar(4), sepalWidth varchar(4), petalLength varchar(4), petalWidth  varchar(4), Iris varchar (30))";
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void createTableTennis(String tableName) throws SQLException {
		String query = "CREATE TABLE " + tableName
				+ " ( Outlook  varchar(10), Temperature varchar(10), Humidity varchar(10), Wind varchar(10), PlayTennis varchar (30))";
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public List<String> columnNames(String tableName) throws SQLException {

		int i = 1;
		attributes = new ArrayList<String>();
		String query = "select * from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int columnCount = resultSetMetaData.getColumnCount();

		while (i <= columnCount) {
			attributes.add(resultSetMetaData.getColumnName(i));
			i++;
		}

		return attributes;
	}

	public String[] distinctColumnValuesMethod(String columnName, String tableName)
			throws SQLException, ClassNotFoundException {

		if (columnName != null) {

			countValue = distinctCount(columnName, tableName);
			attributes = select(columnName, tableName);
			distinctColumnValues = new String[(int) countValue];

			List<String> temp = new ArrayList<String>();
			for (int i = 0; i < attributes.size(); i++) {
				if (temp.contains(attributes.get(i)))
					continue;
				else
					temp.add(attributes.get(i));
			}

			for (int i = 0; i < temp.size(); i++)
				distinctColumnValues[i] = temp.get(i);

			return distinctColumnValues;
		} else
			return null;
	}

	public void addColumn(String columnName, String tableName) throws SQLException {

		String query = "alter table " + tableName + " add " + columnName + " varchar(30)";
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void dropColumn(String columnName, String tableName) throws SQLException {

		String query = "alter table " + tableName + " drop column " + columnName;
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void insertRow(String[] rowValues, String tableName) throws SQLException {

		String query = "insert into " + tableName + " values ( '" + rowValues[0] + "' , '" + rowValues[1] + "' , '"
				+ rowValues[2] + "' , '" + rowValues[3] + "' , '" + rowValues[4] + "'  ) ";
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void deleteColumn(String columnName, String tableName) throws SQLException {
		String query = "alter table " + tableName + " drop column " + columnName;
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public List<String> select(String columName, String tableName) throws SQLException {

		attributes = new ArrayList<String>();
		String query = "select " + columName + " from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			attributes.add(resultSet.getString(1));

		return attributes;
	}

	public double distinctCount(String columnName, String tableName) throws SQLException {

		String query = "select count (distinct " + columnName + ") from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			countValue = resultSet.getInt(1);

		return countValue;
	}

	public double count(String columnValue, String columnName, String tableName) throws SQLException {

		String query = "select count (" + columnName + ") from " + tableName + " where " + columnName + " = '"
				+ columnValue + "'";
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			countValue = resultSet.getInt(1);

		return countValue;
	}

	public void updateColumnNo(double columnValue, String columnName, String newColumnName, String tableName)
			throws SQLException {

		String query = "update " + tableName + " set " + newColumnName + " = 'no' where " + columnName + " < "
				+ columnValue;
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public void updateColumnYes(double columnValue, String columnName, String newColumnName, String tableName)
			throws SQLException {

		String query = "update " + tableName + " set " + newColumnName + " = 'yes' where " + columnName + " >= "
				+ columnValue;
		ResultSet resultSet = statement.executeQuery(query);
		return;
	}

	public double columnAttributeValuesCount(String focusColumnValue, String focusColumnName, String columnValue,
			String columnName, String classValueYN, String classNameYN, String tableName) throws SQLException {

		String query = "select count (" + columnName + ") from " + tableName + " where " + focusColumnName + " = '"
				+ focusColumnValue + "'" + " and " + columnName + " = '" + columnValue + "' and " + classNameYN + " = '"
				+ classValueYN + "'";
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			countValue = resultSet.getInt(1);

		return countValue;
	}

	public double columnAttributeCountForLeafNode(String rootColumnValue, String rootColumnName, String columnValue,
			String columnName, String classValueYN, String classNameYN, String tableName) throws SQLException {

		if (columnName != null) {

			String query = "select count (" + columnName + ") from " + tableName + " where " + rootColumnName + " = '"
					+ rootColumnValue + "' and " + columnName + " = '" + columnValue + "' and " + classNameYN + " = '"
					+ classValueYN + "'";
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next())
				countValue = resultSet.getInt(1);

			return countValue;
		}
		return -1;
	}

	public double columnAttributeCount(String columnValue, String columnName, String classValueYN, String classNameYN,
			String tableName) throws SQLException {

		String query = "select count (" + columnName + ") from " + tableName + " where " + columnName + " = '"
				+ columnValue + "' and " + classNameYN + " = '" + classValueYN + "'";
		ResultSet resultSet = statement.executeQuery(query);

		while (resultSet.next())
			countValue = resultSet.getInt(1);

		return countValue;
	}

	public List<List<String>> orderBy(String columName, String tableName) throws SQLException, ClassNotFoundException {

		int i = 0;
		dbConnection dbt = new dbConnection();
		String query = "select * from " + tableName + " order by " + columName;
		ResultSet resultSet = statement.executeQuery(query);
		trainingExampleList = new ArrayList<List<String>>();
		int rowCountNumber = (int) dbt.rowCount(tableName);

		trainingExample = new ArrayList[rowCountNumber];

		while (resultSet.next()) {

			trainingExample[i] = new ArrayList<String>();
			trainingExample[i].add(resultSet.getString(1));
			trainingExample[i].add(resultSet.getString(2));
			trainingExample[i].add(resultSet.getString(3));
			trainingExample[i].add(resultSet.getString(4));
			trainingExample[i].add(resultSet.getString(5));
			
			trainingExampleList.add(trainingExample[i]);
			i++;
		}
		return trainingExampleList;
	}

	@SuppressWarnings("unchecked")
	public List<List<String>> trainingExamples(String tableName) throws SQLException {

		int i = 0;
		int rowCountNumber = (int) rowCount(tableName);
		String query = "select * from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);
		trainingExampleList = new ArrayList<List<String>>();
		trainingExample = new ArrayList[rowCountNumber];

		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (resultSet.next()) {
			trainingExample[i] = new ArrayList<String>();

			for (int j = 1; j <= columnCount; j++)
				trainingExample[i].add(resultSet.getString(j));

			trainingExampleList.add(trainingExample[i]);
			i++;
		}
		return trainingExampleList;
	}

	public List<List<String>> trainingExamplesTrainingSet(String tableName) throws SQLException {

		int i = 0;
		int rowCountNumber = (int) rowCount(tableName);
		rowCountNumber *= 0.7;
		String query = "select * from " + tableName;
		ResultSet resultSet = statement.executeQuery(query);
		trainingExampleList = new ArrayList<List<String>>();
		trainingExample = new ArrayList[rowCountNumber];

		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (resultSet.next() && i < rowCountNumber) {
			trainingExample[i] = new ArrayList<String>();

			for (int j = 1; j <= columnCount; j++)
				trainingExample[i].add(resultSet.getString(j));

			trainingExampleList.add(trainingExample[i]);
			i++;
		}
		return trainingExampleList;
	}

	@SuppressWarnings("unchecked")
	public List<List<String>> newTrainingExamples(String columnValue, String columnName, String tableName)
			throws SQLException {

		int i = 0;
		int rowCountNumber = (int) rowCount(tableName);
		String query = "select * from " + tableName + " where " + columnName + " = '" + columnValue + "'";
		ResultSet resultSet = statement.executeQuery(query);
		trainingExampleList = new ArrayList<List<String>>();
		trainingExample = new ArrayList[rowCountNumber];

		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (resultSet.next()) {
			trainingExample[i] = new ArrayList<String>();

			for (int j = 1; j <= columnCount; j++)
				trainingExample[i].add(resultSet.getString(j));

			trainingExampleList.add(trainingExample[i]);
			i++;
		}
		return trainingExampleList;
	}

	public void reorderColumn(String tableName) throws SQLException {

		String line1 = "create table tempTable as select SEPALLENGTH_026, SEPALWIDTH_120, PETALLENGTH_26, PETALWIDTH_34,IRIS from "
				+ tableName;
		statement.executeQuery(line1);

		String line2 = "rename " + tableName + " to oldTable";
		statement.executeQuery(line2);

		String line3 = "rename tempTable to " + tableName;
		statement.executeQuery(line3);

		String line4 = "drop table oldTable";
		statement.executeQuery(line4);
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {

		dbConnection db = new dbConnection();
		// db.createTableIris("TrainIrisData");

		String tableName = "TrainTennisData";
		db.reorderColumn(tableName);
		// db.trainingExamplesTrainingSet(tableName);
		// db.createTable(tableName);
		// db.rowCount(tableName);

		// IrisTree ir = new IrisTree();
		// db.deleteTable("TrainIrisDataString");
		// ir.recoverTableValues(tableName);
	}

}
