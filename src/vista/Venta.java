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

public class Venta extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextField textField;
	private JComboBox<Object> comboBox;
	private Conexion conexion;
	private String nombreTabla;
	private Object[][] datos;
	private Object[] columnasTabla = null;
	private JScrollPane scrollPane;

	public Venta(Conexion conexion) {
		this.conexion = conexion;
		nombreTabla = "productos";
		getContentPane().setBackground(Color.BLACK);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Ventas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 16, 864, 534);
		getContentPane().add(panel);
		panel.setLayout(null);

		JButton btnAgregarAlCarrito = new JButton("Vender");
		btnAgregarAlCarrito.addActionListener(new oyenteVenta());
		btnAgregarAlCarrito.setBounds(10, 484, 838, 39);
		panel.add(btnAgregarAlCarrito);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(
				new TitledBorder(null, "Buscar Producto por:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		setTitle("Ventas");

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

			String aux = comboBox.getSelectedIndex() == 0 ? "" : comboBox.getSelectedItem().toString();

			rs = conexion.Consulta(
					"Select * from " + nombreTabla + " where `" + aux.concat("` ") + operador + "' and existencia > 0");
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
				int existencia = Integer.parseInt(String.valueOf(f[4]));
				String[] tipos = new String[existencia];
				for (int i = 0; i < tipos.length; i++) {
					tipos[i] = String.valueOf(i + 1);
				}
				Object num = JOptionPane.showInputDialog(null, "Cuantos " + f[1] + " Desea Vender", "Cantidad",
						JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

				int cant = Integer.parseInt(String.valueOf(num));

				if (!(cant > existencia)) {
					Object valor = table.getValueAt(fila, 0);
					if (conexion.hayExistencia(Integer.parseInt(String.valueOf(valor)))) {

						double precio = Double.parseDouble(String.valueOf(f[3]));
						double total = precio * cant;

						escribir("Precio Unitario: " + precio + "\nTotal: " + total);

						String venta = "UPDATE `productos` SET `existencia` = '" + (existencia - cant) + "'"
								+ " WHERE `productos`.`CodigoDeBarras` = " + f[0];
						try {
							PreparedStatement ps = conexion.getPreparedStatement(venta);
							ps.executeUpdate();
							iniciarTabla();
							venta = "INSERT INTO `ventas`(`clave producto`, `cantidad`, `fecha venta`, `total`) VALUES (?,?,?,?)";
							ps = conexion.getPreparedStatement(venta);
							ps.setString(1, String.valueOf(f[0]));
							ps.setInt(2, cant);
							ps.setObject(3, LocalDateTime.now());
							ps.setDouble(4, total);
							ps.executeUpdate();

							conexion.venderProducto(precio);

							escribir("Vendido!");
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

					} else
						escribir("No Hay Existencias");
				} else
					escribir("Solo hay Disponibles " + existencia);

			} else {
				escribir("No se ha Seleccionado Ningun Producto");
			}

		}

	}

	private void iniciarTabla() {
		try {
			for (Object dato : conexion.getCamposTabla("productos"))
				comboBox.addItem(dato);
			columnasTabla = conexion.getCamposTabla("productos");
			datos = conexion.getDatosTabla(conexion.Consulta("Select * from productos where existencia > 0"));
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
