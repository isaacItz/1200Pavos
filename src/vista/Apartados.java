package vista;

import static modelo.Utileria.escribir;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modelo.Conexion;
import modelo.Utileria;

public class Apartados extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextField textField;
	private JComboBox<Object> comboBox;
	private Conexion conexion;
	private String nombreTabla;
	private Object[][] datos;
	private Object[] columnasTabla = null;
	private JScrollPane scrollPane;

	public Apartados(Conexion conexion) {
		this.conexion = conexion;
		nombreTabla = "apartados";
		getContentPane().setBackground(Color.BLACK);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Apartados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 16, 864, 534);
		getContentPane().add(panel);
		panel.setLayout(null);

		JButton btnAgregarAlCarrito = new JButton("Abonar");
		btnAgregarAlCarrito.addActionListener(new oyenteVenta());
		btnAgregarAlCarrito.setBounds(10, 484, 838, 39);
		panel.add(btnAgregarAlCarrito);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Buscar Producto Apartado por:", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_2.setBounds(177, 21, 503, 43);
		panel.add(panel_2);
		panel_2.setLayout(null);

		textField = new JTextField();
		textField.setEnabled(false);
		comboBox = new JComboBox<>();
		comboBox.setBounds(6, 16, 214, 20);
		comboBox.addItem("Elija un Criterio");
		iniciarTabla();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboBox.getSelectedIndex() > 0) {
					textField.setEnabled(true);
				} else
					textField.setEnabled(false);
			}
		});
		panel_2.add(comboBox);

		textField = new JTextField();
		textField.setEnabled(false);
		textField.setBounds(230, 16, 262, 20);
		panel_2.add(textField);
		textField.setColumns(10);

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 75, 844, 398);
		panel.add(scrollPane);

		scrollPane.setViewportView(table);
		setModal(true);
		crearEventoSalida();
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\recursos\\icono.png"));
		setSize(900, 600);
		setLocationRelativeTo(null);
		setTitle("Apartados");

		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				llenarTabla();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				llenarTabla();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

	}

	@SuppressWarnings("serial")
	private void llenarTabla() {
		ResultSet rs;
		try {

			String operador = " LIKE '" + textField.getText().concat("%");

			String aux = comboBox.getSelectedIndex() == 0 ? "id_apartado" : comboBox.getSelectedItem().toString();

			rs = conexion.Consulta("Select * from " + nombreTabla + " where `" + aux.concat("` ") + operador + "' ");
			datos = conexion.getDatosTabla(rs);
			table = new JTable(datos, columnasTabla) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setToolTipText("tabla");
			// table.setUpdateSelectionOnSort(false);
			scrollPane.setViewportView(table);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private Object[] obtenerDatosFila(int fila) {
		Object[] ar = new Object[columnasTabla.length];
		for (int i = 0; i < columnasTabla.length; i++) {
			ar[i] = table.getValueAt(fila, i);
		}
		return ar;
	}

	private class oyenteVenta implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			int fila = table.getSelectedRow();

			if (fila > -1) {
				Object[] f = obtenerDatosFila(fila);
				int idApartado = Integer.parseInt(String.valueOf(f[0]));
				// int codigoProducto = Integer.parseInt(String.valueOf(f[1]));
				// int cantidad = Integer.parseInt(String.valueOf(f[2]));
				double precioTotal = Double.parseDouble(String.valueOf(f[5]));
				double pagado = Double.parseDouble(String.valueOf(f[6]));
				double restante = Double.parseDouble(String.valueOf(f[7]));

				Double abono = 0.0;
				do {
					abono = Utileria.leerDouble("Ingresa el Abono");
					if (abono != null) {
						if (abono >= precioTotal) {
							escribir("Total de la deuda: " + precioTotal + "\nDinero Abonado: " + abono + "\ncambio: "
									+ (abono - precioTotal));
							String consulta = "Select max(id_cambio) from caja";
							ResultSet rs;
							try {
								rs = conexion.Consulta(consulta);
								rs.next();
								int maximo = rs.getInt(1);
								double totalCaja = (double) conexion.getCampo("caja", "total", "id_cambio", maximo);
								double c = totalCaja + (precioTotal - pagado);
								consulta = "update caja set total = " + c + " where id_cambio = " + maximo;
								PreparedStatement ps = conexion.getPreparedStatement(consulta);
								ps.executeUpdate();
								consulta = "DELETE FROM `apartados` WHERE `Id_apartado` = " + idApartado;
								ps = conexion.getPreparedStatement(consulta);
								ps.executeUpdate();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}

						} else {
							escribir("Total de la deuda: " + precioTotal + "\nDinero Abonado: " + abono + "\nFaltante: "
									+ (precioTotal - (abono + pagado)));

							String consulta = "Select max(id_cambio) from caja";
							ResultSet rs;
							try {
								rs = conexion.Consulta(consulta);
								rs.next();
								int maximo = rs.getInt(1);
								double totalCaja = (double) conexion.getCampo("caja", "total", "id_cambio", maximo);
								double c = totalCaja + abono;
								consulta = "update caja set total = " + c + " where id_cambio = " + maximo;
								PreparedStatement ps = conexion.getPreparedStatement(consulta);
								ps.executeUpdate();
								consulta = "update apartados set restante = " + (precioTotal - (abono + pagado))
										+ ", pagado = " + (pagado + abono) + " where id_apartado = " + idApartado;
								ps = conexion.getPreparedStatement(consulta);
								ps.executeUpdate();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
					} else
						break;
				} while (abono < 1);

			} else {
				escribir("No se ha Seleccionado Ningun Producto");
			}
			llenarTabla();

		}

	}

	@SuppressWarnings("serial")
	private void iniciarTabla() {
		try {
			for (Object dato : conexion.getCamposTabla("apartados"))
				comboBox.addItem(dato);
			columnasTabla = conexion.getCamposTabla("apartados");
			datos = conexion.getDatosTabla(conexion.Consulta("Select * from apartados "));
			table = new JTable(datos, columnasTabla) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setToolTipText("tabla");
			// table.setUpdateSelectionOnSort(false);
			if (scrollPane != null)
				scrollPane.setViewportView(table);
		} catch (SQLException e) {
			System.err.println("Ocurrio un Error al cargar los criterios" + e.getMessage());
		}
	}

	private void crearEventoSalida() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				new VistaPrincipal();
			}
		});
	}
}
