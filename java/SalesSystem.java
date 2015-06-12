import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.sql.*;

public class SalesSystem {
	private int state;
	private final int[][] mapping = {{-1,1,2},{-1,3,4,5,6},{-1,7,8,9,10,11},{-1,12,13,14},{-1,15,16}};
	
	SalesSystem() {
		state = 0;
	}
	
	private void parseData(PreparedStatement pstmt, String filename, int inputInt) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				String[] splitLine = line.split("\t");
			
				if( inputInt == 5 ) {
					pstmt.setNull(1, java.sql.Types.INTEGER);
					for (int i = 1; i < splitLine.length; i++) {
						pstmt.setString(i+1, splitLine[i]);
					}
				} else {
					for (int i = 0; i < splitLine.length; i++) {
						pstmt.setString(i+1, splitLine[i]);
					}
				}
				
				pstmt.executeUpdate();
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void displayResult(String[] itemName, int numberOfVal, ResultSet rs) {
		
		try {
			int[] maxLength = new int[numberOfVal];
			Arrays.fill(maxLength, -1);

			while (rs.next()) {
				for (int i = 0; i < numberOfVal; i++) {
					int rsLength = rs.getString(i+1).length();
					int itemNameLength = itemName[i].length();
					
					maxLength[i] = Math.max(rsLength, maxLength[i]);
					maxLength[i] = Math.max(itemNameLength, maxLength[i]);
				}
			}
			
			String titleFormat = "";
			String[] col = new String[numberOfVal];
			for (int i = 0; i < numberOfVal; i++) {
				titleFormat += "| %" + maxLength[i] + "s ";
				col[i] = itemName[i];
			}
			titleFormat += "|\n";
			System.out.format(titleFormat, col);
			
			String itemFormat = "";
			for (int i = 0; i < numberOfVal; i++)
				itemFormat += "| %" + maxLength[i] + "s ";
			itemFormat += "|\n";
			
			rs.beforeFirst();
			
			while (rs.next()) {
				for (int i = 0; i < numberOfVal; i++) {
					col[i] = rs.getString(i+1);
				}
				System.out.format(itemFormat, col);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void startMenu() {
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12";
		String user = "c108";
		String password = "ufjsrhvp";
		
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		
		String[] targetTable = {null, "category", "manufacturer", "part", "salesperson", "transaction"};
		int[] numberOfVal = {-1, 2, 4, 6, 4, 4, 3, 3};
		String[][] itemName = {
				null,
				{"ID", "Name"},
				{"ID", "Name", "Address", "Number"},
				{"ID", "Name", "Price", "Manufacturer ID", "Category ID", "Available Quantity"},
				{"ID", "Name", "Address", "Phone Number"},
				{"Transaction ID", "Part ID", "Salesperson ID", "Transaction Date"},
				{"Category ID", "Category Name", "Total Sales Value"},
				{"Salesperson ID", "Salesperson Name","Total Sales Value"}
				};
		ResultSet rs;
		
		try { 
			Class.forName(driver);
			conn = DriverManager.getConnection(url,user,password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			int inputInt;
			String inputStr = null;
			Scanner sc = new Scanner(System.in);
			while (true) {
				switch (state) {
				case 0:
					System.out.print("Welcome to Sales System!\n"
							+ "What operation would you like to perform?\n"
							+ "1. Data Manipulation\n"
							+ "2. General Data Operations\n"
							+ "Enter your Choice: ");
					inputInt = sc.nextInt();
					state = mapping[state][inputInt];
					break;
				case 1:
					System.out.print("What operation would you like to perform?\n"
							+ "1. Create All Tables\n"
							+ "2. Delete All Tables\n"
							+ "3. Load Data into Tables\n"
							+ "4. Show Information in Tables\n"
							+ "Enter your Choice: ");
					inputInt = sc.nextInt();
					state = mapping[state][inputInt];
					break;
				case 2:
					System.out.print("What operation would you like to perform?\n"
							+ "1. Search and list Parts\n"
							+ "2. Enter a new transaction\n"
							+ "3. Show the transaction history\n"
							+ "4. Rank category sales in ascending order of their total sales value\n"
							+ "5. Rank salespersons sales in ascending order of their total sales value\n"
							+ "Enter your Choice: ");
					inputInt = sc.nextInt();
					state = mapping[state][inputInt];
					break;
				case 3:
					System.out.print("Processing.....");
					stmt.executeUpdate(
							"CREATE TABLE category (" + 
								"cid INTEGER NOT NULL," + 
								"name VARCHAR2(20) NOT NULL," + 
								"CONSTRAINT category_pk PRIMARY KEY (CID) ENABLE," + 
								"CONSTRAINT category_cid_chk CHECK (cid >= 0 AND cid < 10) ENABLE)" 
							);
					stmt.executeUpdate(
							"CREATE TABLE manufacturer (" + 
								"mid INTEGER NOT NULL," + 
								"name VARCHAR2(20) NOT NULL," + 
								"address VARCHAR2(50) NOT NULL," + 
								"phoneNumber INTEGER NOT NULL," + 
								"CONSTRAINT manufacturer_pk PRIMARY KEY (MID) ENABLE,"+
								"CONSTRAINT manufacturer_mid_chk CHECK (mid >= 0 AND mid < 100) ENABLE)"
							);
					stmt.executeUpdate(
							"CREATE TABLE part (" + 
								"pid INTEGER NOT NULL," + 
								"name VARCHAR2(20) NOT NULL," + 
								"price INTEGER NOT NULL," + 
								"manufacturer_id INTEGER NOT NULL," + 
								"category_id INTEGER NOT NULL," + 
								"available_quantity INTEGER NOT NULL," + 
								"CONSTRAINT part_pk PRIMARY KEY (pid) ENABLE," + 
								"CONSTRAINT part_category_fk1 FOREIGN KEY (category_id) REFERENCES category (cid) ENABLE," + 
								"CONSTRAINT part_manufacturer_fk1 FOREIGN KEY (manufacturer_id) REFERENCES manufacturer (mid) ENABLE," +
								"CONSTRAINT part_pid_chk CHECK (pid >= 0 AND pid < 1000) ENABLE," +
								"CONSTRAINT part_available_quantity_chk CHECK (available_quantity >= 0 AND available_quantity < 100) ENABLE)" 
							);
					stmt.executeUpdate(
							"CREATE TABLE salesperson (" + 
								"sid INTEGER NOT NULL," + 
								"name VARCHAR2(20) NOT NULL," + 
								"address VARCHAR2(50) NOT NULL," + 
								"phoneNumber INTEGER NOT NULL," + 
								"CONSTRAINT salesperson_pk PRIMARY KEY (sid) ENABLE," +
								"CONSTRAINT salesperson_sid_chk CHECK (sid >= 0 AND sid < 100) ENABLE)"
							);
					stmt.executeUpdate("CREATE SEQUENCE transaction_seq");
					stmt.executeUpdate(
							"CREATE TABLE transaction (" + 
								"tid INTEGER NOT NULL," + 
								"pid INTEGER NOT NULL," + 
								"sid INTEGER NOT NULL," + 
								"transaction_date VARCHAR2(20) NOT NULL," + 
								"CONSTRAINT transaction_pk PRIMARY KEY (tid,pid,sid) ENABLE," + 
								"CONSTRAINT transaction_part_fk1 FOREIGN KEY (pid) REFERENCES part (pid) ENABLE," + 
								"CONSTRAINT transaction_salesperson_fk1 FOREIGN KEY (sid) REFERENCES salesperson (sid) ENABLE," +
								"CONSTRAINT transaction_tid_chk CHECK (tid >= 0 AND tid < 10000) ENABLE)"
							);
					stmt.executeUpdate(
							"CREATE OR REPLACE TRIGGER transaction_trg\n" + 
								"BEFORE INSERT ON transaction\n" + 
								"FOR EACH ROW\n" + 
								"BEGIN\n" + 
									"IF :new.tid IS NULL THEN\n" + 
										"SELECT transaction_seq.nextval INTO :new.tid FROM dual;\n" + 
									"END IF;\n" + 
								"END;"
							);
					System.out.print("Done\n");
					state = 0;
					break;
				case 4:
					System.out.print("Processing.....");
					stmt.executeUpdate("DROP TABLE transaction");
					stmt.executeUpdate("DROP TABLE part"); 
					stmt.executeUpdate("DROP TABLE salesperson"); 
					stmt.executeUpdate("DROP TABLE category");
					stmt.executeUpdate("DROP TABLE manufacturer"); 
					stmt.executeUpdate("DROP SEQUENCE transaction_seq");
					System.out.print("Done\n");
					state = 0;
					break;
				case 5:
					System.out.print("Type in the Source Date File Path: ");
					inputStr = sc.next();
					System.out.print("Which is the target table?\n"
							+ "1. Category\n"
							+ "2. Manufacturer\n"
							+ "3. Part\n"
							+ "4. Salesperson\n"
							+ "5. Transaction Record\n"
							+ "Type in the Target Table: ");
					inputInt = sc.nextInt();
					
					System.out.print("Processing.....");
					
					StringBuilder questionMarks = new StringBuilder();
					for (int i = 0; i < numberOfVal[inputInt] - 1; i++) {
						questionMarks.append("?,");
					}
					questionMarks.append("?");
					
					PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO " + targetTable[inputInt] + " VALUES (" + questionMarks + ")");
					parseData(insertStmt, inputStr, inputInt);
					
					System.out.print("Done\n");
					state = 0;
					break;
				case 6:
					System.out.print("Which table do you want to show?\n"
							+ "1. Category\n"
							+ "2. Manufacturer\n"
							+ "3. Part\n"
							+ "4. Salesperson\n"
							+ "5. Transaction Record\n"
							+ "Type in the Target Table: ");
					inputInt = sc.nextInt();
					
					rs = stmt.executeQuery("SELECT * FROM " + targetTable[inputInt]);

					System.out.print("Results:\n");
					
					displayResult(itemName[inputInt], numberOfVal[inputInt], rs);
					
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				case 7:
					System.out.print("Choose the Search criterion:\n"
							+ "1. Category Name\n"
							+ "2. Manufacturer Name\n"
							+ "3. Part Name\n"
							+ "Enter your Choice: ");
					inputInt = sc.nextInt();
					state = mapping[state-4][inputInt];
					break;
				case 8:
					int salesID, itemID;
					System.out.print("Enter Your Sales ID: ");
					salesID = sc.nextInt();
					System.out.print("Enter Your Item ID: ");
					itemID = sc.nextInt();
					
					System.out.print("Processing.....");
					
					String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()); // for getting the current date
					
					try {
						pstmt = conn.prepareStatement("INSERT INTO transaction (pid, sid, transaction_date) VALUES (?,?,?)");
						pstmt.setInt(1, itemID);
						pstmt.setInt(2, salesID);
						pstmt.setString(3, timeStamp);
						pstmt.executeUpdate();
					
						System.out.print("Done\n");
						
						rs = stmt.executeQuery("SELECT p.price FROM part p WHERE p.pid = '" + itemID + "'");
						if (rs.next())
							System.out.println("Transaction Value: HK$" + rs.getString(1));
						
					}catch(Exception e){
						System.out.print("\nSales Id or Item Id not found.\n");
						e.printStackTrace();
						
					}
					
					
					state = 0;
					break;
				case 9:
					System.out.print("Choose the Search criterion: \n"
							+ "1. Salesperson ID\n"
							+ "2. Part ID\n"
							+ "Enter your Choice: ");
					inputInt = sc.nextInt();
					state = mapping[state-5][inputInt];
					break;
				case 10:{
					String inner_table = "SELECT category_id, SUM(price) AS total From part p GROUP BY category_id";
					rs = stmt.executeQuery("SELECT category_id, c.name, total FROM ("+inner_table+") LEFT JOIN category c ON c.cid = category_id ORDER BY total");
					displayResult(itemName[6], numberOfVal[6], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					
					break;
				}
				case 11:{
					String inner_table = "SELECT s.sid AS ssid, s.name AS sname, p.price AS pprice FROM transaction t LEFT JOIN part p ON p.pid = t.pid LEFT JOIN salesperson s ON s.sid = t.sid";
					String sql_statement = "SELECT ssid, sname, SUM(pprice) as total FROM ("+inner_table+") GROUP BY ssid, sname ORDER by total";
					rs = stmt.executeQuery(sql_statement);
					displayResult(itemName[7], numberOfVal[7], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					
					break;
				}
				case 12:
					System.out.print("Type in the Search Keyword: ");
					inputStr = sc.next();
					rs = stmt.executeQuery("SELECT * FROM part p WHERE p.available_quantity > 0 AND p.category_id = (SELECT c.cid FROM category c WHERE c.name = '" + inputStr + "')");
					displayResult(itemName[3], numberOfVal[3], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				case 13:
					System.out.print("Type in the Search Keyword: ");
					inputStr = sc.next();
					rs = stmt.executeQuery("SELECT * FROM part p WHERE p.available_quantity > 0 AND p.manufacturer_id = (SELECT m.mid FROM manufacturer m WHERE m.name = '" + inputStr + "')");
					displayResult(itemName[3], numberOfVal[3], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				case 14:
					System.out.print("Type in the Search Keyword: ");
					inputStr = sc.next();
					rs = stmt.executeQuery("SELECT * FROM part p WHERE p.available_quantity > 0 AND p.name = '" + inputStr + "'");
					displayResult(itemName[3], numberOfVal[3], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				case 15:
					System.out.print("Type in the Search Key ID: ");
					inputInt = sc.nextInt();
					rs = stmt.executeQuery("SELECT * FROM transaction t WHERE t.sid = " + inputInt);
					displayResult(itemName[5], numberOfVal[5], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				case 16:
					System.out.print("Type in the Search Key ID: ");
					inputInt = sc.nextInt();
					rs = stmt.executeQuery("SELECT * FROM transaction t WHERE t.pid = " + inputInt);
					displayResult(itemName[5], numberOfVal[5], rs);
					System.out.print("End of Query Result\n");
					state = 0;
					break;
				}
				
				System.out.println();
			}
		
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception x) {
			System.out.println("Unable to load the driver class!"); 
			x.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String [] args) {
		SalesSystem ss = new SalesSystem();
		ss.startMenu();
	}
}