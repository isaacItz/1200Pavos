package vista;

import static modelo.Utileria.esNumero;
import static modelo.Utileria.esNumeroDecimal;
import static modelo.Utileria.escribir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import modelo.Conexion;

public class RegistroProducto extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtcodigo;
	private JTextField nombreTxt;
	private JTextField precioTxt;
	private JTextField cantidadTxt;
	private JTextField tipoTxt;
	private Conexion conexion;
	private JTextArea descripcion;

	public RegistroProducto(Conexion conexion) {
		this.conexion = conexion;
		setTitle("Registro de Productos C&R");
		setResizable(false);
		setModal(true);
		setSize(393, 419);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.BLACK);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JSeparator separator = new JSeparator();
			separator.setForeground(Color.GREEN);
			separator.setBackground(Color.GREEN);
			separator.setBounds(440, 239, -432, 2);
			contentPanel.add(separator);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(
					new TitledBorder(null, "Registro de Producto", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.setBounds(10, 11, 367, 336);
			contentPanel.add(panel);
			panel.setLayout(null);
			{
				JLabel lblCodigoDeProducto = new JLabel("Codigo de Producto:");
				lblCodigoDeProducto.setBounds(10, 27, 100, 14);
				panel.add(lblCodigoDeProducto);
			}

			txtcodigo = new JTextField();
			txtcodigo.setEditable(false);
			txtcodigo.setText("AUTOGENERADO");
			txtcodigo.setBounds(141, 24, 108, 20);
			panel.add(txtcodigo);
			txtcodigo.setColumns(10);
			{
				JLabel lblNombre = new JLabel("Nombre:");
				lblNombre.setBounds(10, 58, 100, 14);
				panel.add(lblNombre);
			}
			{
				JLabel lblPrecio = new JLabel("Precio");
				lblPrecio.setBounds(10, 99, 100, 14);
				panel.add(lblPrecio);
			}
			{
				JLabel lblCantidad = new JLabel("Cantidad:");
				lblCantidad.setBounds(10, 135, 100, 14);
				panel.add(lblCantidad);
			}
			{
				JLabel lblTipo = new JLabel("Tipo:");
				lblTipo.setBounds(10, 173, 100, 14);
				panel.add(lblTipo);
			}
			{
				JLabel lblDescripcion = new JLabel("Descripcion:");
				lblDescripcion.setBounds(10, 217, 100, 14);
				panel.add(lblDescripcion);
			}
			{
				nombreTxt = new JTextField();
				nombreTxt.setBounds(151, 55, 188, 20);
				panel.add(nombreTxt);
				nombreTxt.setColumns(10);
			}
			{
				precioTxt = new JTextField();
				precioTxt.setColumns(10);
				precioTxt.setBounds(151, 96, 188, 20);
				panel.add(precioTxt);
			}
			{
				cantidadTxt = new JTextField();
				cantidadTxt.setColumns(10);
				cantidadTxt.setBounds(151, 132, 188, 20);
				panel.add(cantidadTxt);
			}
			{
				tipoTxt = new JTextField();
				tipoTxt.setColumns(10);
				tipoTxt.setBounds(151, 170, 188, 20);
				panel.add(tipoTxt);
			}

			JCheckBox chckbxPersonalizado = new JCheckBox("Personalizado");
			chckbxPersonalizado.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					txtcodigo.setEditable(!txtcodigo.isEditable());
					if (txtcodigo.isEditable())
						txtcodigo.setText("");
					else
						txtcodigo.setText("AUTOGENERADO");
				}
			});
			chckbxPersonalizado.setBounds(255, 23, 106, 23);
			panel.add(chckbxPersonalizado);

			descripcion = new JTextArea();
			descripcion.setBounds(151, 212, 188, 113);
			panel.add(descripcion);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.BLACK);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new OyenteOk());
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		txtcodigo.setText(String.valueOf(conexion.getAutoIncrement("productos")));
	}

	private boolean validar() {
		if (txtcodigo.isEditable() && !esNumero(txtcodigo.getText())) {
			escribir("Escribe un Codigo Valido");
			return false;
		}
		if (txtcodigo.isEditable() && esNumero(txtcodigo.getText())) {
			if (conexion.existe("productos", "codigodebarras", txtcodigo.getText())) {
				escribir("el Codigo ya esta registrado");
				txtcodigo.setText("");
				return false;
			}

		}

		if (nombreTxt.getText().equals("")) {
			escribir("Escribe un Nombre Valido");
			return false;
		}

		if (precioTxt.getText().equals("") || !esNumeroDecimal(precioTxt.getText())) {
			escribir("Escribe un Precio Valido");
			return false;
		}
		if ((cantidadTxt.getText().equals("") || !esNumero(cantidadTxt.getText()))) {
			escribir("Escribe una cantidad valida");
			return false;
		}
		if (!(Integer.parseInt(cantidadTxt.getText()) > 0)) {
			escribir("Escribe una Cantidad Mayor a 0");
			return false;
		}

		if (tipoTxt.getText().isEmpty()) {
			escribir("Escribe un Tipo Valido");
			return false;
		}

		return true;

	}

	private class OyenteOk implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (validar()) {
				String consulta = "";
				if (!txtcodigo.isEditable())
					consulta = "Insert into `productos` (`nombre`, `tipo`, `precio`, `descripcion`, `existencia`) "
							+ "VALUES (?,?,?,?,?)";
				else
					consulta = "Insert into `productos` (`nombre`, `tipo`, `precio`, `descripcion`, `existencia`, `codigodebarras`) "
							+ "VALUES (?,?,?,?,?,?)";

				try {
					PreparedStatement ps = conexion.getPreparedStatement(consulta);
					ps.setString(1, nombreTxt.getText());
					ps.setString(2, tipoTxt.getText());
					ps.setString(3, precioTxt.getText());
					ps.setString(4, descripcion.getText());
					ps.setString(5, cantidadTxt.getText());
					if (txtcodigo.isEditable())
						ps.setString(6, txtcodigo.getText());
					ps.executeUpdate();
				} catch (SQLException e3) {
					e3.printStackTrace();
				}

				escribir("Registro con Exito");
				dispose();
			}
		}

	}

}
