package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Conexion {

	private Connection conexion;
	private Statement consulta;

	public boolean generarConexion() {
		try {
			conexion = DriverManager.getConnection("jdbc:mysql://127.4.0.1/chacharas", "root", "");
			consulta = conexion.createStatement();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Object getCampo(String tabla, String campo, String criterio, Object dato) {
		String consulta = "Select " + campo + " From " + tabla + " where " + criterio + "= '" + dato + "'";
		ResultSet rs;
		try {
			rs = (ResultSet) Consulta(consulta);

			if (rs.next())
				return rs.getObject(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[] getArregloModificaion(ResultSet rs) {

		ArrayList<Object> lista = new ArrayList<>();
		Object[] cols = getNombreCiertasColumnas(rs);
		try {
			rs.next();
			for (int i = 0; i < cols.length; i++) {
				String renglon = cols[i].toString() + ":  " + rs.getString(i + 1) + " ";
				lista.add(renglon);
			}
			return lista.toArray();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return null;
		}

	}

	public int getAutoIncrement(String tabla) {
		try {
			ResultSet rs = Consulta(
					"SELECT `AUTO_INCREMENT`	FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'chacharas' AND   TABLE_NAME   = '"
							+ tabla + "'");
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void venderProducto(double precio) throws SQLException {
		String consulta = "Select max(id_cambio) from caja";
		ResultSet rs = Consulta(consulta);
		rs.next();
		int id = rs.getInt(1);
		rs = Consulta("Select total from caja where id_cambio = " + id);
		rs.next();
		double totalActual = rs.getDouble(1);
		consulta = "update caja set total = " + (precio + totalActual) + " where id_cambio = " + id;

		PreparedStatement ps = getPreparedStatement(consulta);
		ps.executeUpdate();

	}

	public Object[] getCamposTabla(String tablaName) throws SQLException {

		String consulta = "DESCRIBE " + tablaName;
		ArrayList<String> ar = new ArrayList<String>();
		ResultSet rs = (ResultSet) Consulta(consulta);
		while (rs.next()) {
			ar.add((String) rs.getString(1));
		}

		return ar.toArray();
	}

	public int getIDProductos() {
		String consulta = "SELECT max(id_producto) from productos";

		ResultSet rs;
		try {
			rs = (ResultSet) Consulta(consulta);
			rs.next();
			return rs.getInt(1) + 1;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public Object[] getNombreCiertasColumnas(ResultSet rs) {
		try {
			ResultSetMetaData metaDatos = rs.getMetaData();
			Object[] ar = new Object[metaDatos.getColumnCount()];
			for (int i = 0; i < ar.length; i++) {
				ar[i] = metaDatos.getColumnLabel(i + 1);
			}
			return ar;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object[][] getDatosTabla(ResultSet rs) throws SQLException {

		ResultSetMetaData metaDatos = rs.getMetaData();
		int numColumnas = metaDatos.getColumnCount();

		Object[] fila = new Object[numColumnas];

		ArrayList<Object[]> arreglo = new ArrayList<>();

		while (rs.next()) {
			for (int i = 0; i < fila.length; i++) {
				fila[i] = rs.getString(i + 1);
			}
			arreglo.add(fila);
			fila = new Object[numColumnas];
		}

		Object[][] are = new Object[arreglo.size()][numColumnas];

		for (int i = 0; i < arreglo.size(); i++) {
			for (int j = 0; j < numColumnas; j++) {
				are[i][j] = arreglo.get(i)[j];
			}
		}

		return are;
	}

	public ResultSet Consulta(String c) throws SQLException {
		return consulta.executeQuery(c);
	}

	public void cerrarConexion() throws SQLException {
		conexion.close();
	}

	public boolean agregarProductoExistente(int cant, String clave) {
		int cantidad = 0;
		String consulta = "Select existencias from productos where id_producto = '" + clave + "'";
		ResultSet rs;
		try {
			rs = (ResultSet) Consulta(consulta);
			rs.next();
			cantidad = rs.getInt(1);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String consulta1 = "UPDATE `productos` SET `existencias` = '" + (cant + cantidad)
				+ "' WHERE `productos`.`id_producto` = '" + clave + "'";

		try {
			PreparedStatement ps = getPreparedStatement(consulta1);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		}

	}

	public boolean hayExistencia(int dato) {
		String consulta = "Select existencia From productos where CodigoDeBarras = '" + dato + "' and existencia > 0";
		ResultSet rs;
		try {
			rs = (ResultSet) Consulta(consulta);

			if (rs.next())
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean existe(String tabla, String colum, Object dato) {
		String consulta = "Select * From " + tabla + " where " + colum + "= '" + dato + "'";
		ResultSet rs;
		try {
			rs = (ResultSet) Consulta(consulta);

			if (rs.next())
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void realizarConsulta(String consulta) throws SQLException {

		PreparedStatement preparedStatement = conexion.prepareStatement(consulta);
		preparedStatement.executeUpdate();

	}

	public PreparedStatement getPreparedStatement(String consulta) throws SQLException {
		PreparedStatement preparedStatement = conexion.prepareStatement(consulta);
		return preparedStatement;
	}

}
