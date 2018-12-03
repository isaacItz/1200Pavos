package vista;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import modelo.Conexion;

public class VentanaBusquedaGeneral extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private Conexion conexion;
	private JScrollPane scrollPane;
	private Object[][] datos;
	private Object[] columnasTabla = null;
	private String nombreTabla;
	private String criterio;
	private boolean ordenar;
	private String extra;

	public VentanaBusquedaGeneral(Conexion conexion, String nombreTabla, String criterio) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaBusquedaGeneral.class.getResource("/fondo.jpg")));
		this.criterio = criterio;
		setModal(true);
		this.nombreTabla = nombreTabla;
		this.conexion = conexion;
		extra = "";

		setTitle("CASA RAIZ");
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
		scrollPane.setBounds(10, 101, 674, 359);
		contentPane.add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Dato a Buscar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 28, 674, 52);
		contentPane.add(panel);
		panel.setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 22, 498, 20);
		panel.add(textField);
		textField.setToolTipText("dato a buscar");
		textField.setColumns(10);

		JButton btnBuscar = new JButton("");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extra = "";
				llenarTabla();
			}
		});
		btnBuscar.setIcon(new ImageIcon(VentanaBusquedaGeneral.class.getResource("/vista/ImagenBusqueda.PNG")));
		btnBuscar.setBounds(518, 22, 39, 23);
		panel.add(btnBuscar);

		JButton btnAscdesc = new JButton("ASC/DESC");
		btnAscdesc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ordenar = !ordenar;
				extra = " order by " + criterio + (ordenar ? " asc" : " desc");
				llenarTabla();
			}
		});
		btnAscdesc.setBounds(575, 21, 89, 23);
		panel.add(btnAscdesc);
		llenarTabla();
		setVisible(true);
	}

	private void llenarTabla() {
		ResultSet rs;
		try {
			String consulta = "Select *from " + nombreTabla + " where `" + criterio + "` LIKE '" + textField.getText()
					+ "%'" + extra;
			rs = conexion.Consulta(consulta);
			datos = conexion.getDatosTabla(rs);
			columnasTabla = conexion.getCamposTabla("productos");
			table = new JTable(datos, columnasTabla);
			table.setToolTipText("tabla");
			// table.setUpdateSelectionOnSort(false);
			scrollPane.setViewportView(table);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
