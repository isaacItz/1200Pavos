package vista;

import static modelo.Utileria.escribir;
import static modelo.Utileria.leerInt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;

import modelo.Conexion;

public class VistaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JMenuBar barra;
	private JMenu ventas;
	private JMenu inventario;
	private JMenu apartados;
	private JMenu caja;
	private JMenuItem realizarVenta;
	private JMenuItem busquedaInventario;
	private JMenuItem altaProducto;
	private JMenuItem bajaProducto;
	private JMenuItem modificacion;
	private JMenuItem apartar;
	private JMenuItem consultarApartado;
	private JMenuItem cancelarApartado;
	private JMenuItem depositar;
	private JMenuItem corteCaja;
	private JMenuItem resumen;
	private JMenuItem resumenVentas;
	private Fondo contentPane;
	private Conexion conexion;
	private JMenuItem mntmResurtirProducto;

	public VistaPrincipal() {

		conexion = new Conexion();
		if (!conexion.generarConexion()) {
			escribir("Ha Ocurrido un Error Con la BD");
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\recursos\\icono.png"));
		getContentPane().setBackground(Color.BLACK);
		getContentPane().setLayout(null);
		setTitle("Chacharas & Rock");
		contentPane = new Fondo("logo.png");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setSize(900, 700);
		initComponents();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(900, 700));
		setLocationRelativeTo(null);
		// setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);

	}

	private void initComponents() {
		barra = new JMenuBar();
		ventas = new JMenu("Ventas");
		apartados = new JMenu("Apartados");
		inventario = new JMenu("Inventario");
		caja = new JMenu("caja");
		// VENTAS
		realizarVenta = new JMenuItem("Realizar Venta");
		ventas.add(realizarVenta);
		realizarVenta.addActionListener(new OyenteRealizarVenta());
		resumenVentas = new JMenuItem("Resumen de Ventas");
		ventas.add(resumenVentas);
		resumenVentas.addActionListener(new OyenteResumenDeVentas());
		// INVENTARIO
		altaProducto = new JMenuItem("Registrar Producto");
		inventario.add(altaProducto);
		altaProducto.addActionListener(new OyenteRegistrarProducto());

		mntmResurtirProducto = new JMenuItem("Resurtir Producto");
		inventario.add(mntmResurtirProducto);
		bajaProducto = new JMenuItem("Eliminar Producto del Inventario");
		inventario.add(bajaProducto);
		bajaProducto.addActionListener(new OyenteEliminarProducto());
		busquedaInventario = new JMenuItem("Buscar Producto");
		inventario.add(busquedaInventario);
		busquedaInventario.addActionListener(new OyenteConsultarProducto());
		modificacion = new JMenuItem("Modificar Producto");
		inventario.add(modificacion);
		modificacion.addActionListener(new OyenteModificarProducto());
		// APARTADOS
		apartar = new JMenuItem("Apartar Producto");
		apartados.add(apartar);
		apartar.addActionListener(new OyenteApartarProducto());
		consultarApartado = new JMenuItem("Consultar Apartados");
		apartados.add(consultarApartado);
		consultarApartado.addActionListener(new OyenteConsultarApartado());
		cancelarApartado = new JMenuItem("Cancelar Apartado");
		apartados.add(cancelarApartado);
		cancelarApartado.addActionListener(new OyenteEliminarApartado());
		// CAJA
		depositar = new JMenuItem("Depositar");
		caja.add(depositar);
		depositar.addActionListener(new OyenteAgregarDinero());
		corteCaja = new JMenuItem("Realizar Corte");
		caja.add(corteCaja);
		corteCaja.addActionListener(new OyenteRetirarDinero());
		resumen = new JMenuItem("Resumen");
		caja.add(resumen);
		resumen.addActionListener(new OyenteResumenCaja());
		barra.add(ventas);
		barra.add(apartados);
		barra.add(inventario);
		barra.add(caja);
		setJMenuBar(barra);

	}

	public static void main(String[] args) {
		new VistaPrincipal();
	}

	// OYENTES VENTAS

	private class OyenteRealizarVenta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
			new Venta(conexion).setVisible(true);
		}

	}

	private class OyenteResumenDeVentas implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			new ResumenVentas(conexion);
		}

	}

	// OYENTES APARTADOS

	private class OyenteApartarProducto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteConsultarApartado implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteEliminarApartado implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	// OYENTES INVENTARIO

	private class OyenteRegistrarProducto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteEliminarProducto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteConsultarProducto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteModificarProducto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	// OYENTES CAJA

	private class OyenteAgregarDinero implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int cantidad = leerInt("Ingresa el Monto A Depositar");
			try {
				ResultSet rs = conexion.Consulta("Select max(id_cambio) from caja");
				rs.next();
				int id = rs.getInt(1);
				int totalActual = 0;
				rs = conexion.Consulta("Select total from caja where id_cambio = " + id);
				rs.next();
				totalActual = rs.getInt(1) + cantidad;
				String consulta = "UPDATE `caja` SET `total`=" + totalActual + ",`fecha inicio`='" + LocalDateTime.now()
						+ "' WHERE id_cambio = " + id;

				PreparedStatement ps = conexion.getPreparedStatement(consulta);
				ps.executeUpdate();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	private class OyenteRetirarDinero implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class OyenteResumenCaja implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			new ResumenCaja(conexion).setVisible(true);
		}

	}

	// private class Oyente implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	//
	// }
	//
	// }

}
