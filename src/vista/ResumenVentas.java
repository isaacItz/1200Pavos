package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import modelo.Conexion;

public class ResumenVentas extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private Conexion conexion;
	private JScrollPane scrollPane;
	private Object[][] datos;
	private Object[] columnasTabla;
	private JLabel lblElementosVendidos;
	private JLabel label;
	private JButton btnVentasDelDia;
	private JPanel panel;
	private JButton btnUltimaSemana;
	private JButton btnTodasLasVentas;

	public ResumenVentas(Conexion conexion) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ResumenVentas.class.getResource("/fondo.jpg")));

		setModal(true);
		this.conexion = conexion;
		setTitle("Chacharas R&R");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(700, 500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 106, 674, 328);
		contentPane.add(scrollPane);

		lblElementosVendidos = new JLabel("Productos Vendidos:");
		lblElementosVendidos.setForeground(Color.GREEN);
		lblElementosVendidos.setBackground(Color.GREEN);
		lblElementosVendidos.setFont(new Font("Times New Roman", Font.BOLD, 21));
		lblElementosVendidos.setBounds(10, 439, 197, 21);
		contentPane.add(lblElementosVendidos);

		label = new JLabel("0");
		label.setForeground(Color.GREEN);
		label.setFont(new Font("Times New Roman", Font.BOLD, 21));
		label.setBackground(Color.GREEN);
		label.setBounds(217, 439, 76, 21);
		contentPane.add(label);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Filtrar por:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 35, 674, 60);
		contentPane.add(panel);
		panel.setLayout(null);

		btnVentasDelDia = new JButton("Ventas del Dia Actual");
		btnVentasDelDia.addActionListener(new OyenteDiaActual());
		btnVentasDelDia.setBounds(6, 16, 149, 37);
		panel.add(btnVentasDelDia);

		btnUltimaSemana = new JButton("Ultima Semana");
		btnUltimaSemana.setBounds(165, 16, 157, 37);
		panel.add(btnUltimaSemana);

		btnTodasLasVentas = new JButton("Todas las ventas");
		btnTodasLasVentas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				initComponents(null);
			}
		});
		btnTodasLasVentas.setBounds(332, 16, 157, 37);
		panel.add(btnTodasLasVentas);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Dia Especifico", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(499, 10, 165, 43);
		panel.add(panel_1);
		panel_1.setLayout(null);

		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setBounds(6, 16, 149, 20);
		panel_1.add(dateChooser);
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if (dateChooser.getDate() != null)
					initComponents("where `fecha venta` like '"
							+ new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()) + "%'");
			}
		});

		initComponents(null);

		setVisible(true);
	}

	private class OyenteDiaActual implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String aux = "where `fecha venta` like '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%'";
			initComponents(aux);
		}

	}

	private void initComponents(String personalizado) {
		ResultSet rs;
		try {
			String consulta = ("Select productos.nombre,productos.CodigoDeBarras,"
					+ "	productos.precio as `precio unitario`,ventas.cantidad,ventas.`fecha venta`, ventas.total"
					+ "	from productos,ventas ");
			if (personalizado != null)
				consulta += (personalizado);

			rs = conexion.Consulta(consulta);
			datos = conexion.getDatosTabla(rs);
			columnasTabla = new Object[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < columnasTabla.length; i++)
				columnasTabla[i] = rs.getMetaData().getColumnLabel(i + 1);

			table = new JTable(datos, columnasTabla);
			table.setToolTipText("tabla");
			scrollPane.setViewportView(table);
			String aux = "Select sum(cantidad) from ventas ";
			if (personalizado != null)
				aux += (personalizado);
			rs = conexion.Consulta(aux);
			rs.next();
			int cant = rs.getInt(1);
			label.setText(String.valueOf(cant));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
